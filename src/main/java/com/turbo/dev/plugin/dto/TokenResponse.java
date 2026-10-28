package com.turbo.dev.plugin.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TokenResponse {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("refresh_expires_in")
    public int refreshExpiresIn;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("not-before-policy")
    public int notBeforePolicy;

    @SerializedName("session_state")
    public String sessionState;

    public String scope;
}

