package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.request.UpdateLoginPolicyRequest;
import com.zerotrust.auth_gateway.application.dto.response.LoginPolicyResponse;

public interface LoginPolicyUseCase {
    LoginPolicyResponse getPolicy();
    void updatePolicy(UpdateLoginPolicyRequest request);
}
