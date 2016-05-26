package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.math.BigDecimal;


public class itemList {
    public Long id;
    public BigDecimal rate;
    public BigDecimal quantity;
    public String item;
    public String description;
    public BigDecimal amount;

    public itemList() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getItem() {
        return item;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
