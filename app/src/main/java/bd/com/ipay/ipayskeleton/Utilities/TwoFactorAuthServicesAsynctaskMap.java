package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.SendNewPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFactorAuthServicesListWithOTPRequest;


public class TwoFactorAuthServicesAsynctaskMap {
    private static HttpRequestPostAsyncTask mHttpPostAsyncTask;
    private static HttpRequestPutAsyncTask mHttpPutAsyncTask;

    public static HttpRequestPutAsyncTask getPutAsyncTask(String command, String json,
                                                          String otp, Context context, String uri) {
        Gson gson = new Gson();
        switch (command) {
            case Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS:
                TwoFactorAuthServicesListWithOTPRequest twoFactorAuthServicesListWithOTPRequest = gson.fromJson(json,
                        TwoFactorAuthServicesListWithOTPRequest.class);
                if (otp != null)
                    twoFactorAuthServicesListWithOTPRequest.setOtp(otp);
                json = gson.toJson(twoFactorAuthServicesListWithOTPRequest);
                mHttpPutAsyncTask = new HttpRequestPutAsyncTask(command, uri, json, context, false);
                return mHttpPutAsyncTask;

            case Constants.COMMAND_SET_PIN:
                SetPinRequest mSetPinRequest = gson.fromJson(json, SetPinRequest.class);
                if (otp != null)
                    mSetPinRequest.setOtp(otp);
                json = gson.toJson(mSetPinRequest);
                mHttpPutAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_SET_PIN, uri, json, context, false);
                return mHttpPutAsyncTask;
            default:
                return null;
        }
    }

    public static HttpRequestPostAsyncTask getPostAsyncTask(String command, String json,
                                                            String otp, Context context, String uri) {
        Gson gson = new Gson();
        switch (command) {

            case Constants.COMMAND_SEND_MONEY:
                SendMoneyRequest sendMoneyRequest = gson.fromJson(json, SendMoneyRequest.class);
                if (otp != null)
                    sendMoneyRequest.setOtp(otp);
                json = gson.toJson(sendMoneyRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY, uri, json, context, false);
                return mHttpPostAsyncTask;

            case Constants.COMMAND_TOPUP_REQUEST:
                TopupRequest topupRequest = gson.fromJson(json, TopupRequest.class);
                if (otp != null)
                    topupRequest.setOtp(otp);
                json = gson.toJson(topupRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST, uri, json, context, false);
                return mHttpPostAsyncTask;

            case Constants.COMMAND_ADD_MONEY:
                AddMoneyRequest addMoneyRequest = gson.fromJson(json, AddMoneyRequest.class);
                if (otp != null)
                    addMoneyRequest.setOtp(otp);
                json = gson.toJson(addMoneyRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_MONEY, uri, json, context, false);
                return mHttpPostAsyncTask;

            case Constants.COMMAND_WITHDRAW_MONEY:
                WithdrawMoneyRequest withdrawMoneyRequest = gson.fromJson(json, WithdrawMoneyRequest.class);
                if (otp != null)
                    withdrawMoneyRequest.setOtp(otp);
                json = gson.toJson(withdrawMoneyRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_WITHDRAW_MONEY, uri, json, context, false);
                return mHttpPostAsyncTask;

            case Constants.COMMAND_PAYMENT:
                PaymentRequest paymentRequest = gson.fromJson(json, PaymentRequest.class);
                if (otp != null)
                    paymentRequest.setOtp(otp);
                json = gson.toJson(paymentRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT, uri, json, context, false);
                return mHttpPostAsyncTask;

            case Constants.COMMAND_SEND_PAYMENT_REQUEST:
                SendNewPaymentRequest mSendNewPaymentRequest = gson.fromJson(json, SendNewPaymentRequest.class);
                if (otp != null)
                    mSendNewPaymentRequest.setOtp(otp);
                json = gson.toJson(mSendNewPaymentRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_PAYMENT_REQUEST, uri, json, context, false);
                return mHttpPostAsyncTask;
            case Constants.COMMAND_ACCEPT_REQUESTS_MONEY:
                RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest
                        = gson.fromJson(json, RequestMoneyAcceptRejectOrCancelRequest.class);
                if (otp != null)
                    requestMoneyAcceptRejectOrCancelRequest.setOtp(otp);
                json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_REQUESTS_MONEY, uri, json, context, false);
                return mHttpPostAsyncTask;
            case Constants.COMMAND_ACCEPT_PAYMENT_REQUEST:
                RequestMoneyAcceptRejectOrCancelRequest mRequestMoneyAcceptRejectOrCancelRequest =
                        gson.fromJson(json, RequestMoneyAcceptRejectOrCancelRequest.class);
                if (otp != null)
                    mRequestMoneyAcceptRejectOrCancelRequest.setOtp(otp);
                json = gson.toJson(mRequestMoneyAcceptRejectOrCancelRequest);
                mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST, uri, json, context, false);
                return mHttpPostAsyncTask;

            default:
                return null;
        }
    }
}
