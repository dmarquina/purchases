package com.scoutingtcg.purchases.order.service;

import com.scoutingtcg.purchases.cardforsale.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.shared.exceptionhandler.InsufficientStockException;
import com.scoutingtcg.purchases.shared.integration.EmailService;
import com.scoutingtcg.purchases.shared.integration.S3ClientService;
import com.scoutingtcg.purchases.shared.model.*;
import com.scoutingtcg.purchases.order.dto.*;
import com.scoutingtcg.purchases.order.model.*;
import com.scoutingtcg.purchases.order.repository.OrderItemRepository;
import com.scoutingtcg.purchases.order.repository.OrderRepository;
import com.scoutingtcg.purchases.product.repository.ProductRepository;
import com.scoutingtcg.purchases.security.model.Role;
import com.scoutingtcg.purchases.security.model.User;
import com.scoutingtcg.purchases.security.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.scoutingtcg.purchases.shared.util.MailBodyBuilder.buildOrderConfirmationBody;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CardForSaleRepository cardForSaleRepository;
    private final ProductRepository productRepository;
    private final S3ClientService s3ClientService;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        OrderItemRepository orderItemRepository,
                        CardForSaleRepository cardForSaleRepository,
                        ProductRepository productRepository,
                        S3ClientService s3ClientService,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.cardForSaleRepository = cardForSaleRepository;
        this.productRepository = productRepository;
        this.s3ClientService = s3ClientService;
        this.emailService = emailService;
    }

    public List<CartItemDto> checkStockAvailability(List<CartItemDto> cartItems) {
        List<CartItemDto> unavailable = new ArrayList<>();

        for (CartItemDto item : cartItems) {
            if ("single".equalsIgnoreCase(item.getPresentation())) {
                cardForSaleRepository.findById(item.getProductOrCardForSaleId()).ifPresentOrElse(card -> {
                    if (card.getStock() < item.getQuantity()) {
                        item.setStock(card.getStock());
                        unavailable.add(item);
                    }
                }, () -> {
                    item.setStock(0);
                    unavailable.add(item);
                });
            } else {
                productRepository.findById(item.getProductOrCardForSaleId()).ifPresentOrElse(product -> {
                    if (product.getStock() < item.getQuantity()) {
                        item.setStock(product.getStock());
                        unavailable.add(item);
                    }
                }, () -> {
                    item.setStock(0);
                    unavailable.add(item);
                });
            }
        }
        return unavailable;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        List<OrderItem> items = request.cartItems().stream()
                .map(this::mapToOrderItem)
                .collect(Collectors.toList());

        Order order = new Order();
        order.setEmail(request.email());
        order.setPhone(request.phone());
        order.setFullName(request.fullName());
        order.setAddress(request.address());
        order.setApartment(request.apartment());
        order.setCity(request.city());
        order.setState(request.state());
        order.setZip(request.zip());
        order.setShippingCost(request.shippingCost());
        order.setFreeShippingApplied(request.freeShippingApplied());
        order.setShippingSize(request.shippingSize());
        order.setTotal(request.total());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        handleUserForOrder(request, order);
        Order savedOrder = orderRepository.save(order);

        items.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(items);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getFullName(),
                savedOrder.getEmail(),
                savedOrder.getAddress(),
                savedOrder.getCity(),
                savedOrder.getState(),
                savedOrder.getTotal(),
                savedOrder.getReceiptUrl(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
    }

    public void uploadPayment(String orderId, MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String imageUrl;
        try {
            String receiptBucketName = s3ClientService.getReceiptsBucket();
            imageUrl = s3ClientService.uploadFile(receiptBucketName, fileName, file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setReceiptUrl(imageUrl);
        order.setStatus(OrderStatus.PROCESSING_PAYMENT);
        List<OrderItemDto> items = orderItemRepository.findOrderDetailDtoByOrderId(orderId);

        String body = buildOrderConfirmationBody(order, items);
        emailService.sendSimpleMail(order.getEmail(), "Thanks for your order!", body);

        orderRepository.save(order);
    }

    public Page<OrderSummaryResponse> getAllOrderSummaries(Pageable pageable) {
        return orderRepository.findAllOrderSummaries(pageable);
    }

    public Page<OrderSummaryResponse> getAllUserOrderSummaries(Pageable pageable, Long userId) {
        return orderRepository.findAllUserOrderSummaries(pageable, userId);
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemDto> items = orderItemRepository.findOrderDetailDtoByOrderId(orderId);

        return new OrderDetailResponse(
                order.getId(),
                order.getFullName(),
                order.getEmail(),
                order.getAddress(),
                order.getApartment(),
                order.getCity(),
                order.getState(),
                order.getZip(),
                order.getPhone(),
                order.getShippingSize().toString(),
                order.getShippingCost(),
                order.isFreeShippingApplied(),
                order.getTotal(),
                order.getReceiptUrl(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PROCESSING_PAYMENT -> next == OrderStatus.PAID || next == OrderStatus.CANCELED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.REFUNDED;
            case SHIPPED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
    }

    private void handleUserForOrder(OrderRequest request, Order order) {
        if (request.userId() != null) {
            userRepository.findById(request.userId()).ifPresent(order::setUser);
        } else {
            Optional<User> existing = userRepository.findByEmail(request.email());
            if (existing.isPresent()) {
                order.setUser(existing.get());
            } else {
                User newUser = new User();
                newUser.setEmail(request.email());
                String[] nameParts = Optional.ofNullable(request.fullName()).orElse("").split(" ", 2);
                newUser.setName(nameParts[0]);
                newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                newUser.setPassword("");
                newUser.setPhone(request.phone());
                newUser.setRole(Role.USER.name());
                userRepository.save(newUser);
                order.setUser(newUser);
            }
        }
    }

    private OrderItem mapToOrderItem(CartItemDto cartItemDto) {
        OrderItem item = new OrderItem();
        item.setProductOrCardForSaleId(cartItemDto.getProductOrCardForSaleId());
        item.setName(cartItemDto.getName());
        item.setImage(cartItemDto.getImage());
        item.setPresentation(cartItemDto.getPresentation());
        item.setFranchise(cartItemDto.getFranchise());
        item.setQuantity(cartItemDto.getQuantity());
        item.setPrice(cartItemDto.getPrice());

        if ("single".equalsIgnoreCase(cartItemDto.getPresentation())) {
            cardForSaleRepository.findById(cartItemDto.getProductOrCardForSaleId())
                    .map(card -> {
                        if (card.getStock() < cartItemDto.getQuantity()) {
                            throw new InsufficientStockException("Not enough stock for card: " + card.getId());
                        }
                        card.setStock(card.getStock() - cartItemDto.getQuantity());
                        if (card.getStock() == 0) card.setStatus(Status.INACTIVE);
                        return cardForSaleRepository.save(card);
                    })
                    .orElseThrow(() -> new InsufficientStockException("Card not found: " + cartItemDto.getProductOrCardForSaleId()));
        } else {
            productRepository.findById(cartItemDto.getProductOrCardForSaleId())
                    .map(product -> {
                        if (product.getStock() < cartItemDto.getQuantity()) {
                            throw new InsufficientStockException("Not enough stock for product: " + product.getProductId());
                        }
                        product.setStock(product.getStock() - cartItemDto.getQuantity());
                        if (product.getStock() == 0) product.setStatus(Status.INACTIVE);
                        return productRepository.save(product);
                    })
                    .orElseThrow(() -> new InsufficientStockException("Product not found: " + cartItemDto.getProductOrCardForSaleId()));
        }

        return item;
    }

}