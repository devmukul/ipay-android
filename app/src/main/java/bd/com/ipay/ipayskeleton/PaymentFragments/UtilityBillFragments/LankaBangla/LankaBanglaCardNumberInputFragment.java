package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.LankaBanglaCustomerInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractCardNumberInputFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LankaBanglaCardNumberInputFragment extends IPayAbstractCardNumberInputFragment implements HttpResponseListener {

	private HttpRequestGetAsyncTask mGetLankaBanglaCardUserInfoAsyncTask = null;
	private final Gson gson = new GsonBuilder().create();
	private CustomProgressDialog mProgressDialog;

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getActivity() != null)
			getActivity().setTitle(R.string.lanka_bangla);

		setCardIconImageResource(R.drawable.ic_debit_credit_card_icon);
		setMessage(getString(R.string.lanka_bangla_card_number_input_message));
		setCardNumberHint(getString(R.string.lanka_bangla_card_number));

		mProgressDialog = new CustomProgressDialog(getActivity());
	}

	@Override
	protected boolean verifyInput() {
		if (TextUtils.isEmpty(getCardNumber())) {
			showErrorMessage(getString(R.string.empty_card_number_message));
			return false;
		} else if (!CardNumberValidator.validateCardNumber(getCardNumber())) {
			showErrorMessage(getString(R.string.invalid_card_number_message));
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void performContinueAction() {
		if (!Utilities.isConnectionAvailable(getContext())) {
			Toaster.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
		} else if (mGetLankaBanglaCardUserInfoAsyncTask != null) {
			return;
		}
		CardNumberValidator.Cards cards = CardNumberValidator.getCardType(getCardNumber());
		final String url;
		if (cards == null)
			return;
		switch (cards) {
			case VISA:
				url = Constants.BASE_URL_UTILITY + Constants.URL_GET_LANKA_BANGLA_VISA_CUSTOMER_INFO + CardNumberValidator.sanitizeEntry(getCardNumber(), true);
				break;
			case MASTERCARD:
				url = Constants.BASE_URL_UTILITY + Constants.URL_GET_LANKA_BANGLA_MASTERCARD_CUSTOMER_INFO + CardNumberValidator.sanitizeEntry(getCardNumber(), true);
				break;
			default:
				return;
		}
		mGetLankaBanglaCardUserInfoAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_LANKA_BANGLA_CUSTOMER_INFO, url, getContext(), this, false);
		mGetLankaBanglaCardUserInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		mProgressDialog.setTitle(R.string.please_wait_no_ellipsis);
		mProgressDialog.setMessage(getString(R.string.fetching_user_info));
		mProgressDialog.showDialog();
	}

	@Override
	public void httpResponseReceiver(GenericHttpResponse result) {
		if (getActivity() == null)
			return;

		if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
			mGetLankaBanglaCardUserInfoAsyncTask = null;
		} else {
			try {
				switch (result.getApiCommand()) {
					case Constants.COMMAND_GET_LANKA_BANGLA_CUSTOMER_INFO:
						mGetLankaBanglaCardUserInfoAsyncTask = null;
						LankaBanglaCustomerInfoResponse lankaBanglaCustomerInfoResponse = gson.fromJson(result.getJsonString(), LankaBanglaCustomerInfoResponse.class);
						switch (result.getStatus()) {
							case Constants.HTTP_RESPONSE_STATUS_OK:
								showLankaBanglaUserInfo(lankaBanglaCustomerInfoResponse);
								break;
							default:
								if (!TextUtils.isEmpty(lankaBanglaCustomerInfoResponse.getMessage())) {
									Toaster.makeText(getActivity(), lankaBanglaCustomerInfoResponse.getMessage(), Toast.LENGTH_SHORT);
								} else {
									Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
								}
								break;
						}
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
			}
		}
		mProgressDialog.dismissDialog();
	}

	private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

	private void showLankaBanglaUserInfo(LankaBanglaCustomerInfoResponse lankaBanglaCustomerInfoResponse) {
		if (getActivity() == null)
			return;

		@SuppressLint("InflateParams") final View customTitleView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_custom_title, null, false);
		@SuppressLint("InflateParams") final View customView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_card_customer_info_bill, null, false);

		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		final ImageButton closeButton = customTitleView.findViewById(R.id.close_button);
		final TextView titleTextView = customTitleView.findViewById(R.id.title_text_view);


		final TextView totalOutstandingTextView = customView.findViewById(R.id.total_outstanding_text_view);
		final TextView cardNumberTextView = customView.findViewById(R.id.card_number_text_view);
		final ImageView billClientLogoImageView = customView.findViewById(R.id.bill_client_logo_image_view);
		final ImageView cardLogoImageView = customView.findViewById(R.id.card_logo_image_view);
		final TextView minimumAmountTextView = customView.findViewById(R.id.minimum_amount_text_view);
		final TextView cardHolderNameTextView = customView.findViewById(R.id.card_holder_name_text_view);
		final Button payBillButton = customView.findViewById(R.id.pay_bill_button);

		billClientLogoImageView.setImageResource(R.drawable.ic_lankabd2);
		cardNumberTextView.setText(getCardNumber());
		cardHolderNameTextView.setText(lankaBanglaCustomerInfoResponse.getName());
		CardNumberValidator.Cards cards = CardNumberValidator.getCardType(lankaBanglaCustomerInfoResponse.getCardNumber());
		if (cards != null)
			cardLogoImageView.setImageResource(cards.getCardIconId());
		totalOutstandingTextView.setText(getString(R.string.balance_holder, numberFormat.format(new BigDecimal(lankaBanglaCustomerInfoResponse.getCreditBalance()))));
		minimumAmountTextView.setText(getString(R.string.balance_holder, numberFormat.format(new BigDecimal(lankaBanglaCustomerInfoResponse.getMinimumPay()))));
		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setCustomTitle(customTitleView)
				.setCancelable(false)
				.setView(customView)
				.create();
		alertDialog.show();

		titleTextView.setText(R.string.bill_details);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		payBillButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}
}
