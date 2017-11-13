package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFactorAuthServicesListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFactorAuthServicesListWithOTPRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFactorAuthSettingsSaveResponse;
import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFactorAuthService;
import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFactorAuthServiceGroup;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class ImplementTwoFactorAuthenticationSettingsFragment extends Fragment implements HttpResponseListener, OTPVerificationForTwoFactorAuthenticationServicesDialog.dismissListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressDialog mProgressDialog;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private HttpRequestGetAsyncTask mGetTwoFaSettingsAsynctask;
    private TwoFactorAuthServicesListResponse mTwoFaServiceResponse;

    private HttpRequestPutAsyncTask mPutTwoFaSettingsAsyncTask;

    private int mCurrentSettings = 0;
    private int mChangedSettings = 0;
    private int mTotalServices = 0;

    List<TwoFactorAuthServiceGroup> mTwoFaServiceList;
    List<TwoFactorAuthService> mChangedList;

    private String mUri;
    private String mJsonString;


    HashMap<Integer, TwoFactorAuthService> mPositionToServiceIDMap = new HashMap<Integer, TwoFactorAuthService>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2fa_implement, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        getTwoFaSettings();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyler_view__two_factor_auth);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChangedList = new ArrayList<>();
        return view;
    }

    private void getTwoFaSettings() {
        String API_COMMAND = Constants.COMMAND_GET_TWO_FACTOR_AUTH_SETTINGS;
        String mUri = Constants.BASE_URL_MM + Constants.URL_TWO_FA_SETTINGS;
        mGetTwoFaSettingsAsynctask = new HttpRequestGetAsyncTask(API_COMMAND, mUri, getActivity());
        mGetTwoFaSettingsAsynctask.mHttpResponseListener = this;
        try {
            mGetTwoFaSettingsAsynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    int setBit(int N, int pos) {
        return N = N | (1 << pos);
    }

    int resetBit(int N, int pos) {
        return N = N & ~(1 << pos);
    }

    boolean check(int N, int pos) {
        if ((N & (1 << pos)) != 0)
            return true;
        else
            return false;
    }

    private void setCurrentSettings() {
        int pos = 0;
        for (int i = 0; i < mTwoFaServiceList.size(); i++) {
            for (int j = 0; j < mTwoFaServiceList.get(i).getServices().size(); j++) {
                if (mTwoFaServiceList.get(i).getServices().get(j).getIsEnabled()) {
                    mPositionToServiceIDMap.put(pos, mTwoFaServiceList.get(i).getServices().get(j));
                    mCurrentSettings = setBit(mCurrentSettings, pos);
                    pos++;
                } else {
                    mPositionToServiceIDMap.put(pos, mTwoFaServiceList.get(i).getServices().get(j));
                    mCurrentSettings = resetBit(mCurrentSettings, pos);
                    pos++;
                }
                mTotalServices++;
            }
        }
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void getChangedSettings() {
        int pos = 0;
        for (int i = 0; i < mTwoFaServiceList.size(); i++) {
            for (int j = 0; j < mTwoFaServiceList.get(i).getServices().size(); j++) {
                if (mTwoFaServiceList.get(i).getServices().get(j).getIsEnabled()) {
                    mChangedSettings = setBit(mChangedSettings, pos);
                    pos++;
                } else {
                    mChangedSettings = resetBit(mChangedSettings, pos);
                    pos++;
                }
            }
        }
    }

    public void attemptSaveTwoFaSettings(List<TwoFactorAuthService> mChangedList) {
        if (mPutTwoFaSettingsAsyncTask != null)
            return;
        Gson gson = new Gson();
        String API_COMMAND = Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS;
        mUri = Constants.BASE_URL_MM + Constants.URL_TWO_FA_SETTINGS;
        TwoFactorAuthServicesListWithOTPRequest twoFactorAuthServicesListWithOtpRequest = new TwoFactorAuthServicesListWithOTPRequest(null, mChangedList);
        mJsonString = gson.toJson(twoFactorAuthServicesListWithOtpRequest);
        mPutTwoFaSettingsAsyncTask = new HttpRequestPutAsyncTask(API_COMMAND, mUri, mJsonString, getActivity());
        mPutTwoFaSettingsAsyncTask.mHttpResponseListener = this;
        mPutTwoFaSettingsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class Implement2FaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int IMPLEMENT_2FA_HEADER_VIEW = 1;
        private static final int IMPLEMENT_2FA_ITEM_VIEW = 2;
        private static final int FOOTER_VIEW = 3;

        private List<TwoFactorAuthServiceGroup> mTwoFactorAuthServiceGroupList;
        private List<Integer> headerPositionList;
        private int footerPosition;
        private int itemCount;

        public Implement2FaAdapter(List<TwoFactorAuthServiceGroup> twoFactorAuthServiceGroupList) {
            this.mTwoFactorAuthServiceGroupList = twoFactorAuthServiceGroupList;
            this.headerPositionList = new ArrayList<>();
            footerPosition = 0;

            itemCount = 0;
            headerPositionList = new ArrayList<>();
            if (this.mTwoFactorAuthServiceGroupList != null) {
                for (TwoFactorAuthServiceGroup twoFactorAuthServiceGroup : this.mTwoFactorAuthServiceGroupList) {
                    headerPositionList.add(itemCount);
                    itemCount++;
                    if (twoFactorAuthServiceGroup != null && twoFactorAuthServiceGroup.getServices() != null) {
                        itemCount += twoFactorAuthServiceGroup.getServices().size();
                    }
                }
            }
            footerPosition = itemCount;
            itemCount++;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mHeaderTextView;
            private TextView mDescriptionTextView;
            private Switch mSwitch;
            private Button mButtonSave;
            private View mMargin;
            private int twoFaSettingsGroupIndex = 0;
            private int selectedHeaderPosition = 0;
            private int desiredPositon = 0;

            public ViewHolder(final View itemView) {
                super(itemView);
                mHeaderTextView = (TextView) itemView.findViewById(R.id.header_text_view);
                mDescriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
                mSwitch = (Switch) itemView.findViewById(R.id.switch_list_item);
                mButtonSave = (Button) itemView.findViewById(R.id.button_footer_save);
                mMargin = itemView.findViewById(R.id.divider_below);
            }

            public void bindViewListItem(int position) {

                for (int i = 0; i < headerPositionList.size(); i++) {
                    if (position > headerPositionList.get(i)) {
                        if (i < headerPositionList.size() - 1) {
                            if (position < headerPositionList.get(i + 1)) {
                                twoFaSettingsGroupIndex = i;
                                selectedHeaderPosition = headerPositionList.get(i);
                                break;
                            }
                        } else if (i == headerPositionList.size() - 1) {
                            twoFaSettingsGroupIndex = i;
                            selectedHeaderPosition = headerPositionList.get(i);
                            break;
                        }
                    }
                }
                final TwoFactorAuthService twoFactorAuthService = mTwoFactorAuthServiceGroupList.get(twoFaSettingsGroupIndex).getServices().get(position - selectedHeaderPosition - 1);
                desiredPositon = position - selectedHeaderPosition - 1;
                mDescriptionTextView.setText(twoFactorAuthService.getServiceName());
                mSwitch.setChecked(twoFactorAuthService.getIsEnabled());

                mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mTwoFactorAuthServiceGroupList.get(twoFaSettingsGroupIndex).getServices().get(desiredPositon).setIsEnabled(mSwitch.isChecked());
                    }
                });
            }

            public void bindHeader(int position) {
                mHeaderTextView.setText(mTwoFactorAuthServiceGroupList.get(headerPositionList.indexOf(position)).getGroupName());
            }

            public void bindFooter() {
                mButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mChangedList.clear();
                        getChangedSettings();
                        if (mChangedSettings != mCurrentSettings) {
                            for (int i = 0; i < mTotalServices; i++) {
                                if ((check(mChangedSettings, i) && !check(mCurrentSettings, i)) || !check(mChangedSettings, i) && check(mCurrentSettings, i)) {
                                    mChangedList.add(mPositionToServiceIDMap.get(i));
                                }
                            }
                            attemptSaveTwoFaSettings(mChangedList);
                        }
                    }
                });
            }
        }

        public class ServiceListViewHolder extends ViewHolder {
            public ServiceListViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ServiceHeaderViewHolder extends ViewHolder {
            public ServiceHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ServiceFooterViewHolder extends ViewHolder {
            public ServiceFooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case IMPLEMENT_2FA_HEADER_VIEW:
                    return new ServiceHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header_2fa, parent, false));
                case IMPLEMENT_2FA_ITEM_VIEW:
                    return new ServiceListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_2fa, parent, false));
                case FOOTER_VIEW:
                    return new ServiceFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_button_two_fa_footer, parent, false));
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ServiceHeaderViewHolder) {
                ServiceHeaderViewHolder vh = (ServiceHeaderViewHolder) holder;
                vh.bindHeader(position);
            } else if (holder instanceof ServiceListViewHolder) {
                ServiceListViewHolder vh = (ServiceListViewHolder) holder;
                vh.bindViewListItem(position);
            } else if (holder instanceof ServiceFooterViewHolder) {
                ServiceFooterViewHolder vh = (ServiceFooterViewHolder) holder;
                vh.bindFooter();
            }
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            int headerPositionIndex = headerPositionList.indexOf(position);
            if (headerPositionIndex != -1) {
                return IMPLEMENT_2FA_HEADER_VIEW;
            } else if (position == footerPosition) {
                return FOOTER_VIEW;
            } else {
                return IMPLEMENT_2FA_ITEM_VIEW;
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        Gson gson = new Gson();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetTwoFaSettingsAsynctask = null;
            if (getActivity() != null)
                return;
        } else {
            if (result.getApiCommand().equals(Constants.COMMAND_GET_TWO_FACTOR_AUTH_SETTINGS)) {
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            mTwoFaServiceResponse = gson.fromJson(result.getJsonString(), TwoFactorAuthServicesListResponse.class);
                            mTwoFaServiceList = mTwoFaServiceResponse.getResponse();
                            Implement2FaAdapter adapter = new Implement2FaAdapter(mTwoFaServiceList);
                            mRecyclerView.setAdapter(adapter);
                            setCurrentSettings();
                        }
                    } else {
                        if (getActivity() != null) {
                            mProgressDialog.dismiss();
                            Toaster.makeText(getActivity(), mTwoFaServiceResponse.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                } catch (Exception e) {
                    Toaster.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
                }
                mGetTwoFaSettingsAsynctask = null;
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            } else if (result.getApiCommand().equals(Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS)) {
                TwoFactorAuthSettingsSaveResponse twoFactorAuthSettingsSaveResponse = gson.fromJson(result.getJsonString(), TwoFactorAuthSettingsSaveResponse.class);
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        if (getActivity() != null) {
                            SecuritySettingsActivity.otpDuration = twoFactorAuthSettingsSaveResponse.getOtpValidFor();
                            Toaster.makeText(getActivity(), twoFactorAuthSettingsSaveResponse.getMessage(), Toast.LENGTH_LONG);
                            mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), mJsonString,
                                    Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS, mUri, Constants.METHOD_PUT);
                            mOTPVerificationForTwoFactorAuthenticationServicesDialog.mDismissListener = this;
                        }
                    } else {
                        Toaster.makeText(getActivity(), twoFactorAuthSettingsSaveResponse.getMessage(), Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Toaster.makeText(getActivity(), twoFactorAuthSettingsSaveResponse.getMessage(), Toast.LENGTH_LONG);
                }
                mPutTwoFaSettingsAsyncTask = null;
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onDismissDialog() {
        getTwoFaSettings();
    }
}
