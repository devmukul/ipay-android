package bd.com.ipay.android.datasource.transaction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.concurrent.Executor;

import bd.com.ipay.android.datasource.NetworkDataSource;
import bd.com.ipay.android.utility.NetworkState;
import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseParser;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GenericResponseWithMessageOnly;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryPendingRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.SSLPinning;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransactionHistoryDataSource extends NetworkDataSource<Integer, TransactionHistory> {

	@Nullable
	private final Integer serviceId;
	@Nullable
	private final Calendar fromDate;
	@Nullable
	private final Calendar toDate;
	@Nullable
	private final String searchText;
	@NonNull
	private final TransactionHistoryType transactionHistoryType;

	public TransactionHistoryDataSource(@NonNull Executor retryExecutor,
	                                    @NonNull TransactionHistoryType transactionHistoryType,
	                                    @Nullable Integer serviceId,
	                                    @Nullable Calendar fromDate,
	                                    @Nullable Calendar toDate,
	                                    @Nullable String searchText) {
		super(retryExecutor);
		this.transactionHistoryType = transactionHistoryType;
		this.serviceId = serviceId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.searchText = searchText;
	}

	@Override
	public void loadInitial(@NonNull final LoadInitialParams<Integer> params,
	                        @NonNull final LoadInitialCallback<Integer,
			                        TransactionHistory> callback) {
		getNetworkState().postValue(NetworkState.REFRESHING);
		getInitialLoad().postValue(NetworkState.REFRESHING);
		final Context context = MyApplication.getMyApplicationInstance();
		try {
			String responseFromSSL = SSLPinning.validatePinning();
			if (responseFromSSL.equals(context.getString(R.string.OK))) {
				final Request request = getRequest(1, params.requestedLoadSize);
				final OkHttpClient okHttpClient = MyApplication
						.getMyApplicationInstance().getOkHttpClient();
				final GenericHttpResponse response
						= parseHttpResponse(okHttpClient.newCall(request).execute());
				if (response.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
					TransactionHistoryResponse transactionHistoryResponse =
							new Gson().fromJson(response.getJsonString(),
									TransactionHistoryResponse.class);

					getNetworkState().postValue(NetworkState.LOADED);
					getInitialLoad().postValue(NetworkState.LOADED);
					callback.onResult(transactionHistoryResponse.getTransactions(),
							0, transactionHistoryResponse.isHasNext() ? 2 : null);
				} else {
					GenericResponseWithMessageOnly responseWithMessageOnly =
							new Gson().fromJson(response.getJsonString(),
									GenericResponseWithMessageOnly.class);
					throw new Exception(responseWithMessageOnly.getMessage());
				}
			}
		} catch (Exception ex) {
			this.onRetryListener = new OnRetryListener() {
				@Override
				public void onRetry() {
					loadInitial(params, callback);
				}
			};
			if (ex instanceof SocketException) {
				ex = new Exception(context.getString(R.string.network_unreachable));
			} else if (ex instanceof SocketTimeoutException) {
				ex = new Exception(context.getString(R.string.connection_time_out));
			}
			final NetworkState networkStateError = NetworkState.networkStateError(ex);
			getNetworkState().postValue(networkStateError);
			getInitialLoad().postValue(networkStateError);
		}
	}

	@Override
	public void loadBefore(@NonNull final LoadParams<Integer> params,
	                       @NonNull final LoadCallback<Integer, TransactionHistory> callback) {

	}

	@Override
	public void loadAfter(@NonNull final LoadParams<Integer> params,
	                      @NonNull final LoadCallback<Integer, TransactionHistory> callback) {
		Request request = getRequest(params.key, params.requestedLoadSize);
		final OkHttpClient okHttpClient
				= MyApplication.getMyApplicationInstance().getOkHttpClient();
		getNetworkState().postValue(NetworkState.LOADING);
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				onRetryListener = new OnRetryListener() {
					@Override
					public void onRetry() {
						loadBefore(params, callback);
					}
				};
				final NetworkState networkStateError = NetworkState.networkStateError(e);
				getNetworkState().postValue(networkStateError);
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) {
				final GenericHttpResponse httpResponse = parseHttpResponse(response);
				try {
					if (httpResponse.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
						TransactionHistoryResponse transactionHistoryResponse =
								new Gson().fromJson(httpResponse.getJsonString(),
										TransactionHistoryResponse.class);

						getNetworkState().postValue(NetworkState.LOADED);
						callback.onResult(transactionHistoryResponse.getTransactions(),
								transactionHistoryResponse.isHasNext() ? params.key + 1 : null);
					} else {
						GenericResponseWithMessageOnly responseWithMessageOnly =
								new Gson().fromJson(httpResponse.getJsonString(),
										GenericResponseWithMessageOnly.class);
						throw new Exception(responseWithMessageOnly.getMessage());
					}
				} catch (Exception ex) {
					onRetryListener = new OnRetryListener() {
						@Override
						public void onRetry() {
							loadBefore(params, callback);
						}
					};
					final NetworkState networkStateError = NetworkState.networkStateError(ex);
					getNetworkState().postValue(networkStateError);
				}
			}
		});
	}

	private okhttp3.Request getRequest(@SuppressWarnings("SameParameterValue") int pageNumber,
	                                   int itemCount) {
		final String url;
		switch (transactionHistoryType) {
			case PENDING:
				url = TransactionHistoryPendingRequest.generateUri(serviceId,
						fromDate, toDate, pageNumber, itemCount);
				break;
			case COMPLETED:
			default:
				url = TransactionHistoryRequest.generateUri(serviceId,
						fromDate, toDate, pageNumber, itemCount, searchText);
				break;
		}
		okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().
				header(Constants.USER_AGENT, Constants.USER_AGENT_MOBILE_ANDROID)
				.header("Accept", "application/json")
				.header("Content-type", "application/json")
				.header(Constants.TOKEN, TokenManager.getToken())
				.get()
				.url(url);
		return requestBuilder.build();
	}

	private GenericHttpResponse parseHttpResponse(Response response) {
		GenericHttpResponse mGenericHttpResponse;

		if (response == null)
			return null;

		HttpResponseParser mHttpResponseParser = new HttpResponseParser();
		mHttpResponseParser.setAPI_COMMAND(Constants.COMMAND_GET_TRANSACTION_HISTORY);
		mHttpResponseParser.setHttpResponse(response);
		mGenericHttpResponse = mHttpResponseParser.parseHttpResponse();
		return mGenericHttpResponse;
	}
}
