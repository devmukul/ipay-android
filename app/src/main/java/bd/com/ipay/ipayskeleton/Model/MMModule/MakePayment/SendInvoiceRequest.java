package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.math.BigDecimal;

public class SendInvoiceRequest {

    private final BigDecimal amount;
    private final String clientUserMobileNumber;
    private final String description;
    private final int requestId;
    private final BigDecimal vat;

    public SendInvoiceRequest(BigDecimal amount, String clientUserMobileNumber, String description, int requestId, BigDecimal vat) {
        this.amount = amount;
        this.clientUserMobileNumber = clientUserMobileNumber;
        this.description = description;
        this.requestId = requestId;
        this.vat = vat;
    }
}
