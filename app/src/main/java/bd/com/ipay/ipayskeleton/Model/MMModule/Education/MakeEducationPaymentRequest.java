package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class MakeEducationPaymentRequest {
    private String description;
    private String pin;
    private EducationInvoice educationalInvoiceDBDTO;
    private InvoicePayableAccountRelation[] invoicePayableAccountRelationDBDTOList;

    public MakeEducationPaymentRequest(String description, String pin, EducationInvoice educationalInvoiceDBDTO, InvoicePayableAccountRelation[] invoicePayableAccountRelationDBDTOList) {
        this.description = description;
        this.pin = pin;
        this.educationalInvoiceDBDTO = educationalInvoiceDBDTO;
        this.invoicePayableAccountRelationDBDTOList = invoicePayableAccountRelationDBDTOList;
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

    public EducationInvoice getEducationalInvoiceDBDTO() {
        return educationalInvoiceDBDTO;
    }

    public void setEducationalInvoiceDBDTO(EducationInvoice educationalInvoiceDBDTO) {
        this.educationalInvoiceDBDTO = educationalInvoiceDBDTO;
    }

    public InvoicePayableAccountRelation[] getInvoicePayableAccountRelationDBDTOList() {
        return invoicePayableAccountRelationDBDTOList;
    }

    public void setInvoicePayableAccountRelationDBDTOList(InvoicePayableAccountRelation[] invoicePayableAccountRelationDBDTOList) {
        this.invoicePayableAccountRelationDBDTOList = invoicePayableAccountRelationDBDTOList;
    }
}
