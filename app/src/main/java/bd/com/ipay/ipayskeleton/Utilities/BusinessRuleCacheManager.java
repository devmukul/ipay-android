package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.List;

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
		if (serviceId == ServiceIdConstants.UTILITY_BILL_PAYMENT ||
				serviceId == ServiceIdConstants.MAKE_PAYMENT || serviceId == ServiceIdConstants.ADD_MONEY_BY_BANK_INSTANTLY) {
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
						switch (apiCommand) {
							case Constants.COMMAND_GET_BUSINESS_RULE_V2:
								updateBusinessRule(mMandatoryBusinessRules, new Gson().fromJson(result.getJsonString(), BusinessRuleV2.class).getRules());
								break;
							case Constants.COMMAND_GET_BUSINESS_RULE:
								final BusinessRule[] businessRuleArray = new Gson().fromJson(result.getJsonString(), BusinessRule[].class);
								if (businessRuleArray != null) {
									for (BusinessRule rule : businessRuleArray) {
										switch (serviceId) {
											case ServiceIdConstants.SEND_MONEY:
												updateSendMoneyBusinessRules(mMandatoryBusinessRules, rule);
												break;
											case ServiceIdConstants.REQUEST_MONEY:
												updateRequestMoneyBusinessRule(mMandatoryBusinessRules, rule);
												break;
                                            case ServiceIdConstants.MAKE_PAYMENT:
                                                updateMakePaymentBusinessRules(mMandatoryBusinessRules, rule);
                                                break;
											case ServiceIdConstants.ADD_MONEY_BY_BANK:
												updateAddMoneyByBankBusinessRule(mMandatoryBusinessRules, rule);
												break;
											case ServiceIdConstants.TOP_UP:
												updateTopupBusinessRule(mMandatoryBusinessRules, rule);
											case ServiceIdConstants.WITHDRAW_MONEY:
												updateWithdrawMoneyBusinessRule(mMandatoryBusinessRules, rule);
												break;
											case ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD:
												updateAddMoneyByCardBusinessRule(mMandatoryBusinessRules, rule);
												break;
										}

									}
								}
								break;
							default:
								return;
						}

						BusinessRuleCacheManager.setBusinessRules(getTag(serviceId), mMandatoryBusinessRules);
						Intent intent = new Intent();
						intent.setAction(Constants.BUSINESS_RULE_UPDATE_BROADCAST);
						intent.putExtra(SERVICE_ID_KEY, serviceId);
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

	private static void updateRequestMoneyBusinessRule(MandatoryBusinessRules mMandatoryBusinessRules, BusinessRule rule) {
		switch (rule.getRuleID()) {
			case BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT:
				mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT:
				mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_VERIFICATION_REQUIRED:
				mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_PIN_REQUIRED:
				mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				break;
		}
	}

	private static void updateTopupBusinessRule(MandatoryBusinessRules mMandatoryBusinessRules, BusinessRule rule) {
		switch (rule.getRuleID()) {
			case BusinessRuleConstants.SERVICE_RULE_TOP_UP_MAX_AMOUNT_PER_PAYMENT:
				mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_TOP_UP_MIN_AMOUNT_PER_PAYMENT:
				mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_TOP_UP_VERIFICATION_REQUIRED:
				mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_TOP_UP_PIN_REQUIRED:
				mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				break;
		}
	}

	private static void updateSendMoneyBusinessRules(MandatoryBusinessRules mandatoryBusinessRules, BusinessRule rule) {
		switch (rule.getRuleID()) {
			case BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT:
				mandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT:
				mandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_VERIFICATION_REQUIRED:
				mandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_PIN_REQUIRED:
				mandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				break;
		}
	}

    private static void updateMakePaymentBusinessRules(MandatoryBusinessRules mandatoryBusinessRules, BusinessRule rule) {
        switch (rule.getRuleID()) {
            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT_V3:
                mandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                break;
            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT_V3:
                mandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                break;
            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_VERIFICATION_REQUIRED_V3:
                mandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                break;
            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_PIN_REQUIRED_V3:
                mandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                break;
            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_LOCATION_REQUIRED_V3:
                mandatoryBusinessRules.setLOCATION_REQUIRED(rule.getRuleValue());
                break;
        }
    }

	private static void updateAddMoneyByBankBusinessRule(final MandatoryBusinessRules mandatoryBusinessRules, final BusinessRule rule) {
		switch (rule.getRuleID()) {
			case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_MAX_AMOUNT_PER_PAYMENT:
				mandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_MIN_AMOUNT_PER_PAYMENT:
				mandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_VERIFICATION_REQUIRED:
				mandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_PIN_REQUIRED:
				mandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				break;
		}
	}

	private static void updateWithdrawMoneyBusinessRule(final MandatoryBusinessRules mandatoryBusinessRules, final BusinessRule rule) {
		switch (rule.getRuleID()) {
			case BusinessRuleConstants.SERVICE_RULE_WITHDRAW_MONEY_MAX_AMOUNT_PER_PAYMENT:
				mandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_WITHDRAW_MONEY_MIN_AMOUNT_PER_PAYMENT:
				mandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_WITHDRAW_MONEY_VERIFICATION_REQUIRED:
				mandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_WITHDRAW_MONEY_PIN_REQUIRED:
				mandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				break;
		}
	}

	private static void updateAddMoneyByCardBusinessRule(final MandatoryBusinessRules mandatoryBusinessRules, final BusinessRule rule) {
		switch (rule.getRuleID()) {
			case BusinessRuleConstants.SERVICE_RULE_ADD_CARDMONEY_MAX_AMOUNT_SINGLE:
				mandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_ADD_CARDMONEY_MIN_AMOUNT_SINGLE:
				mandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_ADD_CARDMONEY_VERIFICATION_REQUIRED:
				mandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
				break;
			case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_PIN_REQUIRED:
				mandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
				break;
		}
	}

	public static String getTag(final int serviceId) {
		switch (serviceId) {
			case ServiceIdConstants.SEND_MONEY:
				return Constants.SEND_MONEY;
			case ServiceIdConstants.MAKE_PAYMENT:
                return Constants.MAKE_PAYMENT;
            case ServiceIdConstants.ADD_MONEY_BY_BANK:
				return Constants.ADD_MONEY_BY_BANK;
			case ServiceIdConstants.WITHDRAW_MONEY:
				return Constants.WITHDRAW_MONEY;
			case ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD:
				return Constants.ADD_MONEY_BY_CARD;
			case ServiceIdConstants.REQUEST_MONEY:
				return Constants.REQUEST_MONEY;
			case ServiceIdConstants.TOP_UP:
				return Constants.TOP_UP;
			case ServiceIdConstants.UTILITY_BILL_PAYMENT:
				return Constants.UTILITY_BILL_PAYMENT;
			case ServiceIdConstants.RAILWAY_TICKET:
				return Constants.RAILWAY_TICKET;
			default:
				return "";
		}
	}
}
