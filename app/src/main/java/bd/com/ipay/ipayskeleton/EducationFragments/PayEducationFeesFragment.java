package bd.com.ipay.ipayskeleton.EducationFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.EducationPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.GetEnabledPayablesRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.PayableItem;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PayEducationFeesFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetEnabledPayablesTask = null;
    private PayableItem[] mEnablePayableItems;

    private TextView mEmptyTextView;
    private RecyclerView mPayablesRecyclerView;
    private PayablesListAdapter mPayablesListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFabCreatePayable;
    private RelativeLayout mPayLayout;
    private ImageView mPayNowButton;
    private TextView mAddedItemsTextView;

    private ArrayList<PayableItem> mEnabledPayableItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_education_pay, container, false);
        getActivity().setTitle(R.string.pay_education_fee);
        mEnabledPayableItemList = new ArrayList<>();

        mEmptyTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mAddedItemsTextView = (TextView) v.findViewById(R.id.items_added_text);
        mPayNowButton = (ImageView) v.findViewById(R.id.pay_now_button);
        mPayLayout = (RelativeLayout) v.findViewById(R.id.pay_now_layout);
        mFabCreatePayable = (FloatingActionButton) v.findViewById(R.id.fab_create_payable);
        mPayablesRecyclerView = (RecyclerView) v.findViewById(R.id.list_payables);
        mPayablesListAdapter = new PayablesListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPayablesRecyclerView.setLayoutManager(mLayoutManager);
        mPayablesRecyclerView.setAdapter(mPayablesListAdapter);

        mFabCreatePayable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnabledPayableItemList.size() == 0) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.please_wait, Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(EducationPaymentActivity.ARGS_ENABLED_PAYABLE_ITEMS, mEnabledPayableItemList);
                    ((EducationPaymentActivity) getActivity()).switchToAddPayableFragment(bundle);
                }
            }
        });

        if (EducationPaymentActivity.mMyPayableItems.size() > 0) {
            mPayLayout.setVisibility(View.VISIBLE);
            mAddedItemsTextView.setText("You have added " + EducationPaymentActivity.mMyPayableItems.size() + " fee items.");
        }

        mPayNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EducationPaymentActivity) getActivity()).switchToPaymentReviewFragment();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        if (EducationPaymentActivity.mMyPayableItems.size() > 0)
            mEmptyTextView.setVisibility(View.GONE);
        getEnabledPayableItems();
    }

    private void setPayableItemsAdapter() {
        for (PayableItem mPayableItem : mEnablePayableItems) {
            mEnabledPayableItemList.add(mPayableItem);
        }
    }

    private void getEnabledPayableItems() {
        if (mGetEnabledPayablesTask != null)
            return;

        GetEnabledPayablesRequestBuilder mGetEnabledPayablesRequestBuilder = new GetEnabledPayablesRequestBuilder(EducationPaymentActivity.institutionID);
        String mUrl = mGetEnabledPayablesRequestBuilder.getGeneratedUrl();

        mGetEnabledPayablesTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ENABLED_PAYABLES_LIST,
                mUrl, getActivity());
        mGetEnabledPayablesTask.mHttpResponseListener = this;

        mGetEnabledPayablesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result,getContext(),null)) {
            mGetEnabledPayablesTask = null;
            return;
        }

        Gson gson = new Gson();
        if (this.isAdded()) setContentShown(true);

        if (result.getApiCommand().equals(Constants.COMMAND_GET_ENABLED_PAYABLES_LIST)) {

            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    try {
                        mEnablePayableItems = gson.fromJson(result.getJsonString(), PayableItem[].class);
                        setPayableItemsAdapter();

                    } catch (Exception e) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.get_all_enabled_payables_failed, Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                        e.printStackTrace();
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.get_all_enabled_payables_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.get_all_enabled_payables_failed, Toast.LENGTH_SHORT).show();
            }

            mGetEnabledPayablesTask = null;
        }
    }

    private class PayablesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PayablesListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mPayableItemName;
            private final TextView mAmountTextView;
            private final TextView mDescriptionTextView;

            private CustomSelectorDialog mCustomSelectorDialog;
            private List<String> mPayableItemActionList;
            private final int ACTION_REMOVE = 0;

            public ViewHolder(final View itemView) {
                super(itemView);

                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                mDescriptionTextView = (TextView) itemView.findViewById(R.id.payable_item_description);
                mPayableItemName = (TextView) itemView.findViewById(R.id.payable_item_name);
            }

            public void bindView(final int pos) {

                final String payableItemName = EducationPaymentActivity.mMyPayableItems.get(pos).getName();
                String payableItemDescription = EducationPaymentActivity.mMyPayableItems.get(pos).getPayableAccountHead().getDescription();
                BigDecimal amount = EducationPaymentActivity.mMyPayableItems.get(pos).getInstituteFee();

                mAmountTextView.setText(amount + "");
                mPayableItemName.setText(payableItemName);
                if (payableItemDescription.length() > 0) {
                    mDescriptionTextView.setVisibility(View.VISIBLE);
                    mDescriptionTextView.setText(payableItemDescription);
                } else mDescriptionTextView.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPayableItemActionList = Arrays.asList(getResources().getStringArray(R.array.payable_education_action));
                        mCustomSelectorDialog = new CustomSelectorDialog(getActivity(), payableItemName, mPayableItemActionList);
                        mCustomSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                            @Override
                            public void onResourceSelected(int selectedIndex, String action) {
                                if (selectedIndex == ACTION_REMOVE) {
                                    EducationPaymentActivity.mMyPayableItems.remove(pos);
                                    mPayablesListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        mCustomSelectorDialog.show();
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_education_payable, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (EducationPaymentActivity.mMyPayableItems != null)
                return EducationPaymentActivity.mMyPayableItems.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
