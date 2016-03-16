package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class GetUserAddressResponse {
    private AddressClass presentAddress;
    private AddressClass permanentAddress;
    private AddressClass officeAddress;

    public GetUserAddressResponse() {

    }

    public AddressClass getPresentAddress() {
        return presentAddress;
    }

    public AddressClass getPermanentAddress() {
        return permanentAddress;
    }

    public AddressClass getOfficeAddress() {
        return officeAddress;
    }
}
