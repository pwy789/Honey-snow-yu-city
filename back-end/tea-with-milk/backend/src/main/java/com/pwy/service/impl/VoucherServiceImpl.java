package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.Result;
import com.pwy.entity.dto.VoucherSearchDto;
import com.pwy.entity.pojo.Voucher;
import com.pwy.enums.VoucherTypeEnum;
import com.pwy.mapper.VoucherMapper;
import com.pwy.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {
    @Transactional
    @Override
    public Result addVoucher(Voucher voucher) {
        Voucher v=new Voucher();
        v.setName(voucher.getName());
        v.setUrl(voucher.getUrl());
        v.setCoinNeed(voucher.getCoinNeed());
        v.setNumber(voucher.getNumber());
        if(voucher.getExpirationTime()!=null &&voucher.getEffectiveTime().isAfter(voucher.getExpirationTime())){
            return Result.error("截止日期不能小于生效日期!");
        }
        v.setEffectiveTime(voucher.getEffectiveTime());
        if(voucher.getExpirationTime()!=null){
            v.setExpirationTime(voucher.getExpirationTime());
        }
        v.setType(voucher.getType());
        //判断优惠券的类型
        Short type = voucher.getType();
        if(type.equals(VoucherTypeEnum.DIRECT_DISCOUNT_DISCOUNT.getCode())){
        //直减型优惠券,拿到扣减金额
            Integer deduct = voucher.getDeduct();
            v.setDeduct(deduct);
        }else if(type.equals(VoucherTypeEnum.THRESHOLD_BASED_DISCOUNT.getCode())){
            //满减型
            Integer threshold = voucher.getThreshold();
            Integer deduct = voucher.getDeduct();
            if(threshold<deduct){
                return Result.error("优惠门槛不能低于优惠金额!");
            }
            v.setThreshold(threshold);
            v.setDeduct(deduct);
        }else {
            //折扣型
            Double discountRate = voucher.getDiscountRate();
            if(discountRate<=0||discountRate>=1){
                return Result.error("折扣值必须在0,1之间");
            }
            v.setDiscountRate(discountRate);
        }
        save(v);
        return Result.success("新增成功");
    }
    @Transactional
    @Override
    public Result editVoucher(Voucher voucher) {
        LambdaUpdateWrapper<Voucher> luw=new LambdaUpdateWrapper<>();
        luw.set(Voucher::getType,voucher.getType());
        luw.set(Voucher::getName,voucher.getName());
        luw.set(Voucher::getCoinNeed,voucher.getCoinNeed());
        luw.set(Voucher::getNumber,voucher.getNumber());
        luw.set(Voucher::getEffectiveTime,voucher.getEffectiveTime());
        luw.set(Voucher::getUrl,voucher.getUrl());
        if(voucher.getExpirationTime()!=null&&voucher.getEffectiveTime().isAfter(voucher.getExpirationTime())){
            return Result.error("截止日期不能小于生效日期!");
        }
        if(voucher.getType().equals(VoucherTypeEnum.THRESHOLD_BASED_DISCOUNT.getCode())){
            //满减型
            luw.set(Voucher::getDiscountRate,null);
            luw.set(Voucher::getThreshold,voucher.getThreshold());
            luw.set(Voucher::getDeduct,voucher.getDeduct());
        }else if(voucher.getType().equals(VoucherTypeEnum.DIRECT_DISCOUNT_DISCOUNT.getCode())){
            //直减型
            luw.set(Voucher::getDiscountRate,null);
            luw.set(Voucher::getThreshold,null);
            luw.set(Voucher::getDeduct,voucher.getDeduct());
        }else {
            //折扣型
            luw.set(Voucher::getThreshold,null);
            luw.set(Voucher::getDeduct,null);
            luw.set(Voucher::getDiscountRate,voucher.getDiscountRate());
        }
      if(voucher.getExpirationTime()==null) luw.set(Voucher::getExpirationTime,null);
       luw.set(Voucher::getId,voucher.getId());
        update(luw);
        return Result.success("修改成功");
    }

    @Override
    public List<Voucher> search(VoucherSearchDto dto) {
         LambdaQueryWrapper<Voucher> lqw=new LambdaQueryWrapper<>();
         if(StringUtils.isNotBlank(dto.getName())){
             lqw.like(Voucher::getName,dto.getName());
         }
         if(StringUtils.isNotBlank(dto.getStartTime())){
             LocalDate startTime = LocalDate.parse(dto.getStartTime());
             lqw.ge(Voucher::getEffectiveTime,startTime);
         }
         if(StringUtils.isNotBlank(dto.getEndTime())){
             LocalDate endTime = LocalDate.parse(dto.getEndTime());
             lqw.le(Voucher::getExpirationTime,endTime);
         }
         if(StringUtils.isNotBlank(dto.getType())){
             lqw.eq(Voucher::getType,dto.getType());
         }
      return  list(lqw);
    }
}
