package com.zerotrust.auth_gateway.application.usecase.interfaces.auth;

import com.zerotrust.auth_gateway.application.dto.request.policy.UpdateLoginPolicyRequest;
import com.zerotrust.auth_gateway.application.dto.response.policy.LoginPolicyResponse;

public interface LoginPolicyUseCase {
    LoginPolicyResponse getPolicy();
    void updatePolicy(UpdateLoginPolicyRequest request);
}
