package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RefreshToken;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;

public class GetRefreshTokenRequest {

    private final String refreshToken;
    private final boolean isRemember;

    public GetRefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
        this.isRemember = SharedPrefManager.isRemberMeActive();
    }
}
