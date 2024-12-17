package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取指定时间段内营业额统计数据
     * @param startDate
     * @param endDate
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate startDate, LocalDate endDate) {

        //存放从开始日期到结束日期的每天日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(startDate);
        while(!startDate.equals(endDate)){
            //如果开始日期和结束日期相同，则退出循环
            //否则，将开始日期加一天，并将其添加到日期列表中
            startDate=startDate.plusDays(1);
            dateList.add(startDate);
        }

        String date = StringUtils.join(dateList, ",");// 集合，分隔符

        //存放每天的营业额
        List<Double> turnoverList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //获取每天的营业额
            LocalDateTime startTime = LocalDateTime.of(localDate, LocalTime.MIN);//每天的开始时间
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);//每天的结束时间

            //select sum(amount) from orders where status=5 and order_time between startTime and endTime
            Map map =new HashMap();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
          Double turnover =  orderMapper.sumByMap(map);
          if(turnover==null){
              turnover=0.0;
          }
          turnoverList.add(turnover);
        }

        String turnover = StringUtils.join(turnoverList, ",");// 集合，分隔符

        return new TurnoverReportVO(date, turnover);
    }

    /**
     * 获取指定时间段内用户统计数据
     * @param startDate
     * @param endDate
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate startDate, LocalDate endDate) {
        //存放从开始日期到结束日期的每天日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(startDate);
        while(!startDate.equals(endDate)){
            //如果开始日期和结束日期相同，则退出循环
            //否则，将开始日期加一天，并将其添加到日期列表中
            startDate=startDate.plusDays(1);
            dateList.add(startDate);
        }

        String date = StringUtils.join(dateList, ",");// 集合，分隔符

        //存放每天新增的用户数量  select count(*) from user where creat_time between ? and ?
        List<Integer> newUserList=new ArrayList<>();
        //存放每天总的用户数量    select count(*) from user where creat_time < ?
        List<Integer> totalUserList=new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime startTime = LocalDateTime.of(localDate, LocalTime.MIN);//每天的开始时间
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);//每天的结束时间
            Map map =new HashMap();

            map.put("endTime", endTime);

            //总用户数量
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);

            map.put("startTime", startTime);

            //新增用户数量
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
        }

        //封装结果数据
        return UserReportVO.builder()
                .dateList(date)
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 获取指定时间段内订单统计数据
     * @param startDate
     * @param endDate
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate startDate, LocalDate endDate) {
        //存放从开始日期到结束日期的每天日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(startDate);
        while(!startDate.equals(endDate)){
            //如果开始日期和结束日期相同，则退出循环
            //否则，将开始日期加一天，并将其添加到日期列表中
            startDate=startDate.plusDays(1);
            dateList.add(startDate);
        }

        String date = StringUtils.join(dateList, ",");// 集合，分隔符

        List<Integer> orderCountList=new ArrayList<>();//每天订单总数
        List<Integer> validOrderCountList=new ArrayList<>();//每天的有效订单数

        //遍历dateList集合，查询每天的有效订单数和订单总数
        for (LocalDate localDate : dateList) {
            LocalDateTime startTime = LocalDateTime.of(localDate, LocalTime.MIN);//每天的开始时间
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);//每天的结束时间
            //查询每天订单总数  select count(*) from orders where order_time between ? and ?
            Integer orderCount = getOrderCount(startTime, endTime, null);

            //查询每天的有效订单数  select count(*) from orders where order_time between ? and ?  and status = ?
            Integer validOrderCount = getOrderCount(startTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间段内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //计算时间段内的有效订单总数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        double orderCompletionRate = 0.0;
        if(totalOrderCount !=0) {
           orderCompletionRate= validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(date)
                .totalOrderCount(totalOrderCount)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .build();
    }


    /**
     * 根据条件统计订单数量
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    private  Integer getOrderCount(LocalDateTime startTime, LocalDateTime endTime ,Integer status){
        Map map =new HashMap();

        map.put("endTime", endTime);
        map.put("startTime", startTime);
        map.put("status", status);

       return orderMapper.countByMap(map);


    }

    /**
     * 获取指定时间段内销量Top10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        //封装结果数据
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

}
