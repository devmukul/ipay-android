package bd.com.ipay.android.viewmodel;

public class RepositoryArguments {
	public static final int DEFAULT_PAGE_SIZE = 10;
	private int pageSize;

	public RepositoryArguments(int pageSize) {
		this.pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
	}
}