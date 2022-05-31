package net.reactive.gateway.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CustomerOrders(Customer customer, List<Order> orders) {
}
