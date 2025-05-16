package com.grup7.Controller;

import com.grup7.Entity.OrderLog;
import com.grup7.Service.OrderLogService;
import com.grup7.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/orders")
public class OrderLogController {

    @Autowired
    private OrderLogService orderLogService;

    @PostMapping("/close/{id}")
    public ResponseEntity<OrderLog> closeOrder(@PathVariable Long id) {
        try {
            OrderLog log = orderLogService.closeOrder(id);
            return ResponseEntity.ok(log);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}