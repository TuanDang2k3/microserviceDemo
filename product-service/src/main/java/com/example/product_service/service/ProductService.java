package com.example.product_service.service;

import com.example.product_service.dto.request.ProductCreationRequest;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreationRequest request) {
        log.info("Creating product: {}", request.getName());
        Product product = productMapper.toProduct(request);

        // Đảm bảo sản phẩm mới luôn có sẵn (available) trừ khi yêu cầu cụ thể là false
        if (!request.isAvailable()) {
            product.setAvailable(false);
        } else {
            product.setAvailable(true);
        }

        product = productRepository.save(product);
        log.info("Product created with ID: {}", product.getId());

        return productMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        log.debug("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        log.debug("Found product: {}, available: {}", product.getName(), product.isAvailable());
        return productMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByBrand(String brand) {
        log.debug("Fetching products with brand: {}", brand);
        return productRepository.findByBrand(brand).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(String id, ProductCreationRequest request) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        productMapper.updateProductFromRequest(request, product);
        product = productRepository.save(product);
        log.info("Product updated: {}", product.getId());

        return productMapper.toProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProductAvailability(String id, boolean available) {
        log.info("Updating product availability - ID: {}, Available: {}", id, available);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Cập nhật trạng thái sản phẩm
        product.setAvailable(available);
        product = productRepository.save(product);
        log.info("Product availability updated: {}", id);

        return productMapper.toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        productRepository.deleteById(id);
        log.info("Product deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        log.debug("Searching products with keyword: {}", keyword);
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}