package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetUserAddressResponse {
    private String message;
    private List<AddressContainer> addressList;

    public String getMessage() {
        return message;
    }

    public List<AddressContainer> getAddressList() {
        return addressList;
    }

    public AddressClass getPresentAddress() {
        for (AddressContainer addressContainer : addressList) {
            if (addressContainer.getAddressType().equals(Constants.ADDRESS_TYPE_PRESENT))
                return addressContainer.getAddress();
        }
        return null;
    }

    public AddressClass getPermanentAddress() {
        for (AddressContainer addressContainer : addressList) {
            if (addressContainer.getAddressType().equals(Constants.ADDRESS_TYPE_PERMANENT))
                return addressContainer.getAddress();
        }
        return null;
    }

    public AddressClass getOfficeAddress() {
        for (AddressContainer addressContainer : addressList) {
            if (addressContainer.getAddressType().equals(Constants.ADDRESS_TYPE_OFFICE))
                return addressContainer.getAddress();
        }
        return null;
    }

    public static class AddressContainer {
        private String addressType;
        private AddressClass address;

        public String getAddressType() {
            return addressType;
        }

        public AddressClass getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "AddressContainer{" +
                    "addressType='" + addressType + '\'' +
                    ", address=" + address +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetUserAddressResponse{" +
                "message='" + message + '\'' +
                ", addressList=" + addressList +
                '}';
    }
}
