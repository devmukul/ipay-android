package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;

public class SignupRequestPersonal {

    private final String mobileNumber;
    private final String deviceId;
    private final String name;
    private final long dob;
    private final String password;
    private final String otp;
    private final String promoCode;
    private final int accountType;
    private final AddressClass presentAddress;

    public SignupRequestPersonal(String mobileNumber, String deviceId, String name,
                                 Long dob, String password,
                                 String otp, String promoCode, int accountType, AddressClass presentAddress) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.name = name;
        this.dob = dob;
        this.password = password;
        this.otp = otp;
        this.promoCode = promoCode;
        this.accountType = accountType;
        this.presentAddress = presentAddress;
    }
}
