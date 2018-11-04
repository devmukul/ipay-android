package bd.com.ipay.ipayskeleton.SourceOfFund;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.Beneficiary;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.GetBeneficiaryListResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

import static android.view.View.GONE;

public class SourceOfFundListShowFragment extends Fragment implements HttpResponseListener {

    private RecyclerView sourceOfFundListRecyclerView;
    private ImageView backButton;
    private TextView addNewTextView;

    private ArrayList<Beneficiary> beneficiaryArrayList;

    private HttpRequestGetAsyncTask getBeneficiaryListAsyncTask;

    private IpayProgressDialog ipayProgressDialog;

    private ProgressDialog progressDialog;

    private SourceOfFundListAdapter sourceOfFundListAdapter;

    private TextView noSourceOfFundTextView;
    private TextView titleTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_source_of_fund_list_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backButton = view.findViewById(R.id.back);
        addNewTextView = view.findViewById(R.id.add_new);
        titleTextView = view.findViewById(R.id.title);
        noSourceOfFundTextView = (TextView) view.findViewById(R.id.no_source_of_fund);
        noSourceOfFundTextView.setVisibility(GONE);
        attemptGetBeneficiaryList();
        sourceOfFundListAdapter = new SourceOfFundListAdapter();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addNewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        sourceOfFundListRecyclerView = view.findViewById(R.id.source_of_fund_list_recycler_view);
        sourceOfFundListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sourceOfFundListRecyclerView.setAdapter(sourceOfFundListAdapter);
    }

    private void attemptGetBeneficiaryList() {
        if (getBeneficiaryListAsyncTask != null) {
            return;
        } else {
            ipayProgressDialog = new IpayProgressDialog(getContext());
            ipayProgressDialog.setMessage("Please wait . . .");
            getBeneficiaryListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BENEFICIARY_LIST, Constants.BASE_URL_MM + Constants.URL_GET_BENEFICIARY,
                    getContext(), this, false);
            getBeneficiaryListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ipayProgressDialog.show();

        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            getBeneficiaryListAsyncTask = null;
            ipayProgressDialog.dismiss();
            return;
        } else {
            ipayProgressDialog.dismiss();
            GetBeneficiaryListResponse getBeneficiaryListResponse = null;
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getBeneficiaryListResponse = new Gson().fromJson(result.getJsonString(), GetBeneficiaryListResponse.class);
                    beneficiaryArrayList = getBeneficiaryListResponse.getBeneficiary();
                    sourceOfFundListAdapter.notifyDataSetChanged();
                    if (beneficiaryArrayList.size() > 0) {
                        noSourceOfFundTextView.setVisibility(GONE);
                        titleTextView.setVisibility(View.VISIBLE);
                        sourceOfFundListRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        noSourceOfFundTextView.setVisibility(View.VISIBLE);
                        titleTextView.setVisibility(GONE);
                        sourceOfFundListRecyclerView.setVisibility(GONE);
                    }
                } else {
                    Toast.makeText(getContext(), getBeneficiaryListResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), getBeneficiaryListResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public class SourceOfFundListAdapter extends RecyclerView.Adapter<SourceOfFundListAdapter.SourceOfFundViewHolder> {
        @NonNull
        @Override
        public SourceOfFundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SourceOfFundViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.list_source_of_fund, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SourceOfFundViewHolder holder, int position) {
            Beneficiary beneficiary = beneficiaryArrayList.get(position);
            holder.profileImageView.setProfilePicture(beneficiary.getUser().getProfilePictureUrl(), false);
            holder.nameTextView.setText(beneficiary.getUser().getName());
            holder.numberTextView.setText(beneficiary.getUser().getMobileNumber());
            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            if (beneficiaryArrayList == null) {
                return 0;
            } else {
                return beneficiaryArrayList.size();
            }
        }


        public class SourceOfFundViewHolder extends RecyclerView.ViewHolder {

            private ProfileImageView profileImageView;
            private TextView nameTextView;
            private TextView numberTextView;
            private ImageView deleteImageView;

            public SourceOfFundViewHolder(View itemView) {
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.name);
                numberTextView = (TextView) itemView.findViewById(R.id.number);
                profileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }
        }
    }
}
