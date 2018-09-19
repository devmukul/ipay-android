package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;


public class BusinessRuleCacheManager {
    private static SharedPreferences pref;

    public static void initialize(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static boolean ifContainsBusinessRule(String tag) {
        return (pref.contains(tag));
    }

    public static void setBusinessRules(String tag, MandatoryBusinessRules mandatoryBusinessRules) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mandatoryBusinessRules);
        prefsEditor.putString(tag, json);
        prefsEditor.apply();
    }

    public static MandatoryBusinessRules getBusinessRules(String tag) {
        Gson gson = new Gson();
        String json = pref.getString(tag, "");
        return gson.fromJson(json, MandatoryBusinessRules.class);
    }
}
