package bd.com.ipay.ipayskeleton.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.BankAccountList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class IPayChooseBankOptionViewModel extends AndroidViewModel implements HttpResponseListener {

	public BankAccountList getBankAccount(int position) {
		if (userBankAccountListLiveData.getValue() != null)
			return userBankAccountListLiveData.getValue().get(position);
		return null;
	}

	public enum BankAccountStatus {
		NOT_ADDED, NOT_VERIFIED, VERIFIED
	}

	private final MutableLiveData<List<BankAccountList>> userBankAccountListLiveData = new MutableLiveData<>();
	private final MutableLiveData<BankAccountStatus> userBankAccountStatus = new MutableLiveData<>();
	private HttpRequestGetAsyncTask mGetBankTask;
	private final Gson gson = new GsonBuilder().create();

	public IPayChooseBankOptionViewModel(@NonNull Application application) {
		super(application);
	}

	public MutableLiveData<List<BankAccountList>> getUserBankAccountListLiveData() {
		return userBankAccountListLiveData;
	}

	public MutableLiveData<BankAccountStatus> getUserBankAccountStatus() {
		return userBankAccountStatus;
	}

	public void fetchUserBankList() {
		if (CommonData.isAvailableBankListLoaded()) {
			getUserBankList();
		} else {
			GetAvailableBankAsyncTask mGetAvailableBankAsyncTask = new GetAvailableBankAsyncTask(getApplication(),
					new GetAvailableBankAsyncTask.BankLoadListener() {
						@Override
						public void onLoadSuccess() {
							getUserBankList();
						}

						@Override
						public void onLoadFailed() {
							Toaster.makeText(getApplication(), R.string.failed_available_bank_list_loading, Toast.LENGTH_LONG);
						}
					});
			mGetAvailableBankAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private void getUserBankList() {
		if (mGetBankTask != null) {
			return;
		}

		mGetBankTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_LIST,
				Constants.BASE_URL_MM + Constants.URL_GET_BANK, getApplication(), false);
		mGetBankTask.mHttpResponseListener = this;

		mGetBankTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private boolean isBankAdded(final List<BankAccountList> bankAccountLists) {
		return bankAccountLists != null && !bankAccountLists.isEmpty();
	}

	@Override
	public void httpResponseReceiver(GenericHttpResponse result) {
		if (HttpErrorHandler.isErrorFound(result, getApplication(), null)) {
			mGetBankTask = null;
		} else {
			switch (result.getApiCommand()) {
				case Constants.COMMAND_GET_BANK_LIST:
					final GetBankListResponse mBankListResponse = gson.fromJson(result.getJsonString(), GetBankListResponse.class);

					switch (result.getStatus()) {
						case Constants.HTTP_RESPONSE_STATUS_OK:
							if (!isBankAdded(mBankListResponse.getBankAccountList())) {
								userBankAccountStatus.postValue(BankAccountStatus.NOT_ADDED);
							} else if (!isVerifiedBankAdded(mBankListResponse.getBankAccountList())) {
								userBankAccountStatus.postValue(BankAccountStatus.NOT_VERIFIED);
							} else {
								userBankAccountStatus.postValue(BankAccountStatus.VERIFIED);
								userBankAccountListLiveData.postValue(mBankListResponse.getBankAccountList());
							}
							break;
						default:
							Toaster.makeText(getApplication(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
							break;
					}
					break;
			}
		}
	}

	private boolean isVerifiedBankAdded(List<BankAccountList> bankAccountList) {
		boolean result = bankAccountList != null;
		if (result) {
			result = false;
			for (BankAccountList bank : bankAccountList) {
				result |= bank.getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED);
			}
		}
		return result;
	}
}
