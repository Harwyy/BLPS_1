package com.blps.blps.service;

import com.blps.blps.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean processPayment(Order order) {
        return true;
    }

    public boolean processRefund(Order order, int percent) {return true;}
}
