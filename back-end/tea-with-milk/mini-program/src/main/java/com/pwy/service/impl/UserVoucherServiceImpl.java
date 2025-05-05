package com.pwy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.Result;
import com.pwy.entity.pojo.UserVoucher;
import com.pwy.entity.pojo.Voucher;
import com.pwy.entity.vo.VoucherVo;
import com.pwy.enums.VoucherStatusEnum;
import com.pwy.mapper.UserVoucherMapper;
import com.pwy.service.UserVoucherService;
import com.pwy.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher> implements UserVoucherService {
    @Autowired
    private VoucherService voucherService;
    @Override
    public Result getVoucherList(String userId) {
        List<UserVoucher> voucherList = list(Wrappers.<UserVoucher>lambdaQuery().eq(UserVoucher::getUserId, userId));
        if(voucherList.isEmpty()) return Result.success(Collections.emptyList());
        //存放优惠券id,用于数据库查询
        Set<String> voucherIds=new HashSet<>();
        voucherList.forEach(v-> voucherIds.add(v.getVoucherId()));
        //查询出这些优惠券的详情信息
        List<Voucher> list = voucherService.lambdaQuery().in(Voucher::getId, voucherIds).list();
        //这个map用于方便取出指定的优惠券，避免多个for循环查找
        Map<String,Voucher> map=new HashMap<>();
        for (Voucher v : list) {
             map.put(v.getId(),v);
        }
        //返回给前端的结果集
        List<VoucherVo> result=new ArrayList<>();
        for (UserVoucher uv : voucherList) {
            //如果优惠券已经过期了或者已经使用了,直接返回就行了
            if(Objects.equals(uv.getStatus(), VoucherStatusEnum.Used.getCode())||Objects.equals(uv.getStatus(),VoucherStatusEnum.EXPIRED.getCode())){
                Voucher voucher = map.get(uv.getVoucherId());
               VoucherVo vv=new VoucherVo();
               vv.setId(uv.getId());
               vv.setVoucher(voucher);
               vv.setAcquireTime(uv.getAcquireTime());
               vv.setValidityPeriod(uv.getValidityPeriod());
               vv.setStatus(uv.getStatus());
               result.add(vv);
               continue;
            }
            //找到这个优惠券的详情信息
            Voucher voucher = map.get(uv.getVoucherId());
            //判断优惠券是否已经过期了 
            //优惠券获取的时期
            LocalDateTime acquireTime = uv.getAcquireTime();
            //有效期(天)
            Integer validityPeriod = uv.getValidityPeriod();
            //两种情况：1如果获取时间加上有效期的天数比现在小的话,说明已经过期了 2如果该种优惠券的截止日期已经到了,也是过期了
            if((validityPeriod!=null&&acquireTime.plusDays(validityPeriod.longValue()).isBefore(LocalDateTime.now()))||(voucher.getExpirationTime()!=null&&voucher.getExpirationTime().isBefore(LocalDateTime.now()))){
                //设置该优惠券状态为已过期
                uv.setStatus(VoucherStatusEnum.EXPIRED.getCode());
                //修改数据库
                update(Wrappers.<UserVoucher>lambdaUpdate().set(UserVoucher::getStatus,VoucherStatusEnum.EXPIRED.getCode()).eq(UserVoucher::getId,uv.getId()));
            }
            if(voucher!=null){
                VoucherVo vv=new VoucherVo();
                vv.setVoucher(voucher);
                vv.setStatus(uv.getStatus());
                vv.setAcquireTime(uv.getAcquireTime());
                vv.setValidityPeriod(uv.getValidityPeriod());
                vv.setId(uv.getId());
                result.add(vv);
            }
        }
        return  Result.success(result);
    }

    @Override
    public void changeVoucherStatusById(String id,VoucherStatusEnum statusEnum,LocalDateTime time) {
        LambdaUpdateWrapper<UserVoucher> luw=new LambdaUpdateWrapper<>();
        if(time!=null){
            luw.set(UserVoucher::getUseTime,time);
        }
        luw.set(UserVoucher::getStatus,statusEnum.getCode());
        luw.eq(UserVoucher::getId,id);
        update(luw);
    }
}
