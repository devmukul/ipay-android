package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GetDistrictAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GetThanaAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;

public class CommonDataLoader {

    public static void loadAll(Context context) {
        loadThanas(context);
        loadDistricts(context);
    }

    public static void loadThanas(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(Constants.THANA)) {
            String thanaJson = sharedPreferences.getString(Constants.THANA, null);
            Gson gson = new Gson();
            CommonData.setThanas(gson.fromJson(thanaJson, GetThanaResponse.class).getThanas());
        }
        else {
            GetThanaAsyncTask getThanaAsyncTask = new GetThanaAsyncTask(context);
            getThanaAsyncTask.execute();
        }
    }

    public static void loadDistricts(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(Constants.DISTRICT)) {
            String districtJson = sharedPreferences.getString(Constants.DISTRICT, null);
            Gson gson = new Gson();
            CommonData.setDistricts(gson.fromJson(districtJson, GetDistrictResponse.class).getDistricts());
        }
        else {
            GetDistrictAsyncTask getDistrictAsyncTask = new GetDistrictAsyncTask(context);
            getDistrictAsyncTask.execute();
        }
    }
}
