package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;

public class SignupRequestBusiness {

    private final String mobileNumber;
    private final String deviceId;
    private final String name;
    private final int accountType;
    private final String dob;
    private final String password;
    private final String otp;
    private final String businessName;
    private final long businessType;
    private final String personalEmail;
    private final String personalMobileNumber;
    private final AddressClass personalAddress;
    private final AddressClass businessAddress;

    private SignupRequestBusiness(Builder builder) {
        mobileNumber = builder.mobileNumber;
        deviceId = builder.deviceId;
        name = builder.name;
        accountType = builder.accountType;
        dob = builder.dob;
        password = builder.password;
        otp = builder.otp;
        businessName = builder.businessName;
        businessType = builder.businessType;
        personalEmail = builder.personalEmail;
        personalMobileNumber = builder.personalMobileNumber;
        personalAddress = builder.personalAddress;
        businessAddress = builder.businessAddress;
    }

    public static final class Builder {
        private String mobileNumber;
        private String deviceId;
        private String name;
        private int accountType;
        private String dob;
        private String password;
        private String otp;
        private String businessName;
        private long businessType;
        private String personalEmail;
        private String personalMobileNumber;
        private AddressClass personalAddress;
        private AddressClass businessAddress;

        public Builder() {
        }


        public Builder mobileNumber(String val) {
            mobileNumber = val;
            return this;
        }

        public Builder deviceId(String val) {
            deviceId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder accountType(int val) {
            accountType = val;
            return this;
        }

        public Builder dob(String val) {
            dob = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder otp(String val) {
            otp = val;
            return this;
        }

        public Builder businessName(String val) {
            businessName = val;
            return this;
        }

        public Builder businessType(long val) {
            businessType = val;
            return this;
        }

        public Builder personalEmail(String val) {
            personalEmail = val;
            return this;
        }

        public Builder personalMobileNumber(String val) {
            personalMobileNumber = val;
            return this;
        }

        public Builder personalAddress(AddressClass val) {
            personalAddress = val;
            return this;
        }

        public Builder businessAddress(AddressClass val) {
            businessAddress = val;
            return this;
        }

        public SignupRequestBusiness build() {
            return new SignupRequestBusiness(this);
        }
    }
}
