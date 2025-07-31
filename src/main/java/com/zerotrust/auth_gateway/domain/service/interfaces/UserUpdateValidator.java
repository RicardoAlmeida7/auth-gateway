package com.zerotrust.auth_gateway.domain.service.interfaces;

import com.zerotrust.auth_gateway.domain.model.User;

public interface UserUpdateValidator {
    void updateUsernameIfNeeded(User user, String newUsername);
    void updateEmailIfNeeded(User user, String newEmail);
    void updatePasswordIfNeeded(User user, String newPassword, String confirmPassword);
}
