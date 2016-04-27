package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class CheckPromoCodeRequest {

    private String mobileNumber;
    private String deviceId;
    private String promoCode;
    private String captcha;

    public CheckPromoCodeRequest(String mobileNumber, String deviceId, String promoCode, String captcha) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.promoCode = promoCode;
        this.captcha = captcha;
    }
}
