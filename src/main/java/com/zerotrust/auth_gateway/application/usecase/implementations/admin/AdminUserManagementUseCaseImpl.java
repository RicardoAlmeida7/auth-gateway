package com.zerotrust.auth_gateway.application.usecase.implementations.admin;

import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.user.AdminUpdateUserRequest;
import com.zerotrust.auth_gateway.application.dto.response.user.ManagedUserResponse;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.MfaManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.admin.AdminUserManagementUseCase;
import com.zerotrust.auth_gateway.domain.exception.InvalidEmailException;
import com.zerotrust.auth_gateway.domain.exception.InvalidUsernameException;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.EmailService;
import com.zerotrust.auth_gateway.domain.service.interfaces.UserUpdateValidator;
import com.zerotrust.auth_gateway.domain.validation.EmailValidator;
import com.zerotrust.auth_gateway.domain.validation.RoleValidator;
import com.zerotrust.auth_gateway.domain.validation.UsernameValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdminUserManagementUseCaseImpl implements AdminUserManagementUseCase {

    private final UserRepository userRepository;
    private final MfaManagementUseCase mfaManagementUseCase;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;
    private final UserUpdateValidator userUpdateValidator;

    public AdminUserManagementUseCaseImpl(
            UserRepository userRepository,
            MfaManagementUseCase mfaManagementUseCase,
            EmailService emailService,
            JwtTokenService jwtTokenService,
            UserUpdateValidator userUpdateValidator
    ) {
        this.userRepository = userRepository;
        this.mfaManagementUseCase = mfaManagementUseCase;
        this.emailService = emailService;
        this.jwtTokenService = jwtTokenService;
        this.userUpdateValidator = userUpdateValidator;
    }

    @Override
    public void deleteUser(String id) {
        try {
            UUID userId = UUID.fromString(id);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found."));

            userRepository.delete(user);
        } catch (IllegalArgumentException e) {
            throw new UserNotFoundException("Invalid user id.");
        }
    }

    @Override
    public List<ManagedUserResponse> getUsers() {
        return userRepository
                .getAll()
                .stream()
                .map(user ->
                        new ManagedUserResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.isEnabled(),
                                user.getRoles()
                        )
                ).toList();
    }

    @Override
    public ManagedUserResponse createUser(RegisterRequest request) {
        validateRegisterRequest(request);
        validateExistUser(request);

        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                "",
                request.getEmail(),
                request.isMfaEnabled(),
                null,
                false,
                request.getRoles(),
                true
        );
        String qrCodeUrl = mfaManagementUseCase.generateMfaSetupIfEnabled(user);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
        return new ManagedUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled(), user.getRoles());
    }

    @Override
    public ManagedUserResponse updateUser(String userId, AdminUpdateUserRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        userUpdateValidator.updateUsernameIfNeeded(user, request.username());
        userUpdateValidator.updateEmailIfNeeded(user, request.email());

        if (!request.roles().isEmpty()) {
            RoleValidator.validate(request.roles());
            user.setRoles(request.roles());
        }

        userRepository.save(user);

        return new ManagedUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled(), user.getRoles());
    }

    private void validateExistUser(RegisterRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isPresent()) throw new InvalidUsernameException("");

        user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) throw new InvalidEmailException("");
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) throw new UserNotFoundException("No user found with provided email or username.");
        UsernameValidator.validate(request.getUsername());
        EmailValidator.validate(request.getEmail());
        RoleValidator.validate(request.getRoles());
    }

    private void sendActivationEmail(User user, String qrCodeUrl) {
        String activationToken = jwtTokenService.generateActivationToken(user);
        // TODO: Move base URL to an environment variable or configuration file
        String activationLink = "http://localhost:8080/api/v1/user/activate?token=" + activationToken;
        emailService.sendActivationEmail(user.getEmail(), activationToken, activationLink, qrCodeUrl);
    }
}
