    package com.pwy.service.impl;
    import afu.org.checkerframework.checker.oigj.qual.O;
    import cn.hutool.core.bean.BeanUtil;
    import cn.hutool.core.bean.copier.CopyOptions;
    import cn.hutool.core.util.IdUtil;
    import cn.hutool.json.JSONUtil;
    import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
    import com.baomidou.mybatisplus.core.toolkit.StringUtils;
    import com.baomidou.mybatisplus.core.toolkit.Wrappers;
    import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
    import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
    import com.pwy.common.RedisKeyConstant;
    import com.pwy.common.Result;
    import com.pwy.entity.OrderMessage;
    import com.pwy.entity.dto.CartDto;
    import com.pwy.entity.dto.FeeDto;
    import com.pwy.entity.dto.OrderDto;
    import com.pwy.entity.dto.OrdersPageDto;
    import com.pwy.entity.pojo.*;
    import com.pwy.entity.vo.FeeVo;
    import com.pwy.entity.vo.OrdersVo;
    import com.pwy.entity.vo.OrdersVoList;
    import com.pwy.enums.OrderBuyWayEnums;
    import com.pwy.enums.OrdersStatusEnums;
    import com.pwy.enums.VoucherStatusEnum;
    import com.pwy.mapper.OrdersMapper;
    import com.pwy.service.*;
    import com.pwy.utils.OrderIsTimeoutUtils;
    import com.pwy.utils.RedisOrderNumberWorker;
    import com.pwy.utils.ThreadLocalUtils;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
    import org.springframework.aop.framework.AopContext;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.ClassPathResource;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.data.redis.core.script.DefaultRedisScript;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;


    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.*;

    @Slf4j
    @Service
    public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
        //地球半径
        private final double earthRadius=6371000;
        @Autowired
        private OrdersGoodsService ordersGoodsService;
        @Autowired
        private GoodsService goodsService;
        @Autowired
        private RedisOrderNumberWorker idWorker;
        @Autowired
        private StringRedisTemplate redisTemplate;
        @Autowired
        private OrdersMapper ordersMapper;
        @Autowired
        private UserVoucherService userVoucherService;
        @Autowired
        private CartService cartService;
        @Autowired
        private  AddressService addressService;
        @Autowired
        private ShopService shopService;
        @Autowired
        private VoucherService voucherService;


        private static final DefaultRedisScript<Long> LOCK_SCRIPT;

        private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;


        static {
            //加锁
            LOCK_SCRIPT=new DefaultRedisScript<>();
            LOCK_SCRIPT.setLocation(new ClassPathResource("GoodsBalance.lua"));
            LOCK_SCRIPT.setResultType(Long.class);
            //释放锁
            UNLOCK_SCRIPT=new DefaultRedisScript<>();
            UNLOCK_SCRIPT.setLocation(new ClassPathResource("Unlock.lua"));
            UNLOCK_SCRIPT.setResultType(Long.class);
        }

        @Override
        public Result Order(OrderDto dto) {
            // TODO: 2025-04-12 判断商品库存是否充足 分布式锁
             //存放所有的商品id,用作锁的key
            //这个set用于去重一样的商品id,因为商品的sku虽然不同，但是id是一样的
            Set<String> keysSet=new HashSet<>();
            List<CartDto> goods = dto.getGoods();
            for (CartDto g : goods) {
                keysSet.add(RedisKeyConstant.LOCK_ORDER_KEY+g.getGoodsId());
            }
            List<String> goodsKeys=new ArrayList<>(keysSet);
            //执行lua脚本
            while(true){
                try {
                    Long result = redisTemplate.execute(LOCK_SCRIPT, goodsKeys, ThreadLocalUtils.get().toString(), RedisKeyConstant.LOCK_ORDER_KEY_EXPIRE.toString());
                    if(result==1) break;
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            try {
                //执行到这里,说明已经获取到锁了
                OrdersService ordersService =(OrdersService) AopContext.currentProxy();
                String id = ordersService.deduceBalance(dto, goods);
                if(id==null){
                    return Result.error("商品库存不足");
                }
                return Result.success(id,"");
            } finally {
                unLock(goodsKeys);
            }


        }
        @Transactional
        public String deduceBalance(OrderDto dto, List<CartDto> goods) {

                for (CartDto g : goods) {
                    Integer balance = goodsService.getGoodsBalanceById(g.getGoodsId());
                    if(balance<1||balance<g.getCount()){

                        return null;
                    }
                }
                //执行到这里,说明库存是充足的
                //扣减库存
                Map<String,Integer> map=new HashMap<>();
                for (CartDto g : goods) {
                     map.merge(g.getGoodsId(),g.getCount(), Integer::sum);
                }
                goodsService.reduceBalanceById(map);

                //创建订单
                String id = createOrder(dto);
                //保存订单包含的商品
                List<OrdersGoods> ogs=new ArrayList<>();
                for (CartDto g : dto.getGoods()) {
                    String cartId = g.getId();
                    OrdersGoods og=new OrdersGoods();
                    BeanUtil.copyProperties(g,og, CopyOptions.create().setIgnoreProperties("id"));
                    og.setOrdersId(id);
                    og.setCartId(cartId);
                    ogs.add(og);
                }
                ordersGoodsService.saveOrdersGoods(ogs);
                //返回id
                return id;

        }

        @Override
        public OrdersVo getOrderById(String id) {
            //获取订单信息
            Orders order = getById(id);

            //根据id获取该订单所包含的商品
            List<OrdersGoods> ogs = ordersMapper.getOrderGoodsById(id);
            //如果使用了优惠券，则查询优惠券数据
            String  userVoucherId = order.getUserVoucherId();
            OrdersVo ov=new OrdersVo();
            if (StringUtils.isNotBlank(userVoucherId)) {
                UserVoucher userVoucher = userVoucherService.getById(userVoucherId);
                String voucherId = userVoucher.getVoucherId();
                Voucher voucher = voucherService.getById(voucherId);
                ov.setVoucher(voucher);
            }
            ov.setOrders(order);
            ov.setOgs(ogs);
            return ov;


        }
        //修改订单状态
        @Override
        public void setOrderStatus(String orderId,Short status) {
          LambdaUpdateWrapper<Orders> luw=new LambdaUpdateWrapper<>();
          luw.set(Orders::getStatus,status).eq(Orders::getId,orderId);
            update(luw);
        }

        @Override
        public OrdersVoList getOrdersByStatus(OrdersPageDto pageDto) {
            //这个集合用于返回结果
            OrdersVoList result=new OrdersVoList();
            List<OrdersVo> ovs=new ArrayList<>();
            Page<Orders> p=new Page<>(pageDto.getPage(),pageDto.getPageSize());
            String userId = ThreadLocalUtils.get().toString();
            LambdaUpdateWrapper<Orders> lqw=new LambdaUpdateWrapper<>();
            lqw.eq(Orders::getUserId,userId);
            lqw.in(Orders::getStatus,pageDto.getStatus());
            lqw.orderByDesc(Orders::getCreateTime);
            Page<Orders> resultSet = page(p, lqw);
            List<Orders> orders = resultSet.getRecords();
            for (Orders order : orders) {
                String id = order.getId();
                //该订单的所有商品
                List<OrdersGoods> ogs = ordersGoodsService.getOrdersGoodsByOrderId(id);
                OrdersVo ov=new OrdersVo();
                ov.setOrders(order);
                ov.setOgs(ogs);
                ovs.add(ov);
            }
            //返回结果以及结果的总条数
            result.setOvs(ovs);
            result.setCount(resultSet.getTotal());
        return result;

        }

        @Override
        public void confirmPay(String id) {
            // TODO: 2025-04-17 修改状态为制作中,并生成订单号给用户
            //首先查询出该订单
            Orders orders = getById(id);
            orders.setStatus(OrdersStatusEnums.IN_THE_MAKING.getCode());
            orders.setOrderTime(LocalDateTime.now());
            //生成订单号
            String orderNum;
            long OrderId = idWorker.nextId("order");
           if(StringUtils.isNotBlank(orders.getDeliveryFee())) {
               //外送
               orderNum="D"+OrderId;
           }else {
               //自提
               orderNum="P"+OrderId;
           }
            orders.setOrderId(orderNum);
            updateById(orders);

            // TODO 清除缓存数据
               OrderIsTimeoutUtils otu=new OrderIsTimeoutUtils();
               otu.setOrderId(id);
               otu.setCreateTime(orders.getCreateTime());
               redisTemplate.opsForList().remove(RedisKeyConstant.ORDER_IS_TIMEOUT_KEY,1,JSONUtil.toJsonStr(otu));
            // TODO: 2025-04-17 如果有优惠券，则需要修改优惠券状态
            String userVoucherId = orders.getUserVoucherId();
            if(StringUtils.isNotBlank(userVoucherId)){
                userVoucherService.changeVoucherStatusById(userVoucherId, VoucherStatusEnum.Used,LocalDateTime.now());
            }
            // TODO: 2025-04-17 如果是购物车的数据，则需要清空购物车
            List<OrdersGoods> ogs = ordersGoodsService.getOrdersGoodsByOrderId(id);
            //这些数据里面只要查询第一个是否有cartId就行了
            if(StringUtils.isNotBlank(ogs.get(0).getCartId())){
                List<String> cartIds=new ArrayList<>();
                for (OrdersGoods og : ogs) {
                    cartIds.add(og.getCartId());
                }
                //清除购物车数据
                cartService.remove(Wrappers.<Cart>lambdaQuery().in(Cart::getId,cartIds));
            }
            // TODO: 2025-04-17 将订单封装成消息推送到消息队列中
            //封装消息,主要是存储订单的一些基本信息
            OrderMessage message=new OrderMessage();
            message.setShopId(orders.getShopId());
            message.setId(orders.getId());
            redisTemplate.opsForStream().add(RedisKeyConstant.ORDER_MESSAGE_QUEUE,BeanUtil.beanToMap(message));

        }

        @Override
        public FeeVo createFee(FeeDto feeDto) {
            FeeVo feeVo=new FeeVo();
            Address address = addressService.getById(feeDto.getAddressId());
            Shop shop = shopService.getById(feeDto.getShopId());
            //获取店铺与收货地址的经纬度
            double aLongitude =Double.parseDouble(address.getLongitude());
            double aLatitude =Double.parseDouble(address.getLatitude());

            double sLongitude = Double.parseDouble(shop.getLongitude());
            double sLatitude = Double.parseDouble(shop.getLatitude());
            Vector3D addressPoint = getCartesianCoordinates(aLatitude, aLongitude, earthRadius);
            Vector3D shopPoint = getCartesianCoordinates(sLatitude, sLongitude, earthRadius);
            //计算距离结果单位:km
            double distance = Math.ceil(addressPoint.distance(shopPoint)/1000) ;
            //收费规则 配送费: 1公里内2元 超出范围每公里增加2元 不足1公里按1公里计费 打包费: 每一件商品收费0.6元
            BigDecimal shopNum = BigDecimal.valueOf(feeDto.getShopNum());
            BigDecimal each = BigDecimal.valueOf(0.6);

            feeVo.setPackagingFee(shopNum.multiply(each));
            if(distance<1){
               feeVo.setDeliveryFee(2);
               return feeVo;
            }
            feeVo.setDeliveryFee(2+(distance-1)*2);
            return feeVo;


        }

        @Transactional
        @Override
        public Result oneMoreOrder(String orderId) {
            //查询出该订单下的商品
            List<OrdersGoods> ogs = ordersGoodsService.list(Wrappers.<OrdersGoods>lambdaQuery().eq(OrdersGoods::getOrdersId, orderId));

            List<Cart> cartList=new ArrayList<>();
            for (OrdersGoods og : ogs) {
                String goodsId = og.getGoodsId();
                Goods goods = goodsService.getById(goodsId);
                if(goods==null){
                    return Result.error("商品数据变更");
                }
                Cart cart=new Cart();
                 cart.setCount(og.getCount());
                 cart.setUserId(ThreadLocalUtils.get().toString());
                 cart.setGoodsId(goodsId);
                 cart.setPrice(og.getPrice());
                 cart.setSkuInfo(og.getSkuInfo());
                 cartList.add(cart);
            }
            //将这些商品添加到购物车中
            for (Cart cart : cartList) {
                cartService.addToCart(cart);
            }
            return Result.success();
        }

        private String createOrder(OrderDto dto) {
            Orders orders =new Orders();
            //用户id
            String userId = ThreadLocalUtils.get().toString();
            orders.setUserId(userId);
            //设置订单状态为未支付
            orders.setStatus(OrdersStatusEnums.PENDING_PAYMENT.getCode());
            //设置创建时间
            orders.setCreateTime(LocalDateTime.now());
            //设置备注
            orders.setRemark(dto.getRemark());
            //设置总价
            orders.setTotalPrice(dto.getTotalPrice());
            //如果购买方式是外送的话,需要记录联系人以及地址信息
            orders.setAddress(dto.getAddress());
            orders.setGender(dto.getGender());
            orders.setName(dto.getName());
            orders.setPhone(dto.getPhone());
            if(StringUtils.isBlank(dto.getDeliveryFee())){
                //购买方式为自提
                orders.setBuyWay(OrderBuyWayEnums.PICK_UP.getCode());
            }else {
                //购买方式为外送
                orders.setBuyWay(OrderBuyWayEnums.DELIVERY.getCode());
            }
            //配送费以及打包费
            orders.setDeliveryFee(dto.getDeliveryFee());
            orders.setPackagingFee(dto.getPackagingFee());
            //设置店铺id以及店铺名称
            orders.setShopId(dto.getShopId());
            orders.setShopName(dto.getShopName());
            //设置优惠券
            if(StringUtils.isNotBlank(dto.getVoucherId())){
                orders.setUserVoucherId(dto.getVoucherId());
            }
            //生成id
            String id = IdUtil.simpleUUID();
            orders.setId(String.valueOf(id));
            save(orders);
            //将该订单的id,createTime存入队列中
            OrderIsTimeoutUtils otu = new OrderIsTimeoutUtils();
            otu.setOrderId(id);
            otu.setCreateTime(orders.getCreateTime());

            redisTemplate.opsForList().leftPush(RedisKeyConstant.ORDER_IS_TIMEOUT_KEY, JSONUtil.toJsonStr(otu));
            return id;
        }

        //释放锁
        private void unLock(List<String> keys){
          redisTemplate.execute(UNLOCK_SCRIPT,keys,ThreadLocalUtils.get().toString());
        }
       //得到经纬度点的三维坐标
        public   Vector3D getCartesianCoordinates(double latitude, double longitude, double radius) {
            double latRad = Math.toRadians(latitude);
            double lonRad = Math.toRadians(longitude);

            double x = radius * Math.cos(latRad) * Math.cos(lonRad);
            double y = radius * Math.cos(latRad) * Math.sin(lonRad);
            double z = radius * Math.sin(latRad);

            return new Vector3D(x, y, z);
        }
    }
