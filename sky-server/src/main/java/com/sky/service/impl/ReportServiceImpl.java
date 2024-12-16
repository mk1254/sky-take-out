package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
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
}