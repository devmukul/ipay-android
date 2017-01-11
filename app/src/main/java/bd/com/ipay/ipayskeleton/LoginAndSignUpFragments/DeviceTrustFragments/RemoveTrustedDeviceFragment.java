package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.DeviceTrustFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.DeviceTrustActivity;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.GetTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.RemoveTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.TrustedDevice;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;

public class RemoveTrustedDeviceFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTrustedDeviceTask = null;
    private GetTrustedDeviceResponse mGetTrustedDeviceResponse = null;

    private HttpRequestDeleteAsyncTask mRemoveTrustedDeviceTask = null;
    private RemoveTrustedDeviceResponse mRemoveTrustedDeviceResponse = null;

    private HttpRequestPostAsyncTask mAddTrustedDeviceTask = null;
    private AddToTrustedDeviceResponse mAddToTrustedDeviceResponse;

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    private ArrayList<TrustedDevice> mTrustedDeviceList;
    private TrustedDeviceAdapter mTrustedDeviceAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mTrustedDevicesRecylerView;

    private ProgressDialog mProgressDialog;
    private Button mLogOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remove_trusted_device, container, false);
        setTitle();

        // TODO: The code was copied from TrustedDeviceFragment. We need to change there too.
        mTrustedDevicesRecylerView = (RecyclerView) v.findViewById(R.id.list_trusted_devices);
        mProgressDialog = new ProgressDialog(getActivity());
        mLogOutButton = (Button) v.findViewById(R.id.button_logout);

        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogout();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);

        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE))
            getTrustedDeviceList();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE);

            if (json == null)
                getTrustedDeviceList();
            else {
                processTrustedDeviceList(json);
            }
        }
    }

    private void showDeviceRemoveConfirmationDialog(final long id) {
        MaterialDialog.Builder mRemoveConfirmationDialogBuilder;
        MaterialDialog mRemoveConfirmationDialog;
        mRemoveConfirmationDialogBuilder = new MaterialDialog.Builder(getActivity())
                .content(getString(R.string.confirmation_remove_trusted_device))
                .cancelable(true)
                .positiveText(R.string.remove)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeTrustedDevice(id);
                    }
                });

        mRemoveConfirmationDialog = mRemoveConfirmationDialogBuilder.build();
        mRemoveConfirmationDialog.show();
    }

    private void getTrustedDeviceList() {
        if (mGetTrustedDeviceTask != null) {
            return;
        }

        mGetTrustedDeviceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRUSTED_DEVICES,
                Constants.BASE_URL_MM + Constants.URL_GET_TRUSTED_DEVICES, getActivity(), this);
        mGetTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeTrustedDevice(long id) {
        if (mRemoveTrustedDeviceTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.remove_trusted_device_message));
        mProgressDialog.show();

        mRemoveTrustedDeviceTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_REMOVE_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_TRUSTED_DEVICE + id, getActivity(), this);
        mRemoveTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setTitle() {
        getActivity().setTitle(R.string.browsers_and_apps);
    }

    private void attemptAddTrustedDevice() {
        if (mAddTrustedDeviceTask != null) {
            return;
        }

        String mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());
        String mDeviceName = DeviceInfoFactory.getDeviceName();

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        String pushRegistrationID = pref.getString(Constants.PUSH_NOTIFICATION_TOKEN, null);

        AddToTrustedDeviceRequest mAddToTrustedDeviceRequest = new AddToTrustedDeviceRequest(mDeviceName,
                Constants.MOBILE_ANDROID + mDeviceID, pushRegistrationID);
        Gson gson = new Gson();
        String json = gson.toJson(mAddToTrustedDeviceRequest);
        mAddTrustedDeviceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_ADD_TRUSTED_DEVICE, json, getActivity());
        mAddTrustedDeviceTask.mHttpResponseListener = this;
        mAddTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_signing_out));
        mProgressDialog.show();
        LogoutRequest mLogoutModel = new LogoutRequest(ProfileInfoCacheManager.getMobileNumber());
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, getActivity());
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetTrustedDeviceTask = null;
            mAddTrustedDeviceTask = null;
            mLogoutTask = null;
            mRemoveTrustedDeviceTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRUSTED_DEVICES)) {

            try {
                mGetTrustedDeviceResponse = gson.fromJson(result.getJsonString(), GetTrustedDeviceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    processTrustedDeviceList(result.getJsonString());

                    DataHelper dataHelper = DataHelper.getInstance(getActivity());
                    dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE, result.getJsonString());

                    PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE, false);
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mGetTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), mGetTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mGetTrustedDeviceTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_TRUSTED_DEVICE)) {

            try {
                mRemoveTrustedDeviceResponse = gson.fromJson(result.getJsonString(), RemoveTrustedDeviceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.success_device_removed, Toast.LENGTH_LONG).show();
                    }

                    mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_trusted_devices));
                    mProgressDialog.show();

                    // Add the device as trusted immediately after removing any device
                    attemptAddTrustedDevice();
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mRemoveTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), mRemoveTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.cancel();
            mRemoveTrustedDeviceTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_TRUSTED_DEVICE)) {
            try {
                mAddToTrustedDeviceResponse = gson.fromJson(result.getJsonString(), AddToTrustedDeviceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String UUID = mAddToTrustedDeviceResponse.getUUID();
                    SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                    pref.edit().putString(Constants.UUID, UUID).apply();

                    // Launch HomeActivity from here on successful trusted device add
                    ((DeviceTrustActivity) getActivity()).switchToHomeActivity();
                } else {
                    Toast.makeText(getActivity(), mAddToTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
            }

            mAddTrustedDeviceTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_LOG_OUT)) {
            try {
                mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK)
                    ((MyApplication) getActivity().getApplication()).launchLoginPage(null);
                else
                    Toast.makeText(getActivity(), mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mLogoutTask = null;
        }
    }

    private void processTrustedDeviceList(String json) {
        Gson gson = new Gson();
        mGetTrustedDeviceResponse = gson.fromJson(json, GetTrustedDeviceResponse.class);
        mTrustedDeviceList = (ArrayList<TrustedDevice>) mGetTrustedDeviceResponse.getDevices();

        mTrustedDeviceAdapter = new TrustedDeviceAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTrustedDevicesRecylerView.setLayoutManager(mLayoutManager);
        mTrustedDevicesRecylerView.setAdapter(mTrustedDeviceAdapter);

        setContentShown(true);
    }

    private class TrustedDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public TrustedDeviceAdapter() {
        }

        public class TrustedDeviceViewHolder extends RecyclerView.ViewHolder {
            private final ImageView deviceImageView;
            private final TextView deviceNameView;
            private final TextView grantTimeView;
            private final ImageButton removeTrustedDeviceButton;


            public TrustedDeviceViewHolder(final View itemView) {
                super(itemView);

                deviceImageView = (ImageView) itemView.findViewById(R.id.trusted_device_imageView);
                deviceNameView = (TextView) itemView.findViewById(R.id.textview_device_name);
                grantTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                removeTrustedDeviceButton = (ImageButton) itemView.findViewById(R.id.remove_trusted_device_button);
            }

            public void bindView(int pos) {

                final TrustedDevice trustedDevice = mTrustedDeviceList.get(pos);

                //Setting the correct image based on trusted device type
                int[] images = {
                        R.drawable.ic_browser3x,
                        R.drawable.ic_android3x,
                };

                final String deviceID = trustedDevice != null ? trustedDevice.getDeviceId() : null;
                String android = getString(R.string.android);
                String ios = getString(R.string.ios);
                String browser = getString(R.string.browser);

                if (deviceID.toLowerCase().contains(android.toLowerCase()))
                    deviceImageView.setImageResource(images[1]);
                else if (deviceID.toLowerCase().contains(ios.toLowerCase()))
                    deviceImageView.setImageResource(images[1]);
                else if (deviceID.toLowerCase().contains(browser.toLowerCase()))
                    deviceImageView.setImageResource(images[0]);

                deviceNameView.setText(trustedDevice.getDeviceName());
                grantTimeView.setText(trustedDevice.getCreatedTimeString());

                removeTrustedDeviceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeviceRemoveConfirmationDialog(trustedDevice.getId());
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trusted_device_remove,
                    parent, false);

            return new TrustedDeviceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                TrustedDeviceViewHolder vh = (TrustedDeviceViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mTrustedDeviceList != null)
                return mTrustedDeviceList.size();
            else return 0;
        }
    }

}
