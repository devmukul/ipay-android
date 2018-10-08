package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRuleV2;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.Rule;


public class BusinessRuleCacheManager {
	private static SharedPreferences pref;
	public static final String SERVICE_ID_KEY = "SERVICE_ID";

	static void initialize(Context context) {
		pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
	}

	static boolean ifContainsBusinessRule(String tag) {
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
		final String mUri = new GetBusinessRuleRequestBuilder(serviceId).getGeneratedUri();
		final String apiCommand;
		if (serviceId == ServiceIdConstants.UTILITY_BILL_PAYMENT) {
			apiCommand = Constants.COMMAND_GET_BUSINESS_RULE_V2;
		} else {
			apiCommand = Constants.COMMAND_GET_BUSINESS_RULE;
		}
		final HttpRequestGetAsyncTask mGetBusinessRuleTask = new HttpRequestGetAsyncTask(apiCommand,
				mUri, context, new HttpResponseListener() {
			@Override
			public void httpResponseReceiver(GenericHttpResponse result) {
				if (!HttpErrorHandler.isErrorFound(result, context, null)) {
					try {

						final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules(getTag(serviceId));
						if (apiCommand.equals(Constants.COMMAND_GET_BUSINESS_RULE_V2)) {
							updateBusinessRule(mMandatoryBusinessRules, new Gson().fromJson(result.getJsonString(), BusinessRuleV2.class).getRules());
						} else {
							final BusinessRule[] businessRuleArray = new Gson().fromJson(result.getJsonString(), BusinessRule[].class);
							updateBusinessRule(mMandatoryBusinessRules, businessRuleArray);
						}
						BusinessRuleCacheManager.setBusinessRules(getTag(serviceId), mMandatoryBusinessRules);
						Intent intent = new Intent();
						intent.setAction(Constants.BUSINESS_RULE_UPDATE_BROADCAST);
						intent.putExtra(SERVICE_ID_KEY, getTag(serviceId));
						LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
					} catch (Exception e) {
						e.printStackTrace();
						DialogUtils.showDialogForBusinessRuleNotAvailable(context);
					}
				}
			}
		}, true);
		mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private static void updateBusinessRule(MandatoryBusinessRules mMandatoryBusinessRules, BusinessRule[] businessRuleArray) {
		if (businessRuleArray != null) {
			for (BusinessRule rule : businessRuleArray) {
				if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
					mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
					mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				} else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_VERIFICATION_REQUIRED)) {
					mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_PIN_REQUIRED)) {
					mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
					mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
					mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				} else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_VERIFICATION_REQUIRED)) {
					mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_PIN_REQUIRED)) {
					mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_MAX_AMOUNT_PER_PAYMENT)) {
					UtilityBillPaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_MIN_AMOUNT_PER_PAYMENT)) {
					UtilityBillPaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_VERIFICATION_REQUIRED)) {
					UtilityBillPaymentActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				} else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_PIN_REQUIRED)) {
					UtilityBillPaymentActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				}
			}
		}
	}

	private static void updateBusinessRule(MandatoryBusinessRules mMandatoryBusinessRules, List<Rule> businessRuleList) {
		if (businessRuleList != null) {
			for (Rule rule : businessRuleList) {
				switch (rule.getRuleName()) {
					case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_MAX_AMOUNT_PER_PAYMENT:
						mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
						break;
					case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_MIN_AMOUNT_PER_PAYMENT:
						mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
						break;
					case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_VERIFICATION_REQUIRED:
						mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
						break;
					case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_PIN_REQUIRED:
						mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
						break;
				}
			}
		}
	}


	public static String getTag(final int serviceId) {
		switch (serviceId) {
			case ServiceIdConstants.SEND_MONEY:
				return Constants.SEND_MONEY;
			case ServiceIdConstants.REQUEST_MONEY:
				return Constants.REQUEST_MONEY;
			case ServiceIdConstants.UTILITY_BILL_PAYMENT:
				return Constants.UTILITY_BILL_PAYMENT;
			default:
				return "";
		}
	}
}
