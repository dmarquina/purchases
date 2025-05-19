package com.scoutingtcg.purchases.shared.util;

import com.scoutingtcg.purchases.order.model.Order;
import com.scoutingtcg.purchases.order.dto.OrderItemDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MailBodyBuilder {

    public static String buildOrderConfirmationBody(Order order, List<OrderItemDto> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hi ").append(order.getShippingName()).append(",\n\n");
        sb.append("Thank you for your order at OnePokeCard!\n\n");

        sb.append("📦 Order Details:\n");
        sb.append("Order ID: 000").append(order.getId()).append("\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        sb.append("Placed on: ").append(order.getCreatedAt().format(formatter)).append("\n");
        sb.append("Total: $").append(String.format("%.2f", order.getTotal())).append("\n\n");

        sb.append("🃏 Items:\n");
        for (OrderItemDto item : items) {
            sb.append("- ").append(item.name())
                    .append(" x").append(item.quantity())
                    .append(" - $").append(String.format("%.2f", item.price() * item.quantity()))
                    .append("\n");
        }

        sb.append("\nWe'll review your payment and notify you once your order is confirmed.\n");
        sb.append("You can check your order status from the Order History page.\n\n");

        sb.append("🔗 Remember to upload your payment receipt if you haven't already.\n\n");
        sb.append("Best,\n");
        sb.append("The OnePokeCard Team");

        return sb.toString();
    }

    public static String buildOwnerNotificationBody(Order order, List<OrderItemDto> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello,\n\n");
        sb.append("A new order has been placed on OnePokeCard.\n\n");

        sb.append("📦 Order Details:\n");
        sb.append("Order ID: 000").append(order.getId()).append("\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        sb.append("Placed on: ").append(order.getCreatedAt().format(formatter)).append("\n");
        sb.append("Customer Name: ").append(order.getShippingName()).append("\n");
        sb.append("Customer Email: ").append(order.getEmail()).append("\n");
        sb.append("Customer Phone: ").append(order.getPhone()).append("\n");
        sb.append("Shipping Address: ").append(order.getShippingAddressLine())
                .append(", ").append(order.getShippingApartment())
                .append(", ").append(order.getShippingCity())
                .append(", ").append(order.getShippingState())
                .append(", ").append(order.getShippingZip())
                .append(", ").append(order.getShippingCountry()).append("\n");
        sb.append("Total: $").append(String.format("%.2f", order.getTotal())).append("\n\n");

        sb.append("🃏 Items:\n");
        for (OrderItemDto item : items) {
            sb.append("- ").append(item.name())
                    .append(" x").append(item.quantity())
                    .append(" - $").append(String.format("%.2f", item.price() * item.quantity()))
                    .append("\n");
        }

        sb.append("\nPlease review the payment and process the order accordingly.\n\n");
        sb.append("Best,\n");
        sb.append("The OnePokeCard System");

        return sb.toString();
    }

}
