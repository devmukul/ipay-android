package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.util.List;

public class SaveInvoiceRequest {
    private final String clientEmailAddress;
    private final String clientUserMobileNumber;
    private final String message;
    private final int vat;
    private final List<InvoiceItemList> invoiceItemList;

    public SaveInvoiceRequest(String clientEmailAddress, String clientUserMobileNumber, String message, int vat, List<InvoiceItemList> invoiceItemList) {
        this.clientEmailAddress = clientEmailAddress;
        this.clientUserMobileNumber = clientUserMobileNumber;
        this.message = message;
        this.vat = vat;
        this.invoiceItemList = invoiceItemList;
    }
}
