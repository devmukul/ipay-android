package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

public class SaveInvoiceResponse {

    private String message;
    private int invoiceId;

    public SaveInvoiceResponse() {

    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public String getMessage() {
        return message;
    }
}
