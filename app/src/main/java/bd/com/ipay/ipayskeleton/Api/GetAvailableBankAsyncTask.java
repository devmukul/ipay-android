package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BankRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetAvailableBankResponse;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Asynchronously loads all supported banks supported by our systems.
 * Loaded bank accounts are saved into {@link CommonData}.
 * <p/>
 * If you want to do something with the result after loading the bank list, pass
 * a {@link bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask.BankLoadListener} to the constructor.
 */
public class GetAvailableBankAsyncTask extends HttpRequestGetAsyncTask {
    public GetAvailableBankAsyncTask(Context context, final BankLoadListener listener) {
        super(Constants.COMMAND_GET_AVAILABLE_BANK_LIST,
                new BankRequestBuilder().getGeneratedUri(),
                context);

        this.mHttpResponseListener = new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(HttpResponseObject result) {
                if (result == null)
                    return;

                try {
                    Gson gson = new Gson();
                    GetAvailableBankResponse getAvailableBankResponse = gson.fromJson(result.getJsonString(),
                            GetAvailableBankResponse.class);

                    List<Bank> availableBanks = getAvailableBankResponse.getAvailableBanks();
                    CommonData.setAvailableBanks(availableBanks);

                    if (listener != null) {
                        listener.onLoadSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onLoadFailed();
                    }
                }
            }
        };
    }

    public GetAvailableBankAsyncTask(Context context) {
        this(context, null);
    }

    public interface BankLoadListener {
        void onLoadSuccess();

        void onLoadFailed();
    }
}
