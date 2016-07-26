package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class Institution implements Resource, Parcelable {

    private int instituteId;
    private String address;
    private String contact;
    private String email;
    private String name;
    private int ipayAccountId;
    private int vat;
    private String responsibleContactPersonName;
    private int responsibleContactPersonMobileNumber;
    private String responsibleContactPersonAddress;
    private String responsibleContactPersonEmail;

    public int getId() {
        return instituteId;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getIpayAccountId() {
        return ipayAccountId;
    }

    public int getVat() {
        return vat;
    }

    public String getResponsibleContactPersonName() {
        return responsibleContactPersonName;
    }

    public int getResponsibleContactPersonMobileNumber() {
        return responsibleContactPersonMobileNumber;
    }

    public String getResponsibleContactPersonAddress() {
        return responsibleContactPersonAddress;
    }

    public String getResponsibleContactPersonEmail() {
        return responsibleContactPersonEmail;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.instituteId);
        dest.writeString(this.address);
        dest.writeString(this.contact);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeInt(this.ipayAccountId);
        dest.writeInt(this.vat);
        dest.writeString(this.responsibleContactPersonName);
        dest.writeInt(this.responsibleContactPersonMobileNumber);
        dest.writeString(this.responsibleContactPersonAddress);
        dest.writeString(this.responsibleContactPersonEmail);
    }

    public Institution() {
    }

    protected Institution(Parcel in) {
        this.instituteId = in.readInt();
        this.address = in.readString();
        this.contact = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.ipayAccountId = in.readInt();
        this.vat = in.readInt();
        this.responsibleContactPersonName = in.readString();
        this.responsibleContactPersonMobileNumber = in.readInt();
        this.responsibleContactPersonAddress = in.readString();
        this.responsibleContactPersonEmail = in.readString();
    }

    public static final Parcelable.Creator<Institution> CREATOR = new Parcelable.Creator<Institution>() {
        @Override
        public Institution createFromParcel(Parcel source) {
            return new Institution(source);
        }

        @Override
        public Institution[] newArray(int size) {
            return new Institution[size];
        }
    };
}

