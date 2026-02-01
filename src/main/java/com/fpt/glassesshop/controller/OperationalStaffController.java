package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.Order;
import com.fpt.glassesshop.service.OrderItemService;
import com.fpt.glassesshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operational-staff")
@RequiredArgsConstructor
public class OperationalStaffController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("prescriptionItems", orderItemService.getPrescriptionItems());
        model.addAttribute("stockItems", orderItemService.getInStockItems());
        model.addAttribute("preOrderItems", orderItemService.getPreOrderItems());
        model.addAttribute("stats", orderItemService.getOrderItemStats());
        return "operational-staff/dashboard";
    }

    @GetMapping("/order/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderService.getOrderItems(id));
        return "operational-staff/order-detail";
    }
}
