package com.scoutingtcg.purchases.util;


import com.scoutingtcg.purchases.dto.PageResponse;
import org.springframework.data.domain.Page;

public class PageUtils {
    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}