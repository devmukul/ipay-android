package bd.com.ipay.ipayskeleton.Api.PromotionApi;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;

public class PromotionResponse {

    @SerializedName("promotions")
    private List<Promotion> promotionList;

    public PromotionResponse() {
    }

    public PromotionResponse(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }

    public List<Promotion> getPromotionList() {
        return promotionList;
    }

    public void setPromotionList(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromotionResponse that = (PromotionResponse) o;
        return (promotionList == that.promotionList) || (promotionList != null && promotionList.equals(that.promotionList));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(promotionList.toArray());
    }

    @Override
    public String toString() {
        return "PromotionResponse{" +
                "promotionList=" + promotionList +
                '}';
    }
}
