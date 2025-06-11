package com.example.product_service.controller;

import com.example.product_service.dto.request.ApiResponse;
import com.example.product_service.dto.request.ProductCreationRequest;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {
    ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
        log.info("Creating new product: {}", request.getName());
        ProductResponse response = productService.createProduct(request);
        log.info("Product created with ID: {}", response.getId());

        return ApiResponse.<ProductResponse>builder()
                .code(0)
                .message("Product created successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        log.debug("Fetching all products");
        return ApiResponse.<List<ProductResponse>>builder()
                .code(0)
                .result(productService.getAllProducts())
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable("productId") String productId) {
        log.debug("Fetching product with ID: {}", productId);
        ProductResponse product = productService.getProductById(productId);

        return ApiResponse.<ProductResponse>builder()
                .code(0)
                .result(product)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        log.debug("Searching products with keyword: {}", keyword);
        return ApiResponse.<List<ProductResponse>>builder()
                .code(0)
                .result(productService.searchProducts(keyword))
                .build();
    }

    @GetMapping("/brand/{brand}")
    public ApiResponse<List<ProductResponse>> getProductsByBrand(@PathVariable String brand) {
        log.debug("Fetching products with brand: {}", brand);
        return ApiResponse.<List<ProductResponse>>builder()
                .code(0)
                .result(productService.getProductsByBrand(brand))
                .build();
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable String productId,
            @RequestBody @Valid ProductCreationRequest request) {
        log.info("Updating product with ID: {}", productId);
        ProductResponse response = productService.updateProduct(productId, request);
        log.info("Product updated successfully: {}", productId);

        return ApiResponse.<ProductResponse>builder()
                .code(0)
                .message("Product updated successfully")
                .result(response)
                .build();
    }

    @PatchMapping("/{productId}/availability")
    public ApiResponse<ProductResponse> updateProductAvailability(
            @PathVariable String productId,
            @RequestParam boolean available) {
        log.info("Updating product availability - ID: {}, Available: {}", productId, available);

        ProductResponse response = productService.updateProductAvailability(productId, available);
        log.info("Product availability updated: {}", productId);

        return ApiResponse.<ProductResponse>builder()
                .code(0)
                .message("Product availability updated successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<String> deleteProduct(@PathVariable String productId) {
        log.info("Deleting product with ID: {}", productId);
        productService.deleteProduct(productId);
        log.info("Product deleted successfully: {}", productId);

        return ApiResponse.<String>builder()
                .code(0)
                .message("Product deleted successfully")
                .result("Product has been deleted")
                .build();
    }

    @GetMapping("/batch")
    public ApiResponse<List<ProductResponse>> getProductsByIds(@RequestBody List<String> productIds) {
        log.debug("Fetching multiple products by IDs, count: {}", productIds.size());
        List<ProductResponse> products = productIds.stream()
                .map(id -> {
                    try {
                        return productService.getProductById(id);
                    } catch (RuntimeException e) {
                        log.warn("Product not found with ID: {}", id);
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toList();

        return ApiResponse.<List<ProductResponse>>builder()
                .code(0)
                .result(products)
                .build();
    }
}