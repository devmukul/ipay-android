package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class SetUserAddressRequest {
    private String addressType;
    private AddressClass address;

    public SetUserAddressRequest() {

    }

    public SetUserAddressRequest(String addressType, AddressClass address) {
        this.addressType = addressType;
        this.address = address;
    }
}
