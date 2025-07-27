package com.zerotrust.auth_gateway.domain.utils;

public class Constants {

    public static long REFRESH_TOKEN_TTL_SECONDS = 172800L;
    public static long ACTIVATION_TOKEN_TTL_SECONDS = 86400L;
    public static long AUTHENTICATION_TOKEN_TTL_SECONDS = 600L;
    public static long RESET_PASSWORD_TOKEN_TTL_SECONDS = 3600L;

    public static String ACTIVATION_TOKEN_TYPE = "activation_token";
    public static String REFRESH_TOKEN_TYPE = "refresh_token";
    public static String RESET_PASSWORD_TOKEN_TYPE = "reset_password_token";
}
