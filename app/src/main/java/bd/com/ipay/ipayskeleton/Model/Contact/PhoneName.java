package bd.com.ipay.ipayskeleton.Model.Contact;

public class PhoneName {

    public PhoneName(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public PhoneName() {

    }

    public int id;
    public String name;
    public String number;
    public String type;
    public String starred;
    public int state;
}
