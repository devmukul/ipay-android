package bd.com.ipay.ipayskeleton.Model.FireBase;

public class UserPersonal {

    private String mobileNumber;
    private String deviceId;
    private String firstName;
    private String lastName;
    private String dob;
    private String passwordHash;
    private String gender;

    public UserPersonal() {
    }

    public UserPersonal(String mobileNumber, String deviceId, String firstName,
                        String lastName, String dob, String passwordHash, String gender) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.passwordHash = passwordHash;
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getGender() {
        return gender;
    }
}
