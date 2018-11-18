package bd.com.ipay.ipayskeleton.SourceOfFund;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GenericResponseWithMessageOnly;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.Beneficiary;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.RemoveSponsorOrBeneficiaryRequest;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.Sponsor;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public abstract class IpayAbstractSpecificSourceOfFundListFragment extends Fragment {

    protected TextView titleTextView;
    public TextView helpTextView;
    public ImageView backButton;

    public TextView noDataTextView;
    public FloatingActionButton addNewResourceButton;
    public RecyclerView resourceListRecyclerView;

    public int recyclerViewLayoutId;

    public ArrayList<Sponsor> parentSponsorArrayList;
    public ArrayList<Beneficiary> parentBeneficiaryArrayList;

    public String type;

    public SourceOfFundListAdapter sourceOfFundListAdapter;

    public HttpDeleteWithBodyAsyncTask deleteSponsorAsyncTask;

    private IpayProgressDialog ipayProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_specific_source_of_fund_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        titleTextView = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back);
        helpTextView = view.findViewById(R.id.help);

        ipayProgressDialog = new IpayProgressDialog(getContext());
        noDataTextView = view.findViewById(R.id.no_data_text_view);
        addNewResourceButton = view.findViewById(R.id.add_new_resource);

        addNewResourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TYPE, type);
                ((SourceOfFundActivity) getActivity()).switchToAddSourceOfFundFragment(bundle);
            }
        });

        resourceListRecyclerView = view.findViewById(R.id.resource_list);
        setFragmentTitle("");
        setType();
        setBackButtonAction();
        setHelpAction();
        getSourceOfFundList();
        setRecyclerViewLayoutId();
        sourceOfFundListAdapter = new SourceOfFundListAdapter();
        resourceListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resourceListRecyclerView.setAdapter(sourceOfFundListAdapter);
        if (type.equals(Constants.BENEFICIARY)) {
            noDataTextView.setText("You do not have any beneficiary iPay user. Tap on the + icon to add one");
        } else {
            noDataTextView.setText("You have not added \n" +
                    "any iPay user as Source of Fund. \n" +
                    "To add one , tap  the (+) button.");
        }

    }

    public abstract void setType();

    public abstract void setRecyclerViewLayoutId();

    public abstract void getSourceOfFundList();

    public abstract void setNoDataText(String text);

    public abstract void setFragmentTitle(String title);

    public abstract void setBackButtonAction();

    public abstract void setHelpAction();

    private void attemptRemoveSponsorWithPinCheck(final long id) {
        new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
            @Override
            public void ifPinCheckedAndAdded(String pin) {
                attemptRemoveSponsorOrBeneficiary(id, pin);
            }
        });
    }

    public void attemptRemoveSponsorOrBeneficiary(long id, String pin) {
        if (deleteSponsorAsyncTask != null) {
            return;
        } else {
            RemoveSponsorOrBeneficiaryRequest removeSponsorOrBeneficiaryRequest = new RemoveSponsorOrBeneficiaryRequest(pin);
            deleteSponsorAsyncTask = new HttpDeleteWithBodyAsyncTask(Constants.COMMAND_REMOVE_SPONSOR_OR_BENEFICIARY,
                    Constants.BASE_URL_MM + Constants.URL_DELETE_SPONSOR + id, new Gson().toJson
                    (removeSponsorOrBeneficiaryRequest),
                    getContext(), new HttpResponseListener() {
                @Override
                public void httpResponseReceiver(GenericHttpResponse result) {
                    if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
                        deleteSponsorAsyncTask = null;
                        ipayProgressDialog.dismiss();
                        return;
                    } else {
                        deleteSponsorAsyncTask = null;
                        ipayProgressDialog.dismiss();
                        try {
                            GenericResponseWithMessageOnly genericResponseWithMessageOnly =
                                    new Gson().fromJson
                                            (result.getJsonString(), GenericResponseWithMessageOnly.class);
                            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                                getSourceOfFundList();
                                Toast.makeText(getContext(),
                                        genericResponseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(),
                                        genericResponseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(),
                                    getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }, false);
            deleteSponsorAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ipayProgressDialog.setMessage("Please wait . . .");
            ipayProgressDialog.show();
        }
    }

    public class SourceOfFundListAdapter extends RecyclerView.Adapter<SourceOfFundListAdapter.SponsorOrBeneficiaryViewHolder> {


        @NonNull
        @Override
        public SourceOfFundViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new SourceOfFundViewHolder(LayoutInflater.
                    from(getContext()).inflate(recyclerViewLayoutId, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SponsorOrBeneficiaryViewHolder sourceOfFundViewHolder, int i) {
            if (type.equals(Constants.BENEFICIARY)) {
                bindBeneficiaryView(sourceOfFundViewHolder, parentBeneficiaryArrayList.get(i));
            } else {
                bindSponsorView(sourceOfFundViewHolder, parentSponsorArrayList.get(i));
            }
        }

        public void bindSponsorView(SponsorOrBeneficiaryViewHolder holder, final Sponsor sponsor) {
            SourceOfFundViewHolder sourceOfFundViewHolder = null;
            if (recyclerViewLayoutId == R.layout.list_source_of_fund) {
                sourceOfFundViewHolder = (SourceOfFundViewHolder) holder;
            }
            sourceOfFundViewHolder.profileImageView.setProfilePicture(sponsor.getUser().getProfilePictureUrl(), false);
            sourceOfFundViewHolder.nameTextView.setText(sponsor.getUser().getName());
            sourceOfFundViewHolder.numberTextView.setText(sponsor.getUser().getMobileNumber());
            sourceOfFundViewHolder.editImageView.setVisibility(View.GONE);
            sourceOfFundViewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sponsor.getStatus().equals("PENDING")) {
                        new AlertDialog.Builder(getContext())
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //attemptRemoveSponsorOrBeneficiary(sponsor.getId(), "");
                                    }
                                }).setMessage("Do you want to remove this sponsor?")
                                .show();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        attemptRemoveSponsorWithPinCheck(sponsor.getId());
                                    }
                                }).setMessage("Do you want to remove this sponsor?")
                                .show();
                    }

                }
            });
        }

        public void bindBeneficiaryView(SponsorOrBeneficiaryViewHolder holder, final Beneficiary beneficiary) {
            SourceOfFundViewHolder sourceOfFundViewHolder = null;
            if (recyclerViewLayoutId == R.layout.list_source_of_fund) {
                sourceOfFundViewHolder = (SourceOfFundViewHolder) holder;
            }
            sourceOfFundViewHolder.editImageView.setVisibility(View.VISIBLE);
            sourceOfFundViewHolder.profileImageView.setProfilePicture(beneficiary.getUser().getProfilePictureUrl(), false);
            sourceOfFundViewHolder.nameTextView.setText(beneficiary.getUser().getName());
            sourceOfFundViewHolder.numberTextView.setText(beneficiary.getUser().getMobileNumber());
            sourceOfFundViewHolder.editImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.BENEFICIARY, beneficiary);
                    ((SourceOfFundActivity) getActivity()).switchToUpdatePermissionBottomSheetFragment(bundle);
                }
            });
            sourceOfFundViewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    attemptRemoveSponsorWithPinCheck(beneficiary.getId());
                                }
                            }).setMessage("Do you want to remove this beneficiary?")
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            if (type.equals(Constants.SPONSOR)) {

                if (parentSponsorArrayList == null) {
                    return 0;
                } else {
                    return parentSponsorArrayList.size();
                }
            } else if (type.equals(Constants.BENEFICIARY)) {
                if (parentBeneficiaryArrayList == null) {
                    return 0;
                } else {
                    return parentBeneficiaryArrayList.size();
                }
            } else {
                return 0;
            }
        }

        public class SponsorOrBeneficiaryViewHolder extends RecyclerView.ViewHolder {

            public SponsorOrBeneficiaryViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        public class SourceOfFundViewHolder extends SponsorOrBeneficiaryViewHolder {
            private ProfileImageView profileImageView;
            private TextView nameTextView;
            private TextView numberTextView;
            private ImageView deleteImageView;
            private ImageView editImageView;

            public SourceOfFundViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.name);
                numberTextView = (TextView) itemView.findViewById(R.id.number);
                profileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                deleteImageView = (ImageView) itemView.findViewById(R.id.delete);
                editImageView = (ImageView) itemView.findViewById(R.id.edit);
            }
        }
    }
}
