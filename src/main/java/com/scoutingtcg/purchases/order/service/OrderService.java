package com.scoutingtcg.purchases.order.service;

import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithDetailsDto;
import com.scoutingtcg.purchases.cardforsale.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.order.dto.CartItemDto;
import com.scoutingtcg.purchases.order.dto.OrderItemDto;
import com.scoutingtcg.purchases.order.dto.request.OrderRequest;
import com.scoutingtcg.purchases.order.dto.response.OrderDetailResponse;
import com.scoutingtcg.purchases.order.dto.response.OrderResponse;
import com.scoutingtcg.purchases.order.dto.response.OrderSummaryResponse;
import com.scoutingtcg.purchases.order.model.Order;
import com.scoutingtcg.purchases.order.model.OrderItem;
import com.scoutingtcg.purchases.order.model.OrderStatus;
import com.scoutingtcg.purchases.order.repository.OrderItemRepository;
import com.scoutingtcg.purchases.order.repository.OrderRepository;
import com.scoutingtcg.purchases.pokemoncard.service.PokemonCardPriceService;
import com.scoutingtcg.purchases.product.repository.ProductRepository;
import com.scoutingtcg.purchases.security.model.Role;
import com.scoutingtcg.purchases.security.model.User;
import com.scoutingtcg.purchases.security.repository.UserRepository;
import com.scoutingtcg.purchases.shared.dto.AddressDto;
import com.scoutingtcg.purchases.shared.exceptionhandler.InsufficientStockException;
import com.scoutingtcg.purchases.shared.integration.EmailService;
import com.scoutingtcg.purchases.shared.integration.S3ClientService;
import com.scoutingtcg.purchases.shared.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static com.scoutingtcg.purchases.shared.util.MailBodyBuilder.buildOwnerNotificationBody;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CardForSaleRepository cardForSaleRepository;
    private final ProductRepository productRepository;
    private final S3ClientService s3ClientService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(PokemonCardPriceService.class);

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
        logger.info("Checking stock availability for {} cart items", cartItems.size());

        for (CartItemDto item : cartItems) {
            logger.debug("Checking stock for item: {}", item);
            if ("single".equalsIgnoreCase(item.getPresentation())) {
                cardForSaleRepository.findById(item.getProductOrCardForSaleId()).ifPresentOrElse(card -> {
                    if (card.getStock() < item.getQuantity()) {
                        logger.warn("Insufficient stock for card ID {}: requested {}, available {}",
                                card.getId(), item.getQuantity(), card.getStock());
                        item.setStock(card.getStock());
                        unavailable.add(item);
                    }
                }, () -> {
                    logger.warn("Card not found for ID {}", item.getProductOrCardForSaleId());
                    item.setStock(0);
                    unavailable.add(item);
                });
            } else {
                productRepository.findById(item.getProductOrCardForSaleId()).ifPresentOrElse(product -> {
                    if (product.getStock() < item.getQuantity()) {
                        logger.warn("Insufficient stock for product ID {}: requested {}, available {}",
                                product.getProductId(), item.getQuantity(), product.getStock());
                        item.setStock(product.getStock());
                        unavailable.add(item);
                    }
                }, () -> {
                    logger.warn("Product not found for ID {}", item.getProductOrCardForSaleId());
                    item.setStock(0);
                    unavailable.add(item);
                });
            }
        }
        logger.info("Stock check completed. {} items unavailable.", unavailable.size());
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

        order.setShippingName(request.shippingAddress().fullName());
        order.setShippingAddressLine(request.shippingAddress().addressLine());
        order.setShippingApartment(request.shippingAddress().apartment());
        order.setShippingCity(request.shippingAddress().city());
        order.setShippingState(request.shippingAddress().state());
        order.setShippingZip(request.shippingAddress().zip());
        order.setShippingCountry("USA");

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
                savedOrder.getEmail(),
                new AddressDto(
                        savedOrder.getShippingName(),
                        savedOrder.getShippingAddressLine(),
                        savedOrder.getShippingApartment(),
                        savedOrder.getShippingCity(),
                        savedOrder.getShippingState(),
                        savedOrder.getShippingZip(),
                        savedOrder.getShippingCountry()
                ),
                savedOrder.getReceiptUrl(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
    }

    public void uploadPayment(String orderId, MultipartFile file) {
        logger.info("Starting uploadPayment for orderId: {}", orderId);
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String imageUrl;
        try {
            String receiptBucketName = s3ClientService.getReceiptsBucket();
            logger.debug("Uploading file to S3 bucket: {}, fileName: {}", receiptBucketName, fileName);
            imageUrl = s3ClientService.uploadFile(receiptBucketName, fileName, file.getInputStream(), file.getContentType());
            logger.info("File uploaded successfully. Image URL: {}", imageUrl);
        } catch (IOException e) {
            logger.error("Error uploading file for orderId: {}", orderId, e);
            throw new RuntimeException(e);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found for orderId: {}", orderId);
                    return new RuntimeException("Order not found");
                });

        logger.info("Updating order with receipt URL and status. OrderId: {}", orderId);
        order.setReceiptUrl(imageUrl);
        order.setStatus(OrderStatus.PROCESSING_PAYMENT);

        List<OrderItemDto> items = orderItemRepository.findOrderDetailDtoByOrderId(orderId);
        logger.debug("Retrieved {} order items for orderId: {}", items.size(), orderId);

        String body = buildOrderConfirmationBody(order, items);
        emailService.sendSimpleMail(order.getEmail(), "Thanks for your order!", body);
        logger.info("Order confirmation email sent to: {}", order.getEmail());

        String bodyForOwner = buildOwnerNotificationBody(order, items);
        emailService.sendSimpleMail("mrdiego0892@gmail.com", "New Order in OnePokeCard!", bodyForOwner);
        logger.info("Owner notification email sent for orderId: {}", orderId);

        orderRepository.save(order);
        logger.info("Order updated and saved successfully. OrderId: {}", orderId);
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
                order.getEmail(),
                order.getPhone(),
                new AddressDto(
                        order.getShippingName(),
                        order.getShippingAddressLine(),
                        order.getShippingApartment(),
                        order.getShippingCity(),
                        order.getShippingState(),
                        order.getShippingZip(),
                        order.getShippingCountry()
                ),
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
            existing.ifPresent(order::setUser);
        }
    }

    private OrderItem mapToOrderItem(CartItemDto cartItemDto) {
        OrderItem item = mapBasicOrderItemDetails(cartItemDto);

        if ("single".equalsIgnoreCase(cartItemDto.getPresentation())) {
            handleSingleCardStock(cartItemDto);
        } else {
            handleProductStock(cartItemDto);
        }

        return item;
    }

    private OrderItem mapBasicOrderItemDetails(CartItemDto cartItemDto) {


        OrderItem item = new OrderItem();
        item.setProductOrCardForSaleId(cartItemDto.getProductOrCardForSaleId());
        if ("single".equalsIgnoreCase(cartItemDto.getPresentation())
                && "pokemon".equalsIgnoreCase(cartItemDto.getFranchise())) {
            Optional<CardForSaleWithDetailsDto> csfdOp = cardForSaleRepository.findCardForSaleDetailsById(cartItemDto.getProductOrCardForSaleId());
            if (csfdOp.isPresent()) {
                CardForSaleWithDetailsDto cfsd = csfdOp.get();
                String name = cfsd.getName()
                        + " #" + cfsd.getNumber()
                        + " - " + cfsd.getSetName()
                        + " - " + cfsd.getRarity()
                        + " - " + cfsd.getPrinting();
                item.setName(name);
            }
        } else {
            item.setName(cartItemDto.getName());
        }
        item.setImage(cartItemDto.getImage());
        item.setPresentation(cartItemDto.getPresentation());
        item.setFranchise(cartItemDto.getFranchise());
        item.setQuantity(cartItemDto.getQuantity());
        item.setPrice(cartItemDto.getPrice());
        return item;
    }

    private void handleSingleCardStock(CartItemDto cartItemDto) {
        cardForSaleRepository.findById(cartItemDto.getProductOrCardForSaleId())
                .map(card -> {
                    validateStock(card.getStock(), cartItemDto.getQuantity(), "card", card.getId());
                    card.setStock(card.getStock() - cartItemDto.getQuantity());
                    if (card.getStock() == 0) card.setStatus(Status.INACTIVE);
                    return cardForSaleRepository.save(card);
                })
                .orElseThrow(() -> new InsufficientStockException("Card not found: " + cartItemDto.getProductOrCardForSaleId()));
    }

    private void handleProductStock(CartItemDto cartItemDto) {
        productRepository.findById(cartItemDto.getProductOrCardForSaleId())
                .map(product -> {
                    validateStock(product.getStock(), cartItemDto.getQuantity(), "product", product.getProductId());
                    product.setStock(product.getStock() - cartItemDto.getQuantity());
                    if (product.getStock() == 0) product.setStatus(Status.INACTIVE);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new InsufficientStockException("Product not found: " + cartItemDto.getProductOrCardForSaleId()));
    }

    private void validateStock(int availableStock, int requestedQuantity, String type, Object id) {
        if (availableStock < requestedQuantity) {
            throw new InsufficientStockException("Not enough stock for " + type + ": " + id);
        }
    }

}