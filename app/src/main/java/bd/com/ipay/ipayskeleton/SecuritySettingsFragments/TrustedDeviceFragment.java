package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.GetTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.RemoveTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.TrustedDevice;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TrustedDeviceFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTrustedDeviceTask = null;
    private GetTrustedDeviceResponse mGetTrustedDeviceResponse = null;

    private HttpRequestDeleteAsyncTask mRemoveTrustedDeviceTask = null;
    private RemoveTrustedDeviceResponse mRemoveTrustedDeviceResponse = null;
    private ListView mTrustedDevicesListView;
    private TrustedDeviceAdapter mTrustedDeviceAdapter;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trusted_devices, container, false);
        setTitle();

        mTrustedDevicesListView = (ListView) v.findViewById(R.id.list_trusted_devices);
        mProgressDialog = new ProgressDialog(getActivity());

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

    private void getTrustedDeviceList() {
        if (mGetTrustedDeviceTask != null) {
            return;
        }

        mGetTrustedDeviceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRUSTED_DEVICES,
                Constants.BASE_URL_MM + Constants.URL_GET_TRUSTED_DEVICES, getActivity(), this);
        mGetTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showTrustedDeviceRemoveConfirmationDialog(final long id, String name) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog
                .setMessage(getString(R.string.confirmation_remove_trusted_device))
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTrustedDevice(id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void removeTrustedDevice(long id) {
        if (mRemoveTrustedDeviceTask != null)
            return;

        mProgressDialog.setMessage("Removing device from your trusted device list");
        mProgressDialog.show();

        mRemoveTrustedDeviceTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_REMOVE_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_TRUSTED_DEVICE + id, getActivity(), this);
        mRemoveTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setTitle() {
        getActivity().setTitle(R.string.trusted_devices);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetTrustedDeviceTask = null;
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

                    getTrustedDeviceList();
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
        }

    }

    private void processTrustedDeviceList(String json) {
        Gson gson = new Gson();
        mGetTrustedDeviceResponse = gson.fromJson(json, GetTrustedDeviceResponse.class);

        ArrayList<TrustedDevice> mTrustedDeviceList = (ArrayList<TrustedDevice>) mGetTrustedDeviceResponse.getDevices();
        mTrustedDeviceAdapter = new TrustedDeviceAdapter(getActivity(), mTrustedDeviceList);
        mTrustedDevicesListView.setAdapter(mTrustedDeviceAdapter);
        Utilities.setUpNonScrollableListView(mTrustedDevicesListView);

        setContentShown(true);
    }

    public class TrustedDeviceAdapter extends ArrayAdapter<TrustedDevice> {

        private LayoutInflater inflater;
        private CustomSelectorDialog mCustomSelectorDialog;
        private List<String> mTrustedDeviceActionList;
        private int ACTION_REMOVE = 0;

        public TrustedDeviceAdapter(Context context, List<TrustedDevice> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TrustedDevice trustedDevice = getItem(position);

            View view = convertView;
            if (view == null)
                view = inflater.inflate(R.layout.list_item_trusted_device, null);


            RelativeLayout layout_item_view = (RelativeLayout) view.findViewById(R.id.layout_list_item_trusted_device);
            ImageView deviceImageView = (ImageView) view.findViewById(R.id.trusted_device_imageView);
            TextView deviceNameView = (TextView) view.findViewById(R.id.textview_device_name);
            TextView grantTimeView = (TextView) view.findViewById(R.id.textview_time);

            //Setting the correct image based on trusted device type
            int[] images = {
                    R.drawable.ic_browser3x,
                    R.drawable.ic_android3x,
                    R.drawable.ic_ios3x
            };

            String deviceID = trustedDevice.getDeviceId();
            String Android = "android";
            String IOS = "ios";
            String Computer = "browser";
            if (deviceID.toLowerCase().contains(Android.toLowerCase())) {
                deviceImageView.setImageResource(images[1]);

            } else if (deviceID.toLowerCase().contains(IOS.toLowerCase())) {
                deviceImageView.setImageResource(images[2]);

            } else if (deviceID.toLowerCase().contains(Computer.toLowerCase())) {
                deviceImageView.setImageResource(images[0]);

            }

            String myDeviceID = "mobile-android-";
            myDeviceID = myDeviceID.concat(DeviceInfoFactory.getDeviceId(getActivity()));

            if (myDeviceID.equals(deviceID)) {
                deviceNameView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            deviceNameView.setText(trustedDevice.getDeviceName());
            grantTimeView.setText(trustedDevice.getCreatedTimeString());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTrustedDeviceActionList = Arrays.asList(getResources().getStringArray(R.array.trusted_device_action));
                    mCustomSelectorDialog = new CustomSelectorDialog(getActivity(), trustedDevice.getDeviceName(), mTrustedDeviceActionList);
                    mCustomSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                        @Override
                        public void onResourceSelected(int selectedIndex, String mName) {
                            if (selectedIndex == ACTION_REMOVE) {
                                showTrustedDeviceRemoveConfirmationDialog(
                                        trustedDevice.getId(), trustedDevice.getDeviceName());
                            }
                        }
                    });
                    mCustomSelectorDialog.show();

                }
            });

            return view;
        }
    }

}
