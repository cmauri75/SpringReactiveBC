package net.reactive.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private Integer orderId;
    private Integer CustomerId;
}
