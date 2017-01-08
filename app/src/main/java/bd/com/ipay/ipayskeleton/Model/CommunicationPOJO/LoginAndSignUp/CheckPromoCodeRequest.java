package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class CheckPromoCodeRequest {

    private final String mobileNumber;
    private final String deviceId;
    private final String promoCode;
    private final String captcha;

    public CheckPromoCodeRequest(String mobileNumber, String deviceId, String promoCode, String captcha) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.promoCode = promoCode;
        this.captcha = captcha;
    }
}
