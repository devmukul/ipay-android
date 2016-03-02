package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BankRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetAvailableBankResponse;
import bd.com.ipay.ipayskeleton.Utilities.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Asynchronously loads all supported bank accounts supported by our systems.
 * Loaded bank accounts are saved into {@link CommonData}.
 *
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
                public void httpResponseReceiver(String result) {
                String[] resultArr = result.split(";");
                try {
                    Gson gson = new Gson();
                    GetAvailableBankResponse getAvailableBankResponse = gson.fromJson(resultArr[2],
                            GetAvailableBankResponse.class);

                    List<Bank> availableBanks = getAvailableBankResponse.getAvailableBanks();
                    CommonData.setAvailableBanks(availableBanks);

                    if (listener != null) {
                        listener.onLoadSuccess(availableBanks);
                    }
                } catch(Exception e) {
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
        public void onLoadSuccess(List<Bank> banks);
        public void onLoadFailed();
    }
}
