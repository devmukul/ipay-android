package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

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
