package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.math.BigDecimal;

public class SendInvoiceRequest {

    private BigDecimal amount;
    private String clientUserMobileNumber;
    private String description;
    private int requestId;
    private BigDecimal vat;

    public SendInvoiceRequest(BigDecimal amount, String clientUserMobileNumber, String description, int requestId, BigDecimal vat) {
        this.amount = amount;
        this.clientUserMobileNumber = clientUserMobileNumber;
        this.description = description;
        this.requestId = requestId;
        this.vat = vat;
    }
}
