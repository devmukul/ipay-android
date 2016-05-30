package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.util.List;

public class SaveInvoiceRequest {
    private String clientEmailAddress;
    private String clientUserMobileNumber;
    private String message;
    private int vat;
    private List<InvoiceItemList> invoiceItemList;

    public SaveInvoiceRequest(String clientEmailAddress, String clientUserMobileNumber, String message, int vat, List<InvoiceItemList> invoiceItemList) {
        this.clientEmailAddress = clientEmailAddress;
        this.clientUserMobileNumber = clientUserMobileNumber;
        this.message = message;
        this.vat = vat;
        this.invoiceItemList = invoiceItemList;
    }
}
