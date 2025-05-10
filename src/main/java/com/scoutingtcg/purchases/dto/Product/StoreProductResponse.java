package com.scoutingtcg.purchases.dto.Product;

import com.scoutingtcg.purchases.dto.CardForSale.PokemonSingleResponse;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class StoreProductResponse {

    List<PokemonSingleResponse> singleProducts;
    List<Product> sealedProducts;

}
