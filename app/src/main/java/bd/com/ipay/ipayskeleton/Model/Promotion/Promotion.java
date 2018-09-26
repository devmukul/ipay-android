package bd.com.ipay.ipayskeleton.Model.Promotion;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import bd.com.ipay.ipayskeleton.Utilities.PromotionConstants;

@SuppressWarnings("unused")
public class Promotion {
	@SerializedName("campaignTitle")
	private String campaignTitle;
	@SerializedName("campaignType")
	private String campaignType;
	@SerializedName("promotionDetails")
	private String promotionDetails;
	@SerializedName("consumptionDetails")
	private String consumptionDetails;
	@SerializedName("imageUrl")
	private String imageUrl;
	@SerializedName("terms")
	private String terms;
	@SerializedName("isActive")
	private boolean isActive;
	@SerializedName("meta")
	private Map<String, String> metaDataMap;

	public Promotion() {
	}

	public Promotion(String campaignTitle, String campaignType, String promotionDetails, String consumptionDetails, String imageUrl, String terms, boolean isActive, Map<String, String> metaDataMap) {

		this.campaignTitle = campaignTitle;
		this.campaignType = campaignType;
		this.promotionDetails = promotionDetails;
		this.consumptionDetails = consumptionDetails;
		this.imageUrl = imageUrl;
		this.terms = terms;
		this.isActive = isActive;
		this.metaDataMap = metaDataMap;
	}

	public String getCampaignTitle() {

		return campaignTitle;
	}

	public void setCampaignTitle(String campaignTitle) {
		this.campaignTitle = campaignTitle;
	}

	public String getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;
	}

	public String getPromotionDetails() {
		return promotionDetails;
	}

	public void setPromotionDetails(String promotionDetails) {
		this.promotionDetails = promotionDetails;
	}

	public String getConsumptionDetails() {
		return consumptionDetails;
	}

	public void setConsumptionDetails(String consumptionDetails) {
		this.consumptionDetails = consumptionDetails;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public Map<String, String> getMetaDataMap() {
		return metaDataMap;
	}

	public void setMetaDataMap(Map<String, String> metaDataMap) {
		this.metaDataMap = metaDataMap;
	}

	public <T extends PromotionMetaData> T getMedata(Class<T> tClassType) {
		final Gson gson = PromotionConstants.promotionConverterGson;
		return gson.fromJson(gson.toJson(metaDataMap), tClassType);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Promotion promotion = (Promotion) o;

		if (isActive != promotion.isActive) return false;
		if (campaignTitle != null ? !campaignTitle.equals(promotion.campaignTitle) : promotion.campaignTitle != null)
			return false;
		if (campaignType != null ? !campaignType.equals(promotion.campaignType) : promotion.campaignType != null)
			return false;
		if (promotionDetails != null ? !promotionDetails.equals(promotion.promotionDetails) : promotion.promotionDetails != null)
			return false;
		if (consumptionDetails != null ? !consumptionDetails.equals(promotion.consumptionDetails) : promotion.consumptionDetails != null)
			return false;
		if (imageUrl != null ? !imageUrl.equals(promotion.imageUrl) : promotion.imageUrl != null)
			return false;
		if (terms != null ? !terms.equals(promotion.terms) : promotion.terms != null) return false;
		return metaDataMap != null ? metaDataMap.equals(promotion.metaDataMap) : promotion.metaDataMap == null;
	}

	@Override
	public int hashCode() {
		int result = campaignTitle != null ? campaignTitle.hashCode() : 0;
		result = 31 * result + (campaignType != null ? campaignType.hashCode() : 0);
		result = 31 * result + (promotionDetails != null ? promotionDetails.hashCode() : 0);
		result = 31 * result + (consumptionDetails != null ? consumptionDetails.hashCode() : 0);
		result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
		result = 31 * result + (terms != null ? terms.hashCode() : 0);
		result = 31 * result + (isActive ? 1 : 0);
		result = 31 * result + (metaDataMap != null ? metaDataMap.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Promotion{" +
				"campaignTitle='" + campaignTitle + '\'' +
				", campaignType='" + campaignType + '\'' +
				", promotionDetails='" + promotionDetails + '\'' +
				", consumptionDetails='" + consumptionDetails + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				", terms='" + terms + '\'' +
				", isActive=" + isActive +
				", metaDataMap=" + metaDataMap +
				'}';
	}


}
