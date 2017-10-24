package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Pay;

public class PayDashBoardIconProperty {
    private final String title;
    private final String imageUrl;
    private final int serviceId;
    private final String operatorPrefix;


    public PayDashBoardIconProperty(String title, String imageUrl, int serviceId, String operatorPrefix) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.serviceId = serviceId;
        this.operatorPrefix = operatorPrefix;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getOperatorPrefix() {
        return operatorPrefix;
    }

    @Override
    public String toString() {
        return "BillPayMarchantClass{" +
                "title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", operatorPrefix='" + operatorPrefix + '\'' +
                '}';
    }
}