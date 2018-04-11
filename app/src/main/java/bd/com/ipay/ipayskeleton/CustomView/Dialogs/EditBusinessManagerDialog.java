
package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.CreateEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.UpdateEmployeeRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessRole;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditBusinessManagerDialog extends AlertDialog implements HttpResponseListener{

    private Context context;
    private OnResourceSelectedListener onResourceSelectedListener;
    private LayoutInflater inflater;
    private View mSelectImageHeaderView;


    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private TextView mManagerNameTextView;
    private EditText mRoleEditText;
    private ProfileImageView mProfilePicImageView;
    private String mSelectedRoleName;
    private long mSelectedRoleID = -1;
    private long mManagerID;

    private Button mUpdateButton;
    private Button mCancelButton;
    private TextView mSelectImageHeaderTitle;

    private long mAssociationId;

    private ProgressDialog mProgressDialog;
    private ResourceSelectorDialog mRolesSelectorDialog;
    private HashMap<String, Integer> mIDtoRoleNameMap;
    private ArrayList<BusinessRole> mAllRolesList;

    private HttpRequestPutAsyncTask mEditEmployeeAsyncTask;

    public EditBusinessManagerDialog(Context context, String mTitle, String managerName, long managerId, String roleName, String pictureUrl) {
        super(context);
        this.context = context;

        this.mManagerID = managerId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_edit_business_manager, null);
        mProfilePicImageView = (ProfileImageView) view.findViewById(R.id.profile_picture);
        mManagerNameTextView = (TextView) view.findViewById(R.id.manager_name_text_view);
        mRoleEditText = (EditText) view.findViewById(R.id.role);
        mUpdateButton = (Button) view.findViewById(R.id.button_update);
        mCancelButton = (Button) view.findViewById(R.id.cancel_button);
        mSelectImageHeaderView = inflater.inflate(R.layout.dialog_selector_header, null);
        mSelectImageHeaderTitle = (TextView) mSelectImageHeaderView.findViewById(R.id.textviewTitle);
        mSelectImageHeaderTitle.setText(mTitle);

        mRoleEditText.setFocusable(false);
        mRoleEditText.setClickable(true);

        mManagerNameTextView.setText(managerName);
        mProfilePicImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + pictureUrl, false);
        mRoleEditText.setText(roleName);

        mProgressDialog = new ProgressDialog(context);

        this.setCustomTitle(mSelectImageHeaderView);
        this.setView(view);
        mIDtoRoleNameMap = new HashMap<>();
        createRoleNameToIDMap();
        setRolesSelectorAdapter(mAllRolesList);
        setButtonActions();
    }

    private void setButtonActions() {
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiding the keyboard after verifying OTP
                System.out.println("Result ");
                Utilities.hideKeyboard(context, v);
                if (Utilities.isConnectionAvailable(context)) verifyInput();
                else if (context != null)
                    Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditBusinessManagerDialog.this.dismiss();
            }
        });
    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        mProgressDialog.dismiss();

        if (HttpErrorHandler.isErrorFound(result,getContext(),mProgressDialog)) {
            mEditEmployeeAsyncTask = null;
            return;
        }

        Gson gson = new Gson();
        switch (result.getApiCommand()) {
            case Constants.COMMAND_CREATE_EMPLOYEE:
                try {
                    CreateEmployeeResponse createEmployeeResponse = gson.fromJson(result.getJsonString(), CreateEmployeeResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (context != null) {
                            Toaster.makeText(context, createEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                            ((ManagePeopleActivity) context).switchToEmployeeManagementFragment();
                            this.dismiss();
                        }
                    } else {
                        if (context != null) {
                            Toaster.makeText(context, createEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                } catch (Exception e) {
                    if (context != null)
                        Toaster.makeText(context, R.string.new_employee_creation_failed, Toast.LENGTH_LONG);
                }

                mEditEmployeeAsyncTask = null;
                break;
        }

    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(int id, String name);
    }


    private void setRolesSelectorAdapter(ArrayList<BusinessRole> rolesList) {
        mRolesSelectorDialog = new ResourceSelectorDialog(context, context.getString(R.string.select_a_role), rolesList);
        mRolesSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mRoleEditText.setText(name);
                mSelectedRoleName = name;
                mSelectedRoleID = mIDtoRoleNameMap.get(name);

            }
        });

        mRoleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRolesSelectorDialog.show();
            }
        });
    }

    private void createRoleNameToIDMap() {
        mAllRolesList = new ArrayList<>();
        mAllRolesList = ((ManagePeopleActivity) (context)).mAllRoleList;
        for (BusinessRole roles : mAllRolesList)
            mIDtoRoleNameMap.put(roles.getName(), roles.getId());
    }

    private void verifyInput() {
        boolean cancel = false;
        View focusView = null;

        if (mSelectedRoleID == -1) {
            cancel = true;
        }

        if (cancel) {
            this.dismiss();
        } else {
            updateEmployee();
        }
    }


    private void updateEmployee() {
        if (mEditEmployeeAsyncTask != null)
            return;

        mProgressDialog.setMessage("Updating employee role...");
        mProgressDialog.show();

        UpdateEmployeeRequest createEmployeeRequest = new UpdateEmployeeRequest(mManagerID, mSelectedRoleID);
        Gson gson = new Gson();
        String json = gson.toJson(createEmployeeRequest);

        mEditEmployeeAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CREATE_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_LIST, json, context, this);
        mEditEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}