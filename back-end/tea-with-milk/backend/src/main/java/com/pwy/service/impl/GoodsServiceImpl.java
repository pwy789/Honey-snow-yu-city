package com.pwy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.Result;
import com.pwy.entity.dto.GoodsInfoDto;
import com.pwy.entity.dto.GoodsSearchDto;
import com.pwy.entity.dto.ImageOperate;
import com.pwy.entity.dto.SkuOperate;
import com.pwy.entity.pojo.Goods;
import com.pwy.entity.pojo.GoodsPictures;
import com.pwy.entity.pojo.GoodsSku;
import com.pwy.entity.pojo.OrdersGoods;
import com.pwy.entity.vo.GoodsVo;
import com.pwy.mapper.GoodsMapper;
import com.pwy.service.GoodsService;
import com.pwy.service.OrdersGoodsService;
import com.pwy.utils.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;



@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
   @Autowired
   private GoodsMapper goodsMapper;
   @Autowired
   private OrdersGoodsService ordersGoodsService;

    @Override
    public Page<Goods> getGoodsList(GoodsSearchDto dto) {
        Page<Goods> p=new Page<>(dto.getPage(),dto.getPageSize());
        LambdaQueryWrapper<Goods> lqw=new LambdaQueryWrapper<>();
        //若名字不为空
        if(!dto.getName().equals("")||dto.getName()!=null){
             lqw.like(Goods::getName,dto.getName());
        }
        //若价格(始)不为空
        if(dto.getPriceLow()!=null){
            lqw.ge(Goods::getPrice,dto.getPriceLow());
        }
        //若价格(末)不为空
        if(dto.getPriceHigh()!=null){
            lqw.le(Goods::getPrice,dto.getPriceHigh());
        }
        //状态
        if(dto.getStatus()!=2){
            lqw.eq(Goods::getStatus,dto.getStatus());
        }

        //商品分类
        if(!dto.getCategoryId().equals("0")){
            //说明是查询某一个分类,否则是查询全部
            lqw.eq(Goods::getCategoryId,dto.getCategoryId());
        }

       return page(p,lqw);
    }
   @Transactional
    @Override
    public GoodsVo getGoodsById(String id) {
        //根据id查询出商品信息
       Goods goods = getById(id);
       //查询出该商品包含的图
       List<GoodsPictures> images = goodsMapper.getGoodsImages(id);
       //查询出该商品包含的维度及其sku
       List<GoodsSku> skus = goodsMapper.getGoodsSkus(id);
       GoodsVo gv = BeanUtil.copyProperties(goods, GoodsVo.class);
       gv.setImages(images);
       gv.setSkus(skus);
       return gv;
   }
    @Transactional
    @Override
    public void updateGoods(GoodsInfoDto dto) {
        //首先更新商品的基本信息
        Goods goods = BeanUtil.copyProperties(dto, Goods.class);
        //将图集的第一个作为封面图
        goods.setCoverImage(dto.getImages().get(0).getUrl());
        //更新商品信息
        updateById(goods);
        //更新商品图片信息
        List<ImageOperate>  imageOperate = dto.getImageOperate();
        for (ImageOperate o : imageOperate) {
            if (o.getOperation().equals(GlobalConstant.GOODS_PICTURES_ADD)) {
                //执行插入操作
                goodsMapper.insertGoodsImages(new GoodsPictures(null,dto.getId(),o.getUrl()));
            }else if(o.getOperation().equals(GlobalConstant.GOODS_PICTURES_UPDATE)){
                //执行更新操作
                goodsMapper.updateGoodsImages(new GoodsPictures(o.getId(),dto.getId(),o.getUrl()));
            }else {
                //执行删除操作
                goodsMapper.deleteGoodsImages(o.getId());
            }
        }
        //更新商品sku信息
        List<SkuOperate> skuOperate = dto.getSkuOperate();
        for (SkuOperate o : skuOperate) {
             if (o.getOperate().equals(GlobalConstant.GOODS_SKUS_ADD)){
                 //进行新增操作
                 goodsMapper.insertGoodsSkus(new GoodsSku(null,dto.getId(),o.getSkuId(),o.getPrice()));
             }else if(o.getOperate().equals(GlobalConstant.GOODS_SKUS_DELETE)){
                 goodsMapper.deleteGoodsSkus(o.getSkuId());
             }else {
                 goodsMapper.updateGoodsSkus(o.getPrice(),o.getSkuId());
             }
        }
    }

    @Override
    public void saveCoverImage(GoodsPictures gp) {

        goodsMapper.saveImage(gp);
    }
   @Transactional
    @Override
    public void add(Goods goods) {
        save(goods);
        //将封面图存入goods_pictures表
        String coverImageUrl = goods.getCoverImage();
        String goodsId = goods.getId();
        GoodsPictures gp=new GoodsPictures(null,goodsId,coverImageUrl);
        saveCoverImage(gp);
    }
    @Transactional
    @Override
    public Result delGoodsByIds(List<String> ids) {
        for (String id : ids) {
            boolean isExists = ordersGoodsService.exists(Wrappers.<OrdersGoods>lambdaQuery().eq(OrdersGoods::getGoodsId, id));
            if(isExists){
                return Result.error("商品包含订单信息，不允许删除");
            }
        }
        // TODO: 2025-03-29  删除商品
          removeByIds(ids);
        // TODO: 2025-03-29 删除包含的图片
            goodsMapper.delImageByIds(ids);
        // TODO: 2025-03-29 删除包含的sku
         goodsMapper.delSkusByIds(ids);
         return Result.success("删除成功");
    }



}
