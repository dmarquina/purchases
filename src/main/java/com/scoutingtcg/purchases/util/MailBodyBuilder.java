package com.scoutingtcg.purchases.util;

import com.scoutingtcg.purchases.model.order.Order;
import com.scoutingtcg.purchases.dto.OrderItemDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MailBodyBuilder {

    public static String buildOrderConfirmationBody(Order order, List<OrderItemDto> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hi ").append(order.getFullName()).append(",\n\n");
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

}
