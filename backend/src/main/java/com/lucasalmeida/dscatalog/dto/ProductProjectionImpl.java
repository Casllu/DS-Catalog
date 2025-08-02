package com.lucasalmeida.dscatalog.dto;

import com.lucasalmeida.dscatalog.projections.ProductProjection;

public class ProductProjectionImpl implements ProductProjection {
    private Long id;
    private String name;

    public ProductProjectionImpl(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
