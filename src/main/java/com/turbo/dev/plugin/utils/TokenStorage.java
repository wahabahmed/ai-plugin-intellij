package com.turbo.dev.plugin.utils;

import com.turbo.dev.plugin.dto.TokenResponse;
import com.turbo.dev.plugin.state.TurboDevSettings;

public class TokenStorage {
    public static String getAccessToken() {
        TurboDevSettings settings = TurboDevSettings.getInstance();
        String token = settings.getState().accessToken;
        return token != null ? token : null;
    }

    public static void setAccessToken(TokenResponse token) {
        TurboDevSettings settings = TurboDevSettings.getInstance();
        if (settings != null) {
            settings.getState().accessToken = token.getAccessToken();

            long now = System.currentTimeMillis() / 1000L;
            settings.getState().expiry = String.valueOf(now + token.getExpiresIn());
        }
    }

    public static void removeAccessToken() {
        TurboDevSettings settings = TurboDevSettings.getInstance();
        if (settings != null) {
            settings.getState().accessToken = null;
            settings.getState().expiry = null;
        }
    }

    public static boolean isTokenExpired() {
        TurboDevSettings settings = TurboDevSettings.getInstance();
        if (null != settings && null != settings.getState().expiry) {
            int expiry = Integer.parseInt(settings.getState().expiry);
            long currentTimeSeconds = System.currentTimeMillis() / 1000L;
            return currentTimeSeconds > expiry;
        }
        return true;
    }
}
