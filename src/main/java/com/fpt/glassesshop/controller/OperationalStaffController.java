package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.service.OrderItemService;
import com.fpt.glassesshop.service.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operational-staff")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OPERATIONAL_STAFF', 'ADMIN')")
public class OperationalStaffController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Stats
        model.addAttribute("prescriptionCount", orderItemService.countPrescriptionItems());
        model.addAttribute("preOrderCount", orderItemService.countPreOrderItems());
        model.addAttribute("inStockCount", orderItemService.countInStockItems());

        // Purchase Lists
        model.addAttribute("prescriptionItems", orderItemService.getPrescriptionItems());
        model.addAttribute("preOrderItems", orderItemService.getPreOrderItems());
        model.addAttribute("inStockItems", orderItemService.getInStockItems());

        return "operational-staff/dashboard";

    }

    @GetMapping("/order/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        orderService.getOrderById(id).ifPresent(order -> {
            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItemService.getOrderItemsByOrderId(id));
        });
        return "operational-staff/order-detail";
    }
}
