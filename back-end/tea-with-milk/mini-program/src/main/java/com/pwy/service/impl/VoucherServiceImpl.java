package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.Result;
import com.pwy.entity.pojo.User;
import com.pwy.entity.pojo.UserVoucher;
import com.pwy.entity.pojo.Voucher;
import com.pwy.entity.vo.VoucherListVo;
import com.pwy.enums.VoucherStatusEnum;
import com.pwy.mapper.VoucherMapper;
import com.pwy.service.UserService;
import com.pwy.service.UserVoucherService;
import com.pwy.service.VoucherService;
import com.pwy.utils.ThreadLocalUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserVoucherService userVoucherService;


    @Override
    public List<Voucher> getVoucherList() {
        //查询库存大于0的优惠券,由于优惠券种类不会太多,因此不采用分页了

        return   list(Wrappers.<Voucher>lambdaQuery().gt(Voucher::getNumber, 0));
    }


    @Override
    public Result exchange(String id) {

        synchronized (id.intern()) {
            VoucherService proxy =(VoucherService) AopContext.currentProxy();
            return proxy.operate(id);
        }
    }
    @Transactional
    public Result operate(String id) {
        //首先查询出该用户所拥有的宇王币数
        String userId = ThreadLocalUtils.get().toString();
        User user = userService.getById(userId);
        Integer coinNum = user.getYuKingCoin();
        //查询出该优惠券所需要的宇王币数以及库存
        Voucher  voucher = getOne(Wrappers.<Voucher>lambdaQuery().eq(Voucher::getId, id));
        Integer coinNeed = voucher.getCoinNeed();
        if (coinNum < coinNeed) {

            return Result.error("您的宇王币不足以兑换该优惠券");
        }
        if (voucher.getNumber() < 1) {
            return Result.error("优惠券库存不足");
        }
        //扣减优惠券库存
        LambdaUpdateWrapper<Voucher> luw=new LambdaUpdateWrapper<>();
        luw.set(Voucher::getNumber,voucher.getNumber()-1);
        luw.eq(Voucher::getId, id);
        update(luw);

        // TODO 扣减该用户的宇王币
           user.setYuKingCoin(user.getYuKingCoin() - coinNeed);
            userService.updateById(user);
        //赋予该用户优惠券
        UserVoucher uv = new UserVoucher();
        uv.setVoucherId(id);
        uv.setStatus(VoucherStatusEnum.UNUSED.getCode());
        uv.setAcquireTime(LocalDateTime.now());
        uv.setUserId(userId);
        userVoucherService.save(uv);
        return Result.success("兑换成功");
    }


}
