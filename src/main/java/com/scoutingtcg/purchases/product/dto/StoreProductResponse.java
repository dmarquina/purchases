package com.scoutingtcg.purchases.product.dto;

import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonSingleResponse;
import com.scoutingtcg.purchases.product.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class StoreProductResponse {

    List<PokemonSingleResponse> singleProducts;
    List<Product> sealedProducts;

}
