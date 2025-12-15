package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.services.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222"
})
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay-order")
    public Map<String,Object> createRazorpayOrder(@RequestParam double amount) throws RazorpayException {
        Order order = paymentService.createRazorpayOrder(amount);

        Map<String,Object> map = new HashMap<>();
        map.put("id", order.get("id"));      // Razorpay order_id
        map.put("amount", order.get("amount"));
        map.put("currency", order.get("currency"));

        return map;
    }


}