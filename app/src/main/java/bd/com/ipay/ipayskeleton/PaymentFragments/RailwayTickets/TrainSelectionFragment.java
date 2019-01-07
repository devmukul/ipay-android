package bd.com.ipay.ipayskeleton.PaymentFragments.RailwayTickets;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetTicketInfoRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetTicketInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetTrainListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.TrainList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.GetAvailableCreditCardBanks;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.LankaBanglaCustomerInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard.Bank;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard.CreditCardInfoInputFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Card.LankaBanglaAmountInputFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.Widget.View.BillDetailsDialog;
import bd.com.ipay.ipayskeleton.Widget.View.TicketDetailsDialog;

public class TrainSelectionFragment extends Fragment implements HttpResponseListener {
    private RecyclerView mTrainListRecyclerView;
    public static List<TrainList> mTrainList;
    private LinearLayout mProgressLayout;
    private BankListAdapter trainListAdapter;
    //private int clickedPosition;
    private int selectedTrainIconId;
    private String selectedTrainCode;
    private CustomProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mGetTrainInfoAsyncTask = null;
    private GetTicketInfoResponse mTicketInfoResponse;
    private GetTicketInfoRequest mTicketInfoRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_train_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((IPayUtilityBillPayActionActivity) getActivity()).setSupportActionBar(toolbar);
        ((IPayUtilityBillPayActionActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle("Buy Railway Ticket");
        mProgressDialog = new CustomProgressDialog(getActivity());

//        System.out.print("Test Tax >> "+getArguments().toString());
//        if (getArguments() != null) {
//            mTrainResponse = (GetTrainListResponse) getArguments().getSerializable("TrainList");
//            System.out.print("Test Tax >> "+mTrainResponse.getTrainList().size());
//        }

        System.out.print("Test Tax >> "+mTrainList.size());

        mTrainListRecyclerView = view.findViewById(R.id.user_bank_list_recycler_view);
        mProgressLayout = view.findViewById(R.id.progress_layout);
        trainListAdapter = new BankListAdapter(getContext(), mTrainList);
        mTrainListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTrainListRecyclerView.setAdapter(trainListAdapter);


//        attemptGetBankList();

//        mTrainList = new Gson().fromJson(result.getJsonString(), GetAvailableCreditCardBanks.class).getBankList();
//        mTrainListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mTrainListRecyclerView.setAdapter(trainListAdapter);
        trainListAdapter.notifyDataSetChanged();
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
    }

    protected void performContinueAction() {
        if (!Utilities.isConnectionAvailable(getContext())) {
            Toaster.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
        } else if (mGetTrainInfoAsyncTask != null) {
            return;
        }
        mGetTrainInfoAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_LANKA_BANGLA_CUSTOMER_INFO,
                "http://10.10.10.11:8866/api/utility/cns/ticket-query/", null, getContext(), this, false);
        mGetTrainInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mProgressDialog.setTitle(R.string.please_wait_no_ellipsis);
        mProgressDialog.setMessage(getString(R.string.fetching_user_info));
        mProgressDialog.showDialog();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() == null)
            return;

        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mGetTrainInfoAsyncTask = null;
            if (result != null && result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                GetTicketInfoResponse lankaBanglaCustomerInfoResponse = new Gson().fromJson(result.getJsonString(), GetTicketInfoResponse.class);
                if (!TextUtils.isEmpty(lankaBanglaCustomerInfoResponse.getMessage())) {
                    Toaster.makeText(getActivity(), lankaBanglaCustomerInfoResponse.getMessage(), Toast.LENGTH_SHORT);
                } else {
                    Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                }
            }
        } else {
            try {
                switch (result.getApiCommand()) {
                    case Constants.COMMAND_GET_LANKA_BANGLA_CUSTOMER_INFO:
                        mGetTrainInfoAsyncTask = null;
                        GetTicketInfoResponse lankaBanglaCustomerInfoResponse = new Gson().fromJson(result.getJsonString(), GetTicketInfoResponse.class);
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

    private void showTicketInfo(final GetTicketInfoResponse ticketInfoResponse) {
        if (getActivity() == null)
            return;

        final TicketDetailsDialog billDetailsDialog = new TicketDetailsDialog(getContext());
        billDetailsDialog.setTitle("Ticket Info");
        billDetailsDialog.setTrainName(ticketInfoResponse.getTrainNumber().toString());
        billDetailsDialog.setClassName(ticketInfoResponse.getClassName());
        billDetailsDialog.setDate(ticketInfoResponse.getJourneyDate());
        billDetailsDialog.setFareAmount(ticketInfoResponse.getFare());
        billDetailsDialog.setVatAmount(ticketInfoResponse.getVat());
        billDetailsDialog.setNetAmount(ticketInfoResponse.getTotalFare());

        billDetailsDialog.setCloseButtonAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billDetailsDialog.cancel();
            }
        });
        billDetailsDialog.setBuyTicketButtonAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billDetailsDialog.cancel();
                Bundle bundle = new Bundle();
                bundle.putInt(LankaBanglaAmountInputFragment.TOTAL_OUTSTANDING_AMOUNT_KEY, Integer.parseInt(ticketInfoResponse.getCreditBalance()));
                bundle.putInt(LankaBanglaAmountInputFragment.MINIMUM_PAY_AMOUNT_KEY, Integer.parseInt(ticketInfoResponse.getMinimumPay()));
                bundle.putString(LankaBanglaAmountInputFragment.CARD_NUMBER_KEY, ticketInfoResponse.getCardNumber());
                bundle.putString(LankaBanglaAmountInputFragment.CARD_USER_NAME_KEY, ticketInfoResponse.getName());
                Utilities.hideKeyboard(getActivity());
                final LankaBanglaAmountInputFragment lankaBanglaAmountInputFragment = new LankaBanglaAmountInputFragment();

                if (getActivity() instanceof IPayUtilityBillPayActionActivity) {
                    ((IPayUtilityBillPayActionActivity) getActivity()).switchFragment(lankaBanglaAmountInputFragment, bundle, 2, true);
                }
            }
        });
        billDetailsDialog.show();
    }

//    public void attemptGetBankList() {
//        if (mGetBankListAsyncTask != null) {
//            return;
//        } else {
//            mGetBankListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_LIST,
//                    Constants.BASE_URL_SM + Constants.URL_GET_BANK_LIST, getContext(), this, false);
//            mGetBankListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//    }

//    @Override
//    public void httpResponseReceiver(GenericHttpResponse result) {
//        try {
//            mGetBankListAsyncTask = null;
//            if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
//                return;
//            } else {
//                if (result.getApiCommand().equals(Constants.COMMAND_GET_BANK_LIST)) {
//                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
//                        mProgressLayout.setVisibility(View.GONE);
//                        mTrainList = new Gson().fromJson(result.getJsonString(), GetAvailableCreditCardBanks.class).getBankList();
//                        mTrainListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        mTrainListRecyclerView.setAdapter(trainListAdapter);
//                        trainListAdapter.notifyDataSetChanged();
//                    } else {
//                        Toaster.makeText(getContext(), "Bank List Fetch Failed", Toast.LENGTH_LONG);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Toaster.makeText(getContext(), "Bank List Fetch Failed", Toast.LENGTH_LONG);
//            mGetBankListAsyncTask = null;
//        }
//    }

    public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.BankViewHolder> {

        Context c;
        List<TrainList> trainList;

        public BankListAdapter(Context c, List<TrainList> mTrainList) {
            this.c = c;
            this.trainList = mTrainList;
        }

        @NonNull
        @Override
        public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_train_item, null, false);
            return new BankViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final BankViewHolder holder, final int position) {
            holder.trainTimeTextView.setText(trainList.get(position).getDepartureTime());
            holder.trainNameTextView.setText(trainList.get(position).getTrainName());
            holder.ticketClassTextView.setText(trainList.get(position).getClassList().get(0));

//            holder.bankIconImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    selectedTrainIconId = getBankIcon(mTrainList.get(position));
//                    selectedTrainCode = mTrainList.get(position).getBankCode();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(IPayUtilityBillPayActionActivity.BANK_CODE, selectedTrainCode);
//                    bundle.putInt(IPayUtilityBillPayActionActivity.BANK_ICON, selectedTrainIconId);
//                    ((IPayUtilityBillPayActionActivity) getActivity()).
//                            switchFragment(new CreditCardInfoInputFragment(), bundle, 2, true);
//                }
//            });
//            holder.parentView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    selectedTrainIconId = getBankIcon(mTrainList.get(position));
//                    selectedTrainCode = mTrainList.get(position).getBankCode();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(IPayUtilityBillPayActionActivity.BANK_CODE, selectedTrainCode);
//                    bundle.putInt(IPayUtilityBillPayActionActivity.BANK_ICON, selectedTrainIconId);
//                    ((IPayUtilityBillPayActionActivity) getActivity()).
//                            switchFragment(new CreditCardInfoInputFragment(), bundle, 2, true);
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return trainList.size();
        }

        public class BankViewHolder extends RecyclerView.ViewHolder {
            public TextView trainTimeTextView;
            public TextView trainNameTextView;
            public TextView ticketClassTextView;
            private View parentView;


            public BankViewHolder(View itemView) {
                super(itemView);
                trainTimeTextView = itemView.findViewById(R.id.train_time);
                trainNameTextView = itemView.findViewById(R.id.train_name);
                ticketClassTextView = itemView.findViewById(R.id.ticket_class);
                parentView = itemView;
            }
        }
    }
}
