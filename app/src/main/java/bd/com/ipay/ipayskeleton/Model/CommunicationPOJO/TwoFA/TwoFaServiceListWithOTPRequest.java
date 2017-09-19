package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFAService;

public class TwoFaServiceListWithOTPRequest {
    String otp;
    List<TwoFAService> services;

    public TwoFaServiceListWithOTPRequest(String otp, List<TwoFAService> mTwoFaService) {
        this.otp = otp;
        this.services = mTwoFaService;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public List<TwoFAService> getmTwoFaService() {
        return services;
    }

    public void setmTwoFaService(List<TwoFAService> mTwoFaService) {
        this.services = mTwoFaService;
    }
}
