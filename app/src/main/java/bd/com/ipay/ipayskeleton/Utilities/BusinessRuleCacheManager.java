package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;


public class BusinessRuleCacheManager {
    private static SharedPreferences pref;
    private static Context context;

    public static void initialize(Context context) {
        context = context;
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static void setDefaultBusinessRules(String tag, MandatoryBusinessRules mandatoryBusinessRules) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mandatoryBusinessRules);
        prefsEditor.putString(tag, json);
        prefsEditor.commit();
    }

    public static void setBusinessRules(String tag, MandatoryBusinessRules mandatoryBusinessRules) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mandatoryBusinessRules);
        prefsEditor.putString(tag, json);
        prefsEditor.commit();
    }

    public static MandatoryBusinessRules getBusinessRules(String tag) {
        Gson gson = new Gson();
        String json = pref.getString(tag, "");
        MandatoryBusinessRules mandatoryBusinessRules = gson.fromJson(json, MandatoryBusinessRules.class);
        return mandatoryBusinessRules;
    }

}
