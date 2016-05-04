package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;

public class SignupRequestPersonal {

    private String mobileNumber;
    private String deviceId;
    private String name;
    private String dob;
    private String password;
    private String gender;
    private String otp;
    private String promoCode;
    private int accountType;
    private AddressClass presentAddress;

    public SignupRequestPersonal(String mobileNumber, String deviceId, String name,
                                 String dob, String password, String gender,
                                 String otp, String promoCode, int accountType, AddressClass presentAddress) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.name = name;
        this.dob = dob;
        this.password = password;
        this.gender = gender;
        this.otp = otp;
        this.promoCode = promoCode;
        this.accountType = accountType;
        this.presentAddress = presentAddress;
    }
}
