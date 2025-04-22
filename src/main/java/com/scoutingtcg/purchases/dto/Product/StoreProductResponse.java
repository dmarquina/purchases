package com.scoutingtcg.purchases.dto.Product;

import com.scoutingtcg.purchases.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class StoreProductResponse {

    List<Product> singleProducts;
    List<Product> sealedProducts;

}
