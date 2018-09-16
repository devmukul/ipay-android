package bd.com.ipay.ipayskeleton.Api.PromotionApi;

import com.google.gson.annotations.SerializedName;

public class PromotionClaimRequest {
    @SerializedName("businessAccountId")
    private final int businessAccountId;
    @SerializedName("outletId")
    private final Long outletId;

    public PromotionClaimRequest(int businessAccountId, Long outletId) {
        this.businessAccountId = businessAccountId;
        this.outletId = outletId;
    }

    public int getBusinessAccountId() {
        return businessAccountId;
    }

    public Long getOutletId() {
        return outletId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PromotionClaimRequest that = (PromotionClaimRequest) o;

        if (businessAccountId != that.businessAccountId) return false;
        return outletId != null ? outletId.equals(that.outletId) : that.outletId == null;
    }

    @Override
    public int hashCode() {
        int result = businessAccountId;
        result = 31 * result + (outletId != null ? outletId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PromotionClaimRequest{" +
                "businessAccountId=" + businessAccountId +
                ", outletId=" + outletId +
                '}';
    }
}
