package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.OrderRequest;
import com.scoutingtcg.purchases.dto.CartItemDto;
import com.scoutingtcg.purchases.model.Order;
import com.scoutingtcg.purchases.model.OrderItem;
import com.scoutingtcg.purchases.model.Role;
import com.scoutingtcg.purchases.model.User;
import com.scoutingtcg.purchases.repository.OrderItemRepository;
import com.scoutingtcg.purchases.repository.OrderRepository;
import com.scoutingtcg.purchases.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setEmail(request.getEmail());
        order.setFullName(request.getFullName());
        order.setAddress(request.getAddress());
        order.setApartment(request.getApartment());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setZip(request.getZip());
        order.setTotal(request.getTotal());
        order.setCreatedAt(LocalDateTime.now());

        if (request.getUserId() != null) {
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            userOpt.ifPresent(order::setUser);
        } else {
            Optional<User> existing = userRepository.findByEmail(request.getEmail());
            if (existing.isPresent()) {
                order.setUser(existing.get());
            } else {
                User newUser = new User();
                newUser.setEmail(request.getEmail());
                String[] nameParts = (request.getFullName() != null ? request.getFullName() : "").split(" ", 2);
                newUser.setName(nameParts[0]);
                newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                newUser.setPassword("");
                newUser.setPhone("");
                newUser.setRole(Role.USER.name());
                userRepository.save(newUser);
                order.setUser(newUser);
            }
        }

        List<OrderItem> items = request.getCartItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            item.setProductOrCardForSaleId(itemDto.getProductOrCardForSaleId());
            item.setName(itemDto.getName());
            item.setImage(itemDto.getImage());
            item.setPresentation(itemDto.getPresentation());
            item.setFranchise(itemDto.getFranchise());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(items);
        return savedOrder;

    }
}
