package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class Institution {

    private long institutionId;
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

    public long getId() {
        return institutionId;
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
}

