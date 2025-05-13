package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.model.ProductImage;
import com.scoutingtcg.purchases.repository.ProductImageRepository;
import com.scoutingtcg.purchases.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ProductImageRepository productImageRepository;

    private final String productBucketName;

    public ProductService(ProductRepository productRepository,
                          S3ClientService s3ClientService,
                          ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.s3ClientService = s3ClientService;
        this.productImageRepository = productImageRepository;
        this.productBucketName = s3ClientService.getProductsBucket();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getSealedProducts(String franchise, Pageable pageable) {
        return productRepository.findByFranchiseAndPresentationNotAndStockGreaterThan(franchise, "Single", pageable,0);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product, MultipartFile file) {
        return saveProductAndHandleImageUpload(product, file);
    }

    public Product updateProduct(Product product, MultipartFile file) {
        Product product1WithOldImage = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));


        if (file != null && !file.isEmpty()) {
            if (product1WithOldImage.getCoverImageUrl() != null && !product1WithOldImage.getCoverImageUrl().isEmpty()) {
                s3ClientService.deleteFile(productBucketName, product1WithOldImage.getCoverImageUrl());
            }
            return saveProductAndHandleImageUpload(product, file);
        } else {
            product.setCoverImageUrl(product1WithOldImage.getCoverImageUrl());
            return productRepository.save(product);
        }

    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .ifPresent(product -> {
                    if (product.getCoverImageUrl() != null && !product.getCoverImageUrl().isEmpty()) {
                        s3ClientService.deleteFile(productBucketName, product.getCoverImageUrl());
                    }
                    productImageRepository.deleteAllByProduct(product);
                    productRepository.deleteById(id);
                });
    }

    public Product saveProductAndHandleImageUpload(Product product, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String imageUrl = s3ClientService.uploadFile(productBucketName, fileName, file.getInputStream(), file.getContentType());
            product.setCoverImageUrl(imageUrl);
            product.setStock(0);
            Product savedProduct = productRepository.save(product);

            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setImageUrl(imageUrl);
            productImageRepository.save(productImage);
            return savedProduct;
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

}
