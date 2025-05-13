package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.OrderRequest;
import com.scoutingtcg.purchases.dto.CartItemDto;
import com.scoutingtcg.purchases.exceptionhandler.InsufficientStockException;
import com.scoutingtcg.purchases.model.*;
import com.scoutingtcg.purchases.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CardForSaleRepository cardForSaleRepository;
    private final ProductRepository productRepository;
    private final S3ClientService s3ClientService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        OrderItemRepository orderItemRepository,
                        CardForSaleRepository cardForSaleRepository,
                        ProductRepository productRepository,
                        S3ClientService s3ClientService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.cardForSaleRepository = cardForSaleRepository;
        this.productRepository = productRepository;
        this.s3ClientService = s3ClientService;
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
    public Order createOrder(OrderRequest request) {
        List<OrderItem> items = request.getCartItems().stream()
                .map(this::mapToOrderItem)
                .collect(Collectors.toList());

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
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        handleUserForOrder(request, order);
        Order savedOrder = orderRepository.save(order);

        items.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(items);

        return savedOrder;
    }

    public void uploadPayment(Long orderId, MultipartFile file) {
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
        orderRepository.save(order);
    }

    private void handleUserForOrder(OrderRequest request, Order order) {
        if (request.getUserId() != null) {
            userRepository.findById(request.getUserId()).ifPresent(order::setUser);
        } else {
            Optional<User> existing = userRepository.findByEmail(request.getEmail());
            if (existing.isPresent()) {
                order.setUser(existing.get());
            } else {
                User newUser = new User();
                newUser.setEmail(request.getEmail());
                String[] nameParts = Optional.ofNullable(request.getFullName()).orElse("").split(" ", 2);
                newUser.setName(nameParts[0]);
                newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                newUser.setPassword("");
                newUser.setPhone("");
                newUser.setRole(Role.USER.name());
                userRepository.save(newUser);
                order.setUser(newUser);
            }
        }
    }

    private OrderItem mapToOrderItem(CartItemDto dto) {
        OrderItem item = new OrderItem();
        item.setProductOrCardForSaleId(dto.getProductOrCardForSaleId());
        item.setName(dto.getName());
        item.setImage(dto.getImage());
        item.setPresentation(dto.getPresentation());
        item.setFranchise(dto.getFranchise());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());

        if ("single".equalsIgnoreCase(dto.getPresentation())) {
            cardForSaleRepository.findById(dto.getProductOrCardForSaleId())
                    .map(card -> {
                        if (card.getStock() < dto.getQuantity()) {
                            throw new InsufficientStockException("Not enough stock for card: " + card.getId());
                        }
                        card.setStock(card.getStock() - dto.getQuantity());
                        if (card.getStock() == 0) card.setStatus(Status.INACTIVE);
                        return cardForSaleRepository.save(card);
                    })
                    .orElseThrow(() -> new InsufficientStockException("Card not found: " + dto.getProductOrCardForSaleId()));
        } else {
            productRepository.findById(dto.getProductOrCardForSaleId())
                    .map(product -> {
                        if (product.getStock() < dto.getQuantity()) {
                            throw new InsufficientStockException("Not enough stock for product: " + product.getProductId());
                        }
                        product.setStock(product.getStock() - dto.getQuantity());
                        if (product.getStock() == 0) product.setStatus(Status.INACTIVE);
                        return productRepository.save(product);
                    })
                    .orElseThrow(() -> new InsufficientStockException("Product not found: " + dto.getProductOrCardForSaleId()));
        }

        return item;
    }

}