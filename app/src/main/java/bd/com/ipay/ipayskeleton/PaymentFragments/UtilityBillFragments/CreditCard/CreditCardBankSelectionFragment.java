package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard;


import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.GetAvailableCreditCardBanks;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCardAmountInputFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Widget.View.CardNumberEditText;
import bd.com.ipay.ipayskeleton.Widgets.IPaySnackbar;

public class CreditCardBankSelectionFragment extends Fragment implements HttpResponseListener {
    private RecyclerView mBankListRecyclerView;
    private Button mContinueButton;
    private ArrayList<Bank> mBankList;
    private HttpRequestGetAsyncTask mGetBankListAsyncTask;
    private LinearLayout mProgressLayout;
    private LinearLayout mCardInfoLayout;
    private BankListAdapter bankListAdapter;
    private int clickedPosition;
    private TextView mTermsAndConditionsTextView;
    private CheckBox saveCardCheckBox;

    private CardNumberEditText mCardNumberEditText;
    private EditText mNameEditText;
    private int selectedBankIconId;
    private String selectedBankCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_card_bank_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContinueButton = view.findViewById(R.id.continue_button);
        mBankListRecyclerView = view.findViewById(R.id.user_bank_list_recycler_view);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.progress_layout);
        bankListAdapter = new BankListAdapter();
        mBankListRecyclerView.setAdapter(bankListAdapter);
        mTermsAndConditionsTextView = (TextView) view.findViewById(R.id.save_card_number);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mCardNumberEditText = view.findViewById(R.id.card_number);
        mNameEditText = view.findViewById(R.id.card_holder_name);
        mCardInfoLayout = view.findViewById(R.id.card_info_layout);
        saveCardCheckBox = view.findViewById(R.id.save_card_checkbox);
        mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardInfoLayout.getVisibility() != View.VISIBLE) {
                    showErrorMessage("Please select a bank ");
                } else {
                    if (verifyInput()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(IPayUtilityBillPayActionActivity.CARD_NUMBER_KEY,
                                mCardNumberEditText.getText().toString());
                        bundle.putString(IPayUtilityBillPayActionActivity.CARD_USER_NAME_KEY,
                                mNameEditText.getText().toString());
                        bundle.putString(IPayUtilityBillPayActionActivity.BANK_CODE, selectedBankCode);
                        bundle.putInt(IPayUtilityBillPayActionActivity.BANK_ICON, selectedBankIconId);
                        bundle.putBoolean(IPayUtilityBillPayActionActivity.SAVE_CARD_INFO, saveCardCheckBox.isChecked());
                        ((IPayUtilityBillPayActionActivity) getActivity()).
                                switchFragment(new CreditCardAmountInputFragment(), bundle, 2, true);

                    }
                }
            }
        });
        ((IPayUtilityBillPayActionActivity) getActivity()).setSupportActionBar(toolbar);
        ((IPayUtilityBillPayActionActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle("Credit Card Bill Pay");
        attemptGetBankList();
        clickedPosition = -1;
    }

    public int getBankIcon(Bank bank) {
        Resources resources = getContext().getResources();
        int resourceId;
        if (bank.getBankCode() != null)
            resourceId = resources.getIdentifier("ic_bank" + bank.getBankCode(), "drawable",
                    getContext().getPackageName());
        else
            resourceId = resources.getIdentifier("ic_bank" + "111", "drawable",
                    getContext().getPackageName());
        return resourceId;
        //return resources.getDrawable(resourceId);
    }

    protected void showErrorMessage(String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage) && getActivity() != null) {
            IPaySnackbar.error(mContinueButton, errorMessage, IPaySnackbar.LENGTH_SHORT).show();
        }
    }

    public String getCardNumber() {
        if (mCardNumberEditText.getText() != null)
            return mCardNumberEditText.getText().toString();
        else
            return "";
    }

    public String getCardHolderName() {
        if (mNameEditText.getText() != null) {
            return mNameEditText.getText().toString();
        } else {
            return "";
        }
    }

    public void attemptGetBankList() {
        if (mGetBankListAsyncTask != null) {
            return;
        } else {
            mGetBankListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_LIST,
                    Constants.BASE_URL_SM + Constants.URL_GET_BANK_LIST, getContext(), this, false);
            mGetBankListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    protected boolean verifyInput() {
        if (TextUtils.isEmpty(getCardNumber())) {
            showErrorMessage(getString(R.string.empty_card_number_message));
            return false;
        } else if (!CardNumberValidator.validateCardNumber(getCardNumber())) {
            showErrorMessage(getString(R.string.invalid_card_number_message));
            return false;
        } else {
            if (TextUtils.isEmpty(getCardHolderName())) {
                showErrorMessage(getString(R.string.enter_a_name));
                return false;
            } else {
                return true;
            }

        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        try {
            mGetBankListAsyncTask = null;
            if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
                return;
            } else {
                if (result.getApiCommand().equals(Constants.COMMAND_GET_BANK_LIST)) {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressLayout.setVisibility(View.GONE);
                        mBankList = new Gson().fromJson(result.getJsonString(), GetAvailableCreditCardBanks.class).getBankList();
                        mBankListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        mBankListRecyclerView.setAdapter(bankListAdapter);
                        bankListAdapter.notifyDataSetChanged();
                    } else {
                        Toaster.makeText(getContext(), "Bank List Fetch Failed", Toast.LENGTH_LONG);
                    }
                }
            }
        } catch (Exception e) {
            Toaster.makeText(getContext(), "Bank List Fetch Failed", Toast.LENGTH_LONG);
            mGetBankListAsyncTask = null;
        }
    }

    public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.BankViewHolder> {
        @NonNull
        @Override
        public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_bank_item, null, false);
            return new BankViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final BankViewHolder holder, final int position) {
            holder.bankNameTextView.setText(mBankList.get(position).getBankName());
            if (clickedPosition == position) {
                holder.bankIconImageView.setImageResource(R.drawable.ic_selected);

            } else {
                holder.bankIconImageView.setImageResource(getBankIcon(mBankList.get(position)));
            }
            holder.bankIconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedBankIconId = getBankIcon(mBankList.get(position));
                    holder.bankIconImageView.setImageResource(R.drawable.ic_selected);
                    clickedPosition = position;
                    selectedBankCode = mBankList.get(position).getBankCode();
                    mCardInfoLayout.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                }
            });
            holder.parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedBankIconId = getBankIcon(mBankList.get(position));
                    holder.bankIconImageView.setImageResource(R.drawable.ic_selected);
                    clickedPosition = position;
                    selectedBankCode = mBankList.get(position).getBankCode();
                    mCardInfoLayout.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mBankList.size();
        }

        public class BankViewHolder extends RecyclerView.ViewHolder {
            public TextView bankNameTextView;
            private ImageView bankIconImageView;
            private View parentView;


            public BankViewHolder(View itemView) {
                super(itemView);
                bankIconImageView = (ImageView) itemView.findViewById(R.id.bank_icon);
                bankNameTextView = (TextView) itemView.findViewById(R.id.bank_name);
                parentView = itemView;
            }
        }
    }
}
