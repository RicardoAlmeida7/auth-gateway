package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.dto.request.UpdateLoginPolicyRequest;
import com.zerotrust.auth_gateway.application.dto.response.LoginPolicyResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.LoginPolicyUseCase;
import com.zerotrust.auth_gateway.domain.model.LoginPolicy;
import com.zerotrust.auth_gateway.domain.repository.LoginPolicyRepository;

public class LoginPolicyUseCaseImpl implements LoginPolicyUseCase {

    private final LoginPolicyRepository loginPolicyRepository;

    public LoginPolicyUseCaseImpl(LoginPolicyRepository loginPolicyRepository) {
        this.loginPolicyRepository = loginPolicyRepository;
    }

    @Override
    public LoginPolicyResponse getPolicy() {
        try {
            LoginPolicy loginPolicy = loginPolicyRepository.get();
            return new LoginPolicyResponse(loginPolicy.getMaxAttempts(), loginPolicy.getLockTimeMillis());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve the login policy configuration.");
        }
    }

    @Override
    public void updatePolicy(UpdateLoginPolicyRequest request) {
        validateUpdateLoginPolicyRequest(request);
        LoginPolicy policy = loginPolicyRepository.get();
        policy.setMaxAttempts(request.maxAttempts());
        policy.setLockTimeMillis(request.lockTimeMillis());
        loginPolicyRepository.update(policy);
    }

    private void validateUpdateLoginPolicyRequest(UpdateLoginPolicyRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Login policy update request must not be null.");
        }

        if (request.maxAttempts() <= 0) {
            throw new IllegalArgumentException("The maximum number of login attempts must be greater than 0.");
        }

        if (request.lockTimeMillis() < 0) {
            throw new IllegalArgumentException("The lock duration must not be negative.");
        }
    }
}
