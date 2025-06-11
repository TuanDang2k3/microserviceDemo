package com.example.product_service.mapper;

import com.example.product_service.dto.request.ProductCreationRequest;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);

    Product toProduct(ProductCreationRequest request);

    void updateProductFromRequest(ProductCreationRequest request, @MappingTarget Product product);
}