package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BASIC_PROFILE;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_DOCUMENTS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_INFO;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.INTRODUCER;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PERSONAL_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PHOTOID;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_COMPLETENESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_INFO;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_PICTURE;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFICATION_DOCUMENT;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFIED_EMAIL;

public class ProfileCompletionFragment extends ProgressFragment implements HttpResponseListener {
    private RecyclerView mProfileCompletionRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProfileCompletionAdapter mProfileCompletionAdapter;

    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private TextView mProfileCompletionStatusView;
    private ProgressBar mProfileCompletionStatusProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_completion, container, false);

        getActivity().setTitle(getString(R.string.profile_completeness));

        mProfileCompletionRecyclerView = (RecyclerView) v.findViewById(R.id.list_profile_completion);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mProfileCompletionRecyclerView.setLayoutManager(mLayoutManager);

        mProfileCompletionStatusView = (TextView) v.findViewById(R.id.textview_profile_completion_status);
        mProfileCompletionStatusProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar_profile_completion_status);

        getProfileCompletionStatus();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void populateView() {
        mProfileCompletionStatusResponse.analyzeProfileCompletionData();
        mProfileCompletionAdapter = new ProfileCompletionAdapter();
        mProfileCompletionRecyclerView.setAdapter(mProfileCompletionAdapter);

        mProfileCompletionStatusView.setText("Your profile is " + mProfileCompletionStatusResponse.getCompletionPercentage()
                + "% complete");
        mProfileCompletionStatusProgressBar.setProgress(mProfileCompletionStatusResponse.getCompletionPercentage());

        if (this.isAdded()) setContentShown(true);
    }

    private void getProfileCompletionStatus() {
        if (mGetProfileCompletionStatusTask != null) {
            return;
        }

        mGetProfileCompletionStatusTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_COMPLETION_STATUS, getActivity(), this);
        mGetProfileCompletionStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetProfileCompletionStatusTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);

            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS)) {
            try {
                mProfileCompletionStatusResponse = gson.fromJson(result.getJsonString(), ProfileCompletionStatusResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    populateView();
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mProfileCompletionStatusResponse.getMessage(), Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_fetching_profile_completion_status, Toast.LENGTH_LONG).show();
            }

            mGetProfileCompletionStatusTask = null;
        }
    }

    private class ProfileCompletionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_BASIC_INFO = 0;
        private static final int VIEW_TYPE_ADDRESS = 1;
        private static final int VIEW_TYPE_IDENTIFICATION = 2;
        private static final int VIEW_TYPE_LINK_BANK = 3;
        private static final int VIEW_TYPE_HEADER = 100;

        private boolean hasServicePermission(String propertyName) {
            switch (propertyName) {
                case Constants.VERIFY_BANK:
                case Constants.LINK_BANK:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS);
                case BASIC_PROFILE:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_PROFILE);
                case BUSINESS_INFO:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_INFO);
                case PROFILE_PICTURE:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_PROFILE_PICTURE);
                case INTRODUCER:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_INTRODUCERS);
                case PERSONAL_ADDRESS:
                case BUSINESS_ADDRESS:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS);
                case VERIFIED_EMAIL:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_EMAILS);
                case BUSINESS_DOCUMENTS:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_DOCS);
                case VERIFICATION_DOCUMENT:
                case PHOTOID:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS);
                case PROFILE_COMPLETENESS:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE_COMPLETION);
                case PROFILE_INFO:
                    return ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE);
                default:
                    return true;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int tempPosition = 0;

            // Basic Info Header
            tempPosition += 1;
            if (position < tempPosition) {
                return VIEW_TYPE_HEADER;
            }

            // Basic Info list items
            tempPosition += mProfileCompletionStatusResponse.getBasicInfoCompletionDetails().size();
            if (position < tempPosition) {
                return VIEW_TYPE_BASIC_INFO;
            }

            // Address Header
            tempPosition += 1;
            if (position < tempPosition) {
                return VIEW_TYPE_HEADER;
            }

            // Address list items
            tempPosition += mProfileCompletionStatusResponse.getAddressCompletionDetails().size();
            if (position < tempPosition) {
                return VIEW_TYPE_ADDRESS;
            }

            // Identification Header
            tempPosition += 1;
            if (position < tempPosition) {
                return VIEW_TYPE_HEADER;
            }

            // Identification list items
            tempPosition += mProfileCompletionStatusResponse.getIdentificationCompletionDetails().size();
            if (position < tempPosition) {
                return VIEW_TYPE_IDENTIFICATION;
            }

            // Link Bank Header
            tempPosition += 1;
            if (position < tempPosition) {
                return VIEW_TYPE_HEADER;
            }

            // Link Bank list items
            tempPosition += mProfileCompletionStatusResponse.getLinkBankCompletionDetails().size();
            if (position < tempPosition) {
                return VIEW_TYPE_LINK_BANK;
            }

            return super.getItemViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == VIEW_TYPE_HEADER) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
                return new HeaderViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_completion, parent, false);
                RecyclerView.ViewHolder vh;

                if (viewType == VIEW_TYPE_BASIC_INFO) {
                    vh = new BasicInfoViewHolder(v);
                } else if (viewType == VIEW_TYPE_ADDRESS) {
                    vh = new AddressViewHolder(v);
                } else if (viewType == VIEW_TYPE_IDENTIFICATION) {
                    vh = new IdentificationViewHolder(v);
                } else {
                    vh = new LinkBankViewHolder(v);
                }

                return vh;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int tempPosition = 0;

            // Basic Info Header
            tempPosition += 1;
            if (position < tempPosition) {
                ((HeaderViewHolder) holder).bindViewHeader(getString(R.string.basic_info));
                return;
            }

            // Basic Info list items
            tempPosition += mProfileCompletionStatusResponse.getBasicInfoCompletionDetails().size();
            if (position < tempPosition) {
                ((BasicInfoViewHolder) holder).bindViewProfileCompletion(
                        (position - (tempPosition - mProfileCompletionStatusResponse.getBasicInfoCompletionDetails().size())));
                return;
            }

            // Address Header
            tempPosition += 1;
            if (position < tempPosition) {
                ((HeaderViewHolder) holder).bindViewHeader(getString(R.string.address));
                return;
            }

            // Address list items
            tempPosition += mProfileCompletionStatusResponse.getAddressCompletionDetails().size();
            if (position < tempPosition) {
                ((AddressViewHolder) holder).bindViewProfileCompletion(
                        (position - (tempPosition - mProfileCompletionStatusResponse.getAddressCompletionDetails().size())));
                return;
            }

            // Identification Header
            tempPosition += 1;
            if (position < tempPosition) {
                ((HeaderViewHolder) holder).bindViewHeader(getString(R.string.identification));
                return;
            }

            // Identification list items
            tempPosition += mProfileCompletionStatusResponse.getIdentificationCompletionDetails().size();
            if (position < tempPosition) {
                ((IdentificationViewHolder) holder).bindViewProfileCompletion(
                        (position - (tempPosition - mProfileCompletionStatusResponse.getIdentificationCompletionDetails().size())));
                return;
            }

            // Link Bank Header
            tempPosition += 1;
            if (position < tempPosition) {
                ((HeaderViewHolder) holder).bindViewHeader(getString(R.string.link_bank));
                return;
            }

            // Link Bank list items
            tempPosition += mProfileCompletionStatusResponse.getLinkBankCompletionDetails().size();
            if (position < tempPosition) {
                ((LinkBankViewHolder) holder).bindViewProfileCompletion(
                        (position - (tempPosition - mProfileCompletionStatusResponse.getLinkBankCompletionDetails().size())));
            }

        }

        @Override
        public int getItemCount() {
            return 1 + mProfileCompletionStatusResponse.getBasicInfoCompletionDetails().size() +
                    1 + mProfileCompletionStatusResponse.getAddressCompletionDetails().size() +
                    1 + mProfileCompletionStatusResponse.getIdentificationCompletionDetails().size() +
                    1 + mProfileCompletionStatusResponse.getLinkBankCompletionDetails().size();
        }

        public abstract class ProfileCompletionViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;
            private final ImageView profileCompletionIcon;
            private final TextView titleView;
            private final TextView currentStatusView;
            private final ImageView verificationStatus;

            public ProfileCompletionViewHolder(final View itemView) {
                super(itemView);

                this.itemView = itemView;
                profileCompletionIcon = (ImageView) itemView.findViewById(R.id.profile_completion_icon);
                titleView = (TextView) itemView.findViewById(R.id.textview_title);
                currentStatusView = (TextView) itemView.findViewById(R.id.textview_current_status);
                verificationStatus = (ImageView) itemView.findViewById(R.id.verification_status);
            }

            public abstract void bindViewProfileCompletion(int position);

            public void bindViewProfileCompletion(final ProfileCompletionStatusResponse.PropertyDetails propertyDetails) {
                if (propertyDetails.getPropertyIcon() != null)
                    profileCompletionIcon.setImageDrawable(getResources().getDrawable(propertyDetails.getPropertyIcon()));
                titleView.setText(propertyDetails.getPropertyTitle());

                if (!propertyDetails.isCompleted() && propertyDetails.getThreshold() > 1) {
                    currentStatusView.setText(propertyDetails.getValue() + "/" + propertyDetails.getThreshold() + " Completed");
                    currentStatusView.setVisibility(View.VISIBLE);
                } else {
                    currentStatusView.setVisibility(View.GONE);
                }

                if (propertyDetails.isCompleted()) {
                    verificationStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                    verificationStatus.setVisibility(View.VISIBLE);
                } else {
                    titleView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    verificationStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_invite));
                    verificationStatus.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!hasServicePermission(propertyDetails.getPropertyName())) {
                            return;
                        }
                        ((ProfileActivity) getActivity()).switchToFragment(propertyDetails.getPropertyName(), null, true);
                    }
                });
            }
        }

        public class BasicInfoViewHolder extends ProfileCompletionViewHolder {

            public BasicInfoViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bindViewProfileCompletion(int position) {
                bindViewProfileCompletion(mProfileCompletionStatusResponse.getBasicInfoCompletionDetails().get(position));
            }
        }

        public class AddressViewHolder extends ProfileCompletionViewHolder {

            public AddressViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bindViewProfileCompletion(int position) {
                bindViewProfileCompletion(mProfileCompletionStatusResponse.getAddressCompletionDetails().get(position));
            }
        }

        public class IdentificationViewHolder extends ProfileCompletionViewHolder {

            public IdentificationViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bindViewProfileCompletion(int position) {
                bindViewProfileCompletion(mProfileCompletionStatusResponse.getIdentificationCompletionDetails().get(position));
            }
        }

        public class LinkBankViewHolder extends ProfileCompletionViewHolder {

            public LinkBankViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bindViewProfileCompletion(int position) {
                bindViewProfileCompletion(mProfileCompletionStatusResponse.getLinkBankCompletionDetails().get(position));
            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            private final TextView headerView;

            public HeaderViewHolder(final View itemView) {
                super(itemView);

                headerView = (TextView) itemView.findViewById(R.id.textview_header);
            }

            public void bindViewHeader(String title) {
                headerView.setText(title);
            }
        }
    }
}
