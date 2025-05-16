package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.order.OrderItem;
import com.scoutingtcg.purchases.dto.OrderItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
                SELECT new com.scoutingtcg.purchases.model.OrderItemDto(
                    oi.id,
                    oi.productOrCardForSaleId,
                    oi.image,
                    oi.name,
                    oi.quantity,
                    oi.price,
                    oi.presentation,
                    oi.franchise
                )
                FROM OrderItem oi
                WHERE oi.order.id = :orderId
            """)
    List<OrderItemDto> findOrderDetailDtoByOrderId(String orderId);
}