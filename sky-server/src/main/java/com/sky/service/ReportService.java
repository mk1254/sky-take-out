package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 获取指定时间段内营业额统计数据
     * @param startDate
     * @param endDate
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定时间段内用户统计数据
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 获取指定时间段内订单统计数据
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 获取指定时间段内销量排名top10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /*
    导出运营数据报表
     */
    void exportData(HttpServletResponse response) throws IOException;
}
