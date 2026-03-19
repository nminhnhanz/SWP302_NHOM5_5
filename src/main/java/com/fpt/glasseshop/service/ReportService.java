package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.dto.RevenueResponse;
import com.fpt.glasseshop.repository.OrderRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepo;


    public RevenueResponse getRevenueBetween(LocalDate fromDate, LocalDate toDate) throws BadRequestException {

        if (fromDate == null || toDate == null) {
            throw new BadRequestException("fromDate and toDate are required");
        }

        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException("fromDate must be before or equal to toDate");
        }

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(23,59,59);

        BigDecimal totalRevenue = orderRepo.calculateRevenueBetween(from, to);
        Long totalOrders = orderRepo.countPaidOrdersBetween(from, to);

        return RevenueResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .build();

    }
}
