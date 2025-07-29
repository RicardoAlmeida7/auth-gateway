package com.zerotrust.auth_gateway.application.usecase.interfaces.user;

import com.zerotrust.auth_gateway.application.dto.request.user.UpdateProfileRequest;

public interface UserProfileUseCase {
    void updateProfile(String userId, UpdateProfileRequest request);
}
