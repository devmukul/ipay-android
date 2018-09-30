package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;


public class BusinessRuleCacheManager {
    private static SharedPreferences pref;
    public static final String SERVICE_ID_KEY = "SERVICE_ID";

    public static void initialize(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static boolean ifContainsBusinessRule(String tag) {
        return (pref.contains(tag));
    }

    public static void setBusinessRules(String tag, MandatoryBusinessRules mandatoryBusinessRules) {
        if (tag.isEmpty())
            return;
        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mandatoryBusinessRules);
        prefsEditor.putString(tag, json);
        prefsEditor.apply();
    }

    public static MandatoryBusinessRules getBusinessRules(String tag) {
        if (tag.isEmpty())
            return null;
        Gson gson = new Gson();
        String json = pref.getString(tag, "");
        return gson.fromJson(json, MandatoryBusinessRules.class);
    }

    public static void fetchBusinessRule(final Context context, final int serviceId) {
        String mUri = new GetBusinessRuleRequestBuilder(serviceId).getGeneratedUri();
        final HttpRequestGetAsyncTask mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, context, new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(GenericHttpResponse result) {
                if (!HttpErrorHandler.isErrorFound(result, context, null)) {
                    try {

                        BusinessRule[] businessRuleArray = new Gson().fromJson(result.getJsonString(), BusinessRule[].class);
                        final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules(getTag(serviceId));
                        if (businessRuleArray != null) {
                            for (BusinessRule rule : businessRuleArray) {

                                //Send money

                                if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                    mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                    mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_VERIFICATION_REQUIRED)) {
                                    mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_PIN_REQUIRED)) {
                                    mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());

                                    //Request Money

                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                    mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                    mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_VERIFICATION_REQUIRED)) {
                                    mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_PIN_REQUIRED)) {
                                    mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());

                                    //Top Up

                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_MAX_AMOUNT_PER_PAYMENT)) {
                                    mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_MIN_AMOUNT_PER_PAYMENT)) {
                                    mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_TOP_UP_VERIFICATION_REQUIRED)) {
                                    mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_PIN_REQUIRED)) {
                                    mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                                }


                            }
                            BusinessRuleCacheManager.setBusinessRules(getTag(serviceId), mMandatoryBusinessRules);
                            Intent intent = new Intent();
                            intent.setAction(Constants.BUSINESS_RULE_UPDATE_BROADCAST);
                            intent.putExtra(SERVICE_ID_KEY, getTag(serviceId));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DialogUtils.showDialogForBusinessRuleNotAvailable(context);
                    }
                }
            }
        }, true);
        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static String getTag(final int serviceId) {
        switch (serviceId) {
            case ServiceIdConstants.SEND_MONEY:
                return Constants.SEND_MONEY;
            case ServiceIdConstants.REQUEST_MONEY:
                return Constants.REQUEST_MONEY;
            case ServiceIdConstants.TOP_UP:
                return Constants.TOP_UP;
            default:
                return "";
        }
    }
}
