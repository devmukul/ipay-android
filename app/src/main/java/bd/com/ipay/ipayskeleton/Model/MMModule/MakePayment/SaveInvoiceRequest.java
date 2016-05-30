package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

public class SaveInvoiceRequest {
    private String clientEmailAddress;
    private String clientUserMobileNumber;
    private String message;
    private int vat;
    InvoiceItemList[] InvoiceItemList;

    public SaveInvoiceRequest(String clientEmailAddress, String clientUserMobileNumber, String message, int vat, InvoiceItemList[] invoiceItemList) {
        this.clientEmailAddress = clientEmailAddress;
        this.clientUserMobileNumber = clientUserMobileNumber;
        this.message = message;
        this.vat = vat;
        this.InvoiceItemList = invoiceItemList;
    }
}
