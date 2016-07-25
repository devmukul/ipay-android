package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;


public class InvoiceItemList {
    private final String itemDescription;
    private final String itemName;
    private final int quantity;
    private final int rate;
    private final int totalPrice;

    public InvoiceItemList(String itemDescription, String itemName, int quantity, int rate, int totalPrice) {
        this.itemDescription = itemDescription;
        this.itemName = itemName;
        this.quantity = quantity;
        this.rate = rate;
        this.totalPrice = totalPrice;
    }
}
