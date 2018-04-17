package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.CardPaymentWebViewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyByCreditOrDebitCardRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyByCreditOrDebitCardResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyFromCreditOrDebitCardReviewFragment extends BaseFragment implements HttpResponseListener {

    private static final int CARD_PAYMENT_WEB_VIEW_REQUEST = 2001;
    private HttpRequestPostAsyncTask mAddMoneyTask = null;

    private ProgressDialog mProgressDialog;

    private double mAmount;
    private String mDescription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);

        mProgressDialog = new ProgressDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_money_by_credit_or_debit_card_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final TextView amountTextView = findViewById(R.id.amount_text_view);
        final LinearLayout descriptionViewHolder = findViewById(R.id.description_view_holder);
        final TextView descriptionTextView = findViewById(R.id.description_text_view);
        final Button addMoneyButton = findViewById(R.id.add_money_button);

        amountTextView.setText(Utilities.formatTaka(mAmount));

        if (mDescription == null || mDescription.isEmpty()) {
            descriptionViewHolder.setVisibility(View.GONE);
        } else {
            descriptionViewHolder.setVisibility(View.VISIBLE);
            descriptionTextView.setText(mDescription);
        }


        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddMoney();
            }
        });
    }

    private void attemptAddMoney() {
        attemptAddMoney(null); // From business rule now pin wont be checked for add money by card
    }

    private void attemptAddMoney(String pin) {
        if (mAddMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        AddMoneyByCreditOrDebitCardRequest mAddMoneyRequest = new AddMoneyByCreditOrDebitCardRequest(mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mAddMoneyRequest);

        mAddMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_MONEY,
                Constants.BASE_URL_CARD + Constants.URL_ADD_MONEY_CREDIT_OR_DEBIT_CARD, json, getActivity(), false);
        mAddMoneyTask.mHttpResponseListener = this;

        mAddMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog(final String errorMessage) {
        new AlertDialog.Builder(getContext())
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity().setResult(resultCode, data);
        getActivity().finish();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (isAdded()) mProgressDialog.dismiss();
        mAddMoneyTask = null;

        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            return;
        }

        switch (result.getApiCommand()) {
            case Constants.COMMAND_ADD_MONEY:
                Gson gson = new GsonBuilder().create();
                AddMoneyByCreditOrDebitCardResponse mAddMoneyByCreditOrDebitResponse = gson.fromJson(result.getJsonString(), AddMoneyByCreditOrDebitCardResponse.class);
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        Intent intent = new Intent(getActivity(), CardPaymentWebViewActivity.class);
                        intent.putExtra(Constants.CARD_PAYMENT_URL, mAddMoneyByCreditOrDebitResponse.getForwardUrl());
                        startActivityForResult(intent, CARD_PAYMENT_WEB_VIEW_REQUEST);
                        break;
                    case Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST:
                    case Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE:
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), mAddMoneyByCreditOrDebitResponse.getMessage(), Toast.LENGTH_SHORT);
                        break;
                    default:
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                        break;
                }
                break;
            default:
                break;
        }
    }
}
