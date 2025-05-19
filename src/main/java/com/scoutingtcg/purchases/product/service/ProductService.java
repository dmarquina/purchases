package com.scoutingtcg.purchases.product.service;

import com.scoutingtcg.purchases.product.dto.ProductRequest;
import com.scoutingtcg.purchases.product.model.Product;
import com.scoutingtcg.purchases.product.repository.ProductRepository;
import com.scoutingtcg.purchases.shared.integration.S3ClientService;
import com.scoutingtcg.purchases.shared.model.ShippingSize;
import com.scoutingtcg.purchases.shared.model.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final S3ClientService s3ClientService;
    private final ProductRepository productRepository;

    private final String productBucketName;

    public ProductService(ProductRepository productRepository,
                          S3ClientService s3ClientService) {
        this.productRepository = productRepository;
        this.s3ClientService = s3ClientService;
        this.productBucketName = s3ClientService.getProductsBucket();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductRequest productRequest, MultipartFile file) {
        Product product = new Product();
        mapProductRequestToProduct(product, productRequest);

        product.setStock(0);
        product.setStatus(Status.ACTIVE);

        String imageUrl = uploadImage(file);
        product.setCoverImageUrl(imageUrl);

        return productRepository.save(product);
    }

    public Product updateProduct(ProductRequest productRequest, MultipartFile file) {
        Product product = productRepository.findById(productRequest.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (file != null && !file.isEmpty()) {
            if (product.getCoverImageUrl() != null && !product.getCoverImageUrl().isEmpty()) {
                s3ClientService.deleteFile(productBucketName, product.getCoverImageUrl());
            }
            String imageUrl = uploadImage(file);
            product.setCoverImageUrl(imageUrl);
        }
        mapProductRequestToProduct(product, productRequest);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .ifPresent(product -> {
                    if (product.getCoverImageUrl() != null && !product.getCoverImageUrl().isEmpty()) {
                        s3ClientService.deleteFile(productBucketName, product.getCoverImageUrl());
                    }
                    productRepository.deleteById(id);
                });
    }

    public String uploadImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            return s3ClientService.uploadFile(productBucketName, fileName, file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    private void mapProductRequestToProduct(Product product, ProductRequest productRequest) {
        product.setProductName(productRequest.productName());
        product.setCurrentPrice(productRequest.currentPrice());
        product.setFranchise(productRequest.franchise());
        product.setPresentation(productRequest.presentation());
        product.setShippingSize(ShippingSize.valueOf(productRequest.shippingSize().toUpperCase()));
    }

}
