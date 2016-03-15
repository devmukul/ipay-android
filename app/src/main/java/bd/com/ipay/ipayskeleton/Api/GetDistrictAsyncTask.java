package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Asynchronously loads all supported thanas supported by our systems.
 * Loaded bank accounts are saved into {@link CommonData}.
 */
public class GetDistrictAsyncTask extends HttpRequestGetAsyncTask {
    public GetDistrictAsyncTask(final Context context) {
        super(Constants.COMMAND_GET_DISTRICT_LIST,
                new DistrictRequestBuilder().getGeneratedUri(),
                context);

        this.mHttpResponseListener = new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(String result) {
                String[] resultArr = result.split(";");
                try {
                    Gson gson = new Gson();
                    GetDistrictResponse getDistrictResponse = gson.fromJson(resultArr[2],
                            GetDistrictResponse.class);
                    List<District> districts = getDistrictResponse.getDistricts();
                    StringBuilder ids = new StringBuilder();
                    StringBuilder names = new StringBuilder();
                    for (District district : districts) {
                        ids.append(district.getId() + ", ");
                        names.append("\"" + district.getName() + "\", ");
                    }

                    Log.i("District Ids", ids.toString());
                    Log.i("District Names", names.toString());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
