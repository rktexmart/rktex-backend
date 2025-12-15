package com.opsmonsters.quick_bite.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private RazorpayClient razorpayClient;

    public PaymentService(@Value("${razorpay.key_id}") String keyId,
                          @Value("${razorpay.key_secret}") String keySecret) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    public Order createRazorpayOrder(double amount) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("amount", (int)(amount * 100)); // in paise
        options.put("currency", "INR");
        options.put("payment_capture", 1);

        return razorpayClient.Orders.create(options); // this will work now
    }
}
