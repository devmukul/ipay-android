package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.math.BigDecimal;

public class CreateInvoiceRequest {

    private String item;
    private BigDecimal amount;
    private BigDecimal vat;
    private String clientUserMobileNumber;
    private String pin;

    public CreateInvoiceRequest(String item, BigDecimal amount, BigDecimal vat, String clientUserMobileNumber, String pin) {
        this.item = item;
        this.amount = amount;
        this.vat = vat;
        this.clientUserMobileNumber = clientUserMobileNumber;
        this.pin = pin;
    }
}
