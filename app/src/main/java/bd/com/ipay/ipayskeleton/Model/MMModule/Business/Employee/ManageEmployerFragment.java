package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.BusinessListRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;

public class ManageEmployerFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetEmployerTask = null;
    private GetBusinessListResponse mGetBusinessListResponse;

    private List<Business> mBusinessList;
    private RecyclerView mManageEmployerView;
    private EmployerAdapter mEmployerAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_employer, container, false);

        mManageEmployerView = (RecyclerView) v.findViewById(R.id.list_employer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mManageEmployerView.setLayoutManager(layoutManager);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBusinessInvitationList();
        setContentShown(false);
    }

    private void getBusinessInvitationList() {
        if (mGetEmployerTask != null) {
            return;
        }

        BusinessListRequestBuilder businessListRequestBuilder = new BusinessListRequestBuilder();
        mGetEmployerTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_LIST,
                businessListRequestBuilder.getAcceptedBusinessListUri(), getActivity(), this);
        mGetEmployerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (isAdded())
            setContentShown(true);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {

            mGetEmployerTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_LIST)) {
            try {
                mGetBusinessListResponse = gson.fromJson(result.getJsonString(), GetBusinessListResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mBusinessList = mGetBusinessListResponse.getBusinessList();
                    mEmployerAdapter = new EmployerAdapter();
                    mManageEmployerView.setAdapter(mEmployerAdapter);

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mGetBusinessListResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.failed_fetching_employer_list, Toast.LENGTH_SHORT).show();
            }

            mGetEmployerTask = null;
        }
    }

    private class EmployerAdapter extends RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder> {
        public class EmployerViewHolder extends RecyclerView.ViewHolder {

            private final ProfileImageView mProfilePictureView;
            private final TextView mNameView;
            private final TextView mMobileNumberView;

            private final View mOptionsLayout;
            private final Button mSwitchAccountButton;
            private final Button mShowPrivilegesButton;
            private final Button mResignButton;

            public EmployerViewHolder(View itemView) {
                super(itemView);

                mProfilePictureView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);

                mOptionsLayout = itemView.findViewById(R.id.options_layout);
                mSwitchAccountButton = (Button) itemView.findViewById(R.id.button_switch_account);
                mShowPrivilegesButton = (Button) itemView.findViewById(R.id.button_show_privileges);
                mResignButton = (Button) itemView.findViewById(R.id.button_resign);
            }

            public void bindView(int pos) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOptionsLayout.getVisibility() == View.VISIBLE)
                            mOptionsLayout.setVisibility(View.GONE);
                        else
                            mOptionsLayout.setVisibility(View.VISIBLE);
                    }
                });

                final Business business = mBusinessList.get(pos);

                mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + business.getProfilePictureUrl(),
                        false);
                mNameView.setText(business.getName());
                mMobileNumberView.setText(business.getMobileNumber());

                mSwitchAccountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TokenManager.setOperatingOnAccountId(Long.toString(business.getAssociationId()));
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

                mShowPrivilegesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                mResignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }

        @Override
        public EmployerAdapter.EmployerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_manage_employer, parent, false);

            return new EmployerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(EmployerAdapter.EmployerViewHolder holder, int position) {
            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            if (mBusinessList == null)
                return 0;
            else
                return mBusinessList.size();
        }
    }
}
