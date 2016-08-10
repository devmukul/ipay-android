package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;

public class SignupRequestBusiness {

    private final String mobileNumber;
    private final String deviceId;
    private final String name;
    private final int accountType;
    private final String dob;
    private final String password;
    private final String gender;
    private final String otp;
    private final String businessName;
    private final long businessType;
    private final String personalEmail;
    private final String personalMobileNumber;
    private final AddressClass personalAddress;
    private final AddressClass businessAddress;
    private final String promoCode;

    public SignupRequestBusiness(String mobileNumber, String deviceId,
                                 String name, int accountType, String dob,
                                 String password, String gender, String otp, String businessName,
                                 long businessType, String businessEmail, String personalEmail,
                                 String personalMobileNumber, AddressClass personalAddress,
                                 AddressClass businessAddress, String promoCode) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.name = name;
        this.accountType = accountType;
        this.dob = dob;
        this.password = password;
        this.gender = gender;
        this.otp = otp;
        this.businessName = businessName;
        this.businessType = businessType;
        this.personalEmail = personalEmail;
        this.personalMobileNumber = personalMobileNumber;
        this.personalAddress = personalAddress;
        this.businessAddress = businessAddress;
        this.promoCode = promoCode;
    }
}
