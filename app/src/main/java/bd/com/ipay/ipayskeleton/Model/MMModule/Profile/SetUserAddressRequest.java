package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class SetUserAddressRequest {
    private AddressClass presentAddress;
    private AddressClass permanentAddress;
    private AddressClass officeAddress;

    public SetUserAddressRequest() {

    }

    public SetUserAddressRequest(AddressClass presentAddress, AddressClass permanentAddress, AddressClass officeAddress) {
        this.presentAddress = presentAddress;
        this.permanentAddress = permanentAddress;
        this.officeAddress = officeAddress;
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
