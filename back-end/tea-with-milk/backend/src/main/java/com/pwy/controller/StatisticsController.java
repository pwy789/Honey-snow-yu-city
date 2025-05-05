package com.pwy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pwy.common.Result;
import com.pwy.entity.pojo.Orders;
import com.pwy.entity.vo.*;
import com.pwy.enums.OrderBuyWayEnums;
import com.pwy.enums.OrdersStatusEnums;
import com.pwy.mapper.StatisticsMapper;
import com.pwy.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

//流水统计控制层
@Slf4j
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private VoucherService voucherService;


    @Autowired
    private StatisticsMapper mapper;

    //获取今日数据
    @GetMapping("/today")
    public Result getCurdayData(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("date: {}", date);
        // TODO 获取当日的自提订单量以及外送订单量
        String dateStart = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        String dateEnd = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));
        Statistics result = getData(dateStart, dateEnd);
        //至此,商品销量榜前5名已经查询完成
        return Result.success(result);
    }

    //获取本周数据
    @GetMapping("/week")
    public Result getCurWeekData(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        //获取到当天天是一个星期的第几天
        int dayOfWeek = date.getDayOfWeek().getValue();
        //本周的开始日期
        String weekStart = date.plusDays(1 - dayOfWeek).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //本周的结束日期
        String weekEnd = date.plusDays(7 - dayOfWeek).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Statistics result = getData(weekStart, weekEnd);
        //获取本周每一天的订单量
        List<DayCount> dc = mapper.getDayOfOrderCountForWeek(weekStart, weekEnd);
        result.setOrderCountByDay(dc);
        return Result.success(result);
    }

    private Statistics getData(String startTime, String endTime) {
        Statistics result = new Statistics();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.ge(Orders::getOrderTime, startTime);
        lqw.le(Orders::getOrderTime, endTime);
        lqw.ge(Orders::getStatus, OrdersStatusEnums.COMPLETED.getCode());
        lqw.select(Orders::getBuyWay, Orders::getTotalPrice, Orders::getId);
        BigDecimal pickUpTotalPrice = BigDecimal.ZERO;
        int pickUpCount = 0;
        int deliveryCount = 0;
        BigDecimal deliveryTotalPrice = BigDecimal.ZERO;
        List<Orders> ordersList = ordersService.list(lqw);

        for (Orders o : ordersList) {
            if (Objects.equals(o.getBuyWay(), OrderBuyWayEnums.DELIVERY.getCode())) {
                deliveryTotalPrice = deliveryTotalPrice.add(new BigDecimal(o.getTotalPrice().toString()));
                deliveryCount++;
            } else {
                pickUpTotalPrice = pickUpTotalPrice.add(new BigDecimal(o.getTotalPrice().toString()));
                pickUpCount++;
            }
        }
        //封装
        OrdersStatistics ocp = new OrdersStatistics();
        ocp.setOrderCount(pickUpCount);
        ocp.setOrderWay(OrderBuyWayEnums.PICK_UP.getCode());
        ocp.setOrderTotalPrice(pickUpTotalPrice);
        OrdersStatistics ocd = new OrdersStatistics();
        ocd.setOrderCount(deliveryCount);
        ocd.setOrderWay(OrderBuyWayEnums.DELIVERY.getCode());
        ocd.setOrderTotalPrice(deliveryTotalPrice);
        List<OrdersStatistics> ordersStatistics = new ArrayList<>();
        ordersStatistics.add(ocp);
        ordersStatistics.add(ocd);
        //至此，订单的总量以及总价格整理完毕
        result.setOrdersStatistics(ordersStatistics);
        // TODO 查询优惠券的使用率
        List<VoucherTotalCount> vtcs = mapper.queryVoucherUseRate(startTime, endTime);
        List<VoucherStatistics> vsList = new ArrayList<>();
        //这里已经查询到指定时间的优惠券使用数量,接着需要根据优惠券的id,查询优惠券的名称
        for (VoucherTotalCount vtc : vtcs) {
            String voucherId = vtc.getVoucherId();
            String voucherName = voucherService.getById(voucherId).getName();
            VoucherStatistics vs = new VoucherStatistics(voucherName, vtc.getTotalCount());
            vsList.add(vs);
        }
        //至此,优惠券的使用率已查询完成
        result.setVoucherStatistics(vsList);
        // TODO 获取商品销售榜前5名
        if (!ordersList.isEmpty()) {
            //根据刚才查询出的订单的主键id,去查询商品
            List<String> orderIds = ordersList.stream().map(Orders::getId).collect(Collectors.toList());
            //这里已经拿到商品销量榜前5名的商品id以及总量
            List<GoodsTotalCount> gtc = mapper.getGoodsIdAndTotalCount(orderIds);
            List<GoodsStatistics> gsList = new ArrayList<>();
            //查询这些商品的名称
            for (GoodsTotalCount g : gtc) {
                String goodsName = goodsService.getById(g.getGoodsId()).getName();
                GoodsStatistics gs = new GoodsStatistics(g.getGoodsId(), goodsName, g.getTotalCount());
                gsList.add(gs);
            }
            result.setGoodsStatistics(gsList);
        } else {
            result.setGoodsStatistics(Collections.emptyList());
        }


        return result;
    }

    //获取本月数据
    @GetMapping("/month")
    public Result getCurMonthData(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        //拿到本月的第一天

        String theFirstDayOfMonth = date.plusDays(1 - date.getDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //获取到本月的天数
        int day = YearMonth.now().lengthOfMonth();
        //本月最后一天
        String theLastDayOfMonth = date.plusDays(day - date.getDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Statistics statistics = getData(theFirstDayOfMonth, theLastDayOfMonth);
        List<DayCount> doc = mapper.getDayOfOrderCountForMonth(theFirstDayOfMonth, theLastDayOfMonth);
        statistics.setOrderCountByDay(doc);
        return Result.success(statistics);
    }

    //获取本年数据
    @GetMapping("/year")
    public Result getCurYearData(@DateTimeFormat(pattern = "yyyy") Year year) {
        //一年的开始,即1月1日
        String YearStart = LocalDate.of(year.getValue(), 1, 1).toString();
        //一年的结束,即12日31日
        String YearEnd = LocalDate.of(year.getValue(), 12, 31).toString();
        Statistics result = getData(YearStart, YearEnd);
        //获取折线图部分数据
        List<DayCount> dc = mapper.getMonthOfOrderCountForYear(year.getValue());
        for (DayCount d : dc) {
            String month = d.getDay();
            d.setDay(year.getValue()+"年"+month+"月");

        }
        result.setOrderCountByDay(dc);
        return Result.success(result);
    }
}
