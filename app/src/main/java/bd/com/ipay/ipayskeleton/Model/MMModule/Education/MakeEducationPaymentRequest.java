package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class MakeEducationPaymentRequest {
    private String description;
    private String pin;
    private EducationInvoice educationalInvoice;

    public MakeEducationPaymentRequest(String description, String pin, EducationInvoice educationalInvoice) {
        this.description = description;
        this.pin = pin;
        this.educationalInvoice = educationalInvoice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public EducationInvoice getEducationalInvoice() {
        return educationalInvoice;
    }

    public void setEducationalInvoice(EducationInvoice educationalInvoice) {
        this.educationalInvoice = educationalInvoice;
    }
}
