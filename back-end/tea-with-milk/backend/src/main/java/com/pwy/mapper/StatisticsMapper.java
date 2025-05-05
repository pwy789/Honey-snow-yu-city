package com.pwy.mapper;

import com.pwy.entity.vo.DayCount;
import com.pwy.entity.vo.GoodsTotalCount;
import com.pwy.entity.vo.VoucherTotalCount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsMapper {
   List<GoodsTotalCount> getGoodsIdAndTotalCount(List<String> orderIds);

   List<VoucherTotalCount> queryVoucherUseRate(@Param("startTime") String startTime,@Param("endTime") String endTime);

   List<DayCount> getDayOfOrderCountForMonth(@Param("start") String theFirstDayOfMonth, @Param("end") String theLastDayOfMonth);

   List<DayCount> getDayOfOrderCountForWeek(@Param("start") String weekStart,@Param("end") String weekEnd);

    List<DayCount> getMonthOfOrderCountForYear(@Param("year") int year);
}
