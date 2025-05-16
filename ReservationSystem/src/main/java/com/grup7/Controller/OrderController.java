package com.grup7.Controller;

import com.grup7.Dto.OrderDto;
import com.grup7.Entity.Order;
import com.grup7.Entity.OrderLog;
import com.grup7.Service.OrderLogService;
import com.grup7.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<Order> saveOrderWithCategories(@RequestBody OrderDto orderDto) {
        Order savedOrder = orderService.saveOrderWithCategories(orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/{reservationCode}")
    public ResponseEntity<List<String>> getCategoryNamesByReservationCode(@PathVariable String reservationCode) {
        List<String> categoryNames = orderService.getCategoryNamesByReservationCode(reservationCode);
        return ResponseEntity.ok(categoryNames);
    }

}