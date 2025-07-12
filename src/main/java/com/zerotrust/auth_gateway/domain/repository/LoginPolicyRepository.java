package com.zerotrust.auth_gateway.domain.repository;

import com.zerotrust.auth_gateway.domain.model.LoginPolicy;

public interface LoginPolicyRepository {
    LoginPolicy get();
    void update(LoginPolicy policy);
}
