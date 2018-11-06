package bd.com.ipay.ipayskeleton.SourceOfFund;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpDeleteWithBodyAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GenericResponseWithMessageOnly;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.GetSponsorListResponse;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.RemoveSponsorRequest;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.Sponsor;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

import static android.view.View.GONE;

public class SourceOfFundListShowFragment extends Fragment implements HttpResponseListener {

    private RecyclerView sourceOfFundListRecyclerView;
    private ImageView backButton;
    private TextView addNewTextView;

    private ArrayList<Sponsor> sponsorArrayList;

    private HttpRequestGetAsyncTask getSponsorListAsyncTask;

    private HttpDeleteWithBodyAsyncTask deleteSponsorAsyncTask;

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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        addNewTextView = view.findViewById(R.id.add_new);
        titleTextView = view.findViewById(R.id.title);
        noSourceOfFundTextView = (TextView) view.findViewById(R.id.no_source_of_fund);
        noSourceOfFundTextView.setVisibility(GONE);
        attemptGetSponsorList();
        sourceOfFundListAdapter = new SourceOfFundListAdapter();
        addNewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SourceOfFundActivity) (getActivity())).switchToAddSourceOfFundFragment();
            }
        });
        sourceOfFundListRecyclerView = view.findViewById(R.id.source_of_fund_list_recycler_view);
        sourceOfFundListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sourceOfFundListRecyclerView.setAdapter(sourceOfFundListAdapter);
    }

    private void attemptGetSponsorList() {
        if (getSponsorListAsyncTask != null) {
            return;
        } else {
            ipayProgressDialog = new IpayProgressDialog(getContext());
            ipayProgressDialog.setMessage("Please wait . . .");
            getSponsorListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SPONSOR_LIST, Constants.BASE_URL_MM + Constants.URL_GET_SPONSOR,
                    getContext(), this, false);
            getSponsorListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ipayProgressDialog.show();

        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            getSponsorListAsyncTask = null;
            ipayProgressDialog.dismiss();
            return;
        } else {
            ipayProgressDialog.dismiss();
            GetSponsorListResponse getSponsorListResponse = null;
            try {
                if (result.getApiCommand().equals(Constants.COMMAND_GET_SPONSOR_LIST)) {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        getSponsorListResponse = new Gson().fromJson(result.getJsonString(), GetSponsorListResponse.class);
                        sponsorArrayList = getSponsorListResponse.getSponsor();
                        sourceOfFundListAdapter.notifyDataSetChanged();
                        if (sponsorArrayList.size() > 0) {
                            noSourceOfFundTextView.setVisibility(GONE);
                            titleTextView.setVisibility(View.VISIBLE);
                            sourceOfFundListRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            noSourceOfFundTextView.setVisibility(View.VISIBLE);
                            titleTextView.setVisibility(GONE);
                            sourceOfFundListRecyclerView.setVisibility(GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), getSponsorListResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    getSponsorListAsyncTask = null;

                } else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_SPONSOR)) {
                    GenericResponseWithMessageOnly responseWithMessageOnly = new Gson().fromJson
                            (result.getJsonString(), GenericResponseWithMessageOnly.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        Toast.makeText(getContext(), responseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                        attemptGetSponsorList();
                    } else {
                        Toast.makeText(getContext(), responseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    deleteSponsorAsyncTask = null;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), getSponsorListResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void attemptRemoveSponsor(long id) {
        if (deleteSponsorAsyncTask != null) {
            return;
        } else {
            RemoveSponsorRequest removeSponsorRequest = new RemoveSponsorRequest("");
            deleteSponsorAsyncTask = new HttpDeleteWithBodyAsyncTask(Constants.COMMAND_REMOVE_SPONSOR,
                    Constants.BASE_URL_MM + Constants.URL_DELETE_SPONSOR + id, new Gson().toJson(removeSponsorRequest),
                    getContext(), this, false);
            deleteSponsorAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ipayProgressDialog.setMessage("Please wait . . .");
            ipayProgressDialog.show();
        }
    }

    public class SourceOfFundListAdapter extends RecyclerView.Adapter<SourceOfFundListAdapter.SourceOfFundViewHolder> {
        @NonNull
        @Override
        public SourceOfFundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SourceOfFundViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.list_source_of_fund, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SourceOfFundViewHolder holder, final int position) {
            final Sponsor sponsor = sponsorArrayList.get(position);
            holder.profileImageView.setProfilePicture(sponsor.getUser().getProfilePictureUrl(), false);
            holder.nameTextView.setText(sponsor.getUser().getName());
            holder.numberTextView.setText(sponsor.getUser().getMobileNumber());
            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sponsor.getStatus().equals("PENDING")) {
                        new AlertDialog.Builder(getContext())
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        attemptRemoveSponsor(sponsor.getId());
                                    }
                                }).setMessage("Do you want to remove this sponsor?")
                                .show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            if (sponsorArrayList == null) {
                return 0;
            } else {
                return sponsorArrayList.size();
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
                deleteImageView = (ImageView) itemView.findViewById(R.id.delete);
            }
        }
    }
}
