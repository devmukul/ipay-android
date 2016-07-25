package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.math.BigDecimal;

public class ItemList {
    private Long id;
    private BigDecimal rate;
    private BigDecimal quantity;
    private String item;
    private String description;
    private BigDecimal amount;

    public ItemList() {
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
