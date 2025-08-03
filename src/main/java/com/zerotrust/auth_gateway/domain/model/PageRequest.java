package com.zerotrust.auth_gateway.domain.model;

public record PageRequest(int page, int size) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("Page index must not be less than zero!");
        if (size < 1) throw new IllegalArgumentException("Page size must not be less than one!");
    }
}
