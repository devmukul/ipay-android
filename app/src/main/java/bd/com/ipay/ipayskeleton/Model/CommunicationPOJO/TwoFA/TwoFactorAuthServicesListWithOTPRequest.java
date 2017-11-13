package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFactorAuthService;

public class TwoFactorAuthServicesListWithOTPRequest {
    String otp;
    List<TwoFactorAuthService> services;

    public TwoFactorAuthServicesListWithOTPRequest(String otp, List<TwoFactorAuthService> mTwoFactorAuthService) {
        this.otp = otp;
        this.services = mTwoFactorAuthService;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public List<TwoFactorAuthService> getmTwoFaService() {
        return services;
    }

    public void setmTwoFaService(List<TwoFactorAuthService> mTwoFactorAuthService) {
        this.services = mTwoFactorAuthService;
    }
}
