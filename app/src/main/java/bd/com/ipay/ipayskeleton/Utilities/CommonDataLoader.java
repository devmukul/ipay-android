package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Api.GetDistrictAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GetThanaAsyncTask;

public class CommonDataLoader {

    public static void loadAll(Context context) {
        loadThanas(context);
        loadDistricts(context);
    }

    public static void loadThanas(Context context) {
        GetThanaAsyncTask getThanaAsyncTask = new GetThanaAsyncTask(context);
        getThanaAsyncTask.execute();
    }

    public static void loadDistricts(Context context) {
        GetDistrictAsyncTask getDistrictAsyncTask = new GetDistrictAsyncTask(context);
        getDistrictAsyncTask.execute();
    }
}
