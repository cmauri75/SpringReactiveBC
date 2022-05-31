package net.reactive.gateway.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CustomerOrders {
    private final Customer customer;
    private final List<Order> orders;
}
