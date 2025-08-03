package com.zerotrust.auth_gateway.domain.model;

import java.util.List;

public record Page<T>(List<T> content, long totalElements, int page, int size) {
}
