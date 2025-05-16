package com.scoutingtcg.purchases.product.controller;

import com.scoutingtcg.purchases.product.service.ProductService;
import com.scoutingtcg.purchases.product.dto.ProductRequest;
import com.scoutingtcg.purchases.product.model.Product;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product createProduct(@RequestPart("product") ProductRequest productRequest,
                                 @RequestPart("file") MultipartFile file) {
        return productService.createProduct(productRequest, file);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product updateProduct(@RequestPart("product") ProductRequest productRequest,
                                 @RequestPart(value = "file", required = false) MultipartFile file) {
        return productService.updateProduct(productRequest, file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
