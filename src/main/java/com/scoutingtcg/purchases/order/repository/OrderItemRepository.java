package com.scoutingtcg.purchases.order.repository;

import com.scoutingtcg.purchases.order.model.OrderItem;
import com.scoutingtcg.purchases.order.dto.OrderItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
                SELECT new com.scoutingtcg.purchases.order.dto.OrderItemDto(
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


    @Query("""
                SELECT new com.scoutingtcg.purchases.order.dto.OrderItemDto(
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
    List<OrderItemDto> findOrderItemDtoByOrderId(String orderId);
}