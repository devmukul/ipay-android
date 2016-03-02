package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.Utilities.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Asynchronously loads all supported thanas supported by our systems.
 * Loaded bank accounts are saved into {@link CommonData}.
 */
public class GetThanaAsyncTask extends HttpRequestGetAsyncTask {
    public GetThanaAsyncTask(final Context context) {
        super(Constants.COMMAND_GET_THANA_LIST,
                new ThanaRequestBuilder().getGeneratedUri(),
                context);

        this.mHttpResponseListener = new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(String result) {
                String[] resultArr = result.split(";");
                try {
                    Gson gson = new Gson();
                    GetThanaResponse getThanaResponse = gson.fromJson(resultArr[2],
                            GetThanaResponse.class);

                    List<Thana> thanas = getThanaResponse.getThanas();
                    CommonData.setThanas(thanas);

                    Log.i("Thanas", thanas.toString());

                    SharedPreferences.Editor sharedPreferenceEditor
                            = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    sharedPreferenceEditor.putString(Constants.THANA, resultArr[2]);
                    sharedPreferenceEditor.apply();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
