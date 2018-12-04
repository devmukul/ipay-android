package bd.com.ipay.android.viewmodel;

import android.support.annotation.Nullable;

import java.util.Calendar;

public class TransactionHistoryRepositoryArguments extends RepositoryArguments {
	@Nullable
	private final Integer serviceId;
	@Nullable
	private final Calendar fromDate;
	@Nullable
	private final Calendar toDate;
	@Nullable
	private final String searchText;

	public TransactionHistoryRepositoryArguments(@Nullable final Integer serviceId,
	                                             @Nullable final Calendar fromDate,
	                                             @Nullable final Calendar toDate,
	                                             @Nullable final String searchText) {
		super(DEFAULT_PAGE_SIZE);
		this.serviceId = serviceId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.searchText = searchText;
	}

	public TransactionHistoryRepositoryArguments(int pageSize,
	                                             @Nullable final Integer serviceId,
	                                             @Nullable final Calendar fromDate,
	                                             @Nullable final Calendar toDate,
	                                             @Nullable final String searchText) {
		super(pageSize);
		this.serviceId = serviceId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.searchText = searchText;
	}

	@Nullable
	public Integer getServiceId() {
		return serviceId;
	}

	@Nullable
	public Calendar getFromDate() {
		return fromDate;
	}

	@Nullable
	public Calendar getToDate() {
		return toDate;
	}

	@Nullable
	public String getSearchText() {
		return searchText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TransactionHistoryRepositoryArguments that = (TransactionHistoryRepositoryArguments) o;

		if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null)
			return false;
		if (fromDate != null ? !fromDate.equals(that.fromDate) : that.fromDate != null)
			return false;
		if (toDate != null ? !toDate.equals(that.toDate) : that.toDate != null) return false;
		return searchText != null ? searchText.equals(that.searchText) : that.searchText == null;
	}

	@Override
	public int hashCode() {
		int result = serviceId != null ? serviceId.hashCode() : 0;
		result = 31 * result + (fromDate != null ? fromDate.hashCode() : 0);
		result = 31 * result + (toDate != null ? toDate.hashCode() : 0);
		result = 31 * result + (searchText != null ? searchText.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "TransactionHistoryRepositoryArguments{" +
				"serviceId=" + serviceId +
				", fromDate=" + fromDate +
				", toDate=" + toDate +
				", searchText='" + searchText + '\'' +
				"} " + super.toString();
	}
}