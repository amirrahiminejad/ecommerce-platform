package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.modules.sale.entity.Order;
import com.webrayan.store.modules.sale.enums.OrderStatus;
import com.webrayan.store.modules.sale.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * لیست سفارشات
     */
    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
            
            OrderStatus orderStatus = null;
            if (status != null && !status.trim().isEmpty()) {
                try {
                    orderStatus = OrderStatus.valueOf(status);
                } catch (IllegalArgumentException e) {
                    // Invalid status, ignore
                }
            }
            
            Page<Order> orders = orderService.findOrdersForAdmin(
                    search.trim().isEmpty() ? null : search,
                    orderStatus,
                    startDate,
                    endDate,
                    pageable
            );
            
            model.addAttribute("orders", orders);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orders.getTotalPages());
            model.addAttribute("totalElements", orders.getTotalElements());
            model.addAttribute("searchTerm", search);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("size", size);
            model.addAttribute("orderStatuses", OrderStatus.values());
            
            return "admin/pages/orders";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "خطا در بارگذاری لیست سفارشات: " + e.getMessage());
            return "admin/pages/orders";
        }
    }

    /**
     * جزئیات سفارش
     */
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        try {
            Order order = orderService.getOrderWithDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", OrderStatus.values());
            
            return "admin/pages/order-detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "خطا در نمایش جزئیات سفارش: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    /**
     * تغییر وضعیت سفارش
     */
    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(defaultValue = "") String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus, reason);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "وضعیت سفارش " + updatedOrder.getOrderNumber() + " به " + 
                    newStatus.getPersianName() + " تغییر یافت");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "وضعیت نامعتبر");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در تغییر وضعیت سفارش: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }

    /**
     * تایید سفارش
     */
    @PostMapping("/{id}/confirm")
    public String confirmOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.confirmOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "سفارش " + order.getOrderNumber() + " با موفقیت تایید شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در تایید سفارش: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }

    /**
     * لغو سفارش
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            @PathVariable Long id, 
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            Order order = orderService.cancelOrder(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "سفارش " + order.getOrderNumber() + " لغو شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در لغو سفارش: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }

    /**
     * ارسال سفارش
     */
    @PostMapping("/{id}/ship")
    public String shipOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.shipOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "سفارش " + order.getOrderNumber() + " برای ارسال آماده شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در ارسال سفارش: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }

    /**
     * تحویل سفارش
     */
    @PostMapping("/{id}/deliver")
    public String deliverOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.deliverOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "سفارش " + order.getOrderNumber() + " تحویل داده شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در تحویل سفارش: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }

    /**
     * پردازش سفارش
     */
    @PostMapping("/{id}/process")
    public String processOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.processOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "سفارش " + order.getOrderNumber() + " وارد مرحله پردازش شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در پردازش سفارش: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
}
