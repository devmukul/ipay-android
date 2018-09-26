package bd.com.ipay.ipayskeleton.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.PromotionApi.PromotionClaimRequest;
import bd.com.ipay.ipayskeleton.Api.PromotionApi.PromotionResponse;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.Promotion.GiftFromIPayMetaData;
import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PromotionsViewModel extends AndroidViewModel implements HttpResponseListener {

	private HttpRequestGetAsyncTask httpRequestGetPromotionListAsyncTask;
	private HttpRequestPostAsyncTask httpRequestClaimPromotionPostAsyncTask;
	public final MutableLiveData<List<Promotion>> mPromotionListMutableLiveData = new MutableLiveData<>();
	public final MutableLiveData<Boolean> offerClaimLiveData = new MutableLiveData<>();
	private final Gson gson = new Gson();
	private Promotion claimedPromotion;
	public ProgressDialogListener progressDialogListener;

	public PromotionsViewModel(@NonNull Application application) {
		super(application);
	}

	public void fetchPromotionsData() {
		if (!Utilities.isConnectionAvailable(getApplication())) {
			Toaster.makeText(getApplication(), R.string.no_internet_connection, Toast.LENGTH_LONG);
			mPromotionListMutableLiveData.postValue(null);
		} else if (httpRequestGetPromotionListAsyncTask == null) {
			httpRequestGetPromotionListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROMOTIONS_LIST, Constants.BASE_URL_OFFER + Constants.URL_PROMOTIONS, getApplication(), this, true);
			httpRequestGetPromotionListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void httpResponseReceiver(GenericHttpResponse result) {

		switch (result.getApiCommand()) {
			case Constants.COMMAND_GET_PROMOTIONS_LIST:
				if (HttpErrorHandler.isErrorFound(result, getApplication())) {
					httpRequestGetPromotionListAsyncTask = null;
					mPromotionListMutableLiveData.postValue(null);
				} else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
					httpRequestGetPromotionListAsyncTask = null;
					final PromotionResponse promotionResponse = gson.fromJson(result.getJsonString(), PromotionResponse.class);
					mPromotionListMutableLiveData.postValue(promotionResponse.getPromotionList());
				} else {
					mPromotionListMutableLiveData.postValue(null);
				}
				break;
			case Constants.COMMAND_PROMOTIONS_CLAIM:
				if (HttpErrorHandler.isErrorFoundForProgressDialogListener(result, getApplication(), progressDialogListener)) {
					httpRequestClaimPromotionPostAsyncTask = null;
					progressDialogListener.dismissDialog();
					offerClaimLiveData.postValue(false);
				} else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
					httpRequestClaimPromotionPostAsyncTask = null;
					progressDialogListener.dismissDialog();
					offerClaimLiveData.postValue(true);
				} else {
					progressDialogListener.dismissDialog();
					offerClaimLiveData.postValue(false);
				}
				break;
		}
	}

	public boolean isFetchingData() {
		return httpRequestGetPromotionListAsyncTask != null;
	}

	public void setClaimedPromotion(Promotion claimedPromotion) {
		this.claimedPromotion = claimedPromotion;
	}

	@Nullable
	public Promotion getPromotion(int position) {
		if (mPromotionListMutableLiveData.getValue() == null || mPromotionListMutableLiveData.getValue().isEmpty())
			return null;
		else
			return mPromotionListMutableLiveData.getValue().get(position);
	}

	private void claimPromotion(@Nullable Long outletId) {
		if (!Utilities.isConnectionAvailable(getApplication())) {
			Toaster.makeText(getApplication(), R.string.no_internet_connection, Toast.LENGTH_LONG);
		} else if (httpRequestClaimPromotionPostAsyncTask == null) {
			final GiftFromIPayMetaData giftFromIPayMetaData = claimedPromotion.getMedata(GiftFromIPayMetaData.class);
			final PromotionClaimRequest promotionClaimRequest = new PromotionClaimRequest(giftFromIPayMetaData.getBusinessAccountId(), outletId);
			final String jsonRequestBody = gson.toJson(promotionClaimRequest);
			httpRequestClaimPromotionPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PROMOTIONS_CLAIM, Constants.BASE_URL_OFFER + giftFromIPayMetaData.getRedeemPath(), jsonRequestBody, getApplication(), this, false);
			httpRequestClaimPromotionPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			progressDialogListener.setLoadingMessage(getApplication().getString(R.string.please_wait));
			progressDialogListener.showDialog();
		}
	}

	public void processClaimedPromotionViaQRScan(Barcode barcode) {
		final String result = barcode.displayValue;
		String[] stringArray = result.split("-");
		Long outletId = null;
		if (stringArray.length > 1) {
			try {
				outletId = Long.parseLong(stringArray[1].trim().replaceAll("[^0-9]", ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		claimPromotion(outletId);
	}
}
