package bd.com.ipay.ipayskeleton.Model.Promotion;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PromotionMetaData {
	@SerializedName("startDate")
	private Date startDate;
	@SerializedName("endDate")
	private Date endDate;

	public PromotionMetaData() {
	}

	public PromotionMetaData(Date startDate, Date endDate) {

		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PromotionMetaData that = (PromotionMetaData) o;

		if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null)
			return false;
		return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
	}

	@Override
	public int hashCode() {
		int result = startDate != null ? startDate.hashCode() : 0;
		result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PromotionMetaData{" +
				"startDate=" + startDate +
				", endDate=" + endDate +
				'}';
	}
}
