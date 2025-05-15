package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.dto.OrderDetailResponse;
import com.scoutingtcg.purchases.model.Order;
import com.scoutingtcg.purchases.model.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("""
                SELECT new com.scoutingtcg.purchases.model.OrderSummaryResponse(
                    o.id,
                    o.fullName,
                    o.total,
                    o.receiptUrl,
                    o.createdAt,
                    o.status,
                    COUNT(oi.id)
                )
                FROM Order o
                JOIN OrderItem oi ON oi.order = o
                GROUP BY o.id, o.fullName, o.total, o.receiptUrl, o.status
                ORDER BY o.createdAt DESC
            """)
    Page<OrderSummaryResponse> findAllOrderSummaries(Pageable pageable);

    @Query("""
                SELECT new com.scoutingtcg.purchases.model.OrderSummaryResponse(
                    o.id,
                    o.fullName,
                    o.total,
                    o.receiptUrl,
                    o.createdAt,
                    o.status,
                    COUNT(oi.id)
                )
                FROM Order o
                JOIN OrderItem oi ON oi.order = o
                WHERE o.user.userId = :userId
                GROUP BY o.id, o.fullName, o.total, o.receiptUrl, o.status
                ORDER BY o.createdAt DESC
            """)
    Page<OrderSummaryResponse> findAllUserOrderSummaries(Pageable pageable, Long userId);

}