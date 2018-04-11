package bd.com.ipay.ipayskeleton.EducationFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.EducationPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.EventParticipant;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.GetSessionRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.GetStudentInfoByStudentIDRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.Institution;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.SemesterOrSession;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SelectInstitutionFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetAllInstitutionsTask = null;
    private HttpRequestGetAsyncTask mGetSessionsByInstitutionTask = null;
    private HttpRequestGetAsyncTask mGetStudentInfoByStudentIDTask = null;

    private Institution[] mInstitutions;
    private SemesterOrSession[] mSemesterOrSessions;
    private EventParticipant mStudent;

    private ResourceSelectorDialog institutionsSelectorDialog;
    private ResourceSelectorDialog sessionsSelectorDialog;
    private int mSelectedSessionId = -1;
    private String mSelectedSessionName = "";
    private int mSelectedInstitutionId = -1;
    private String mSelectedInstitutionName = "";
    private EditText institutionSelection;

    private EditText sessionSelection;
    private EditText studentIDEditText;
    private ProgressDialog mProgressDialog;
    private Button nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_education_select_institutions, container, false);
        institutionSelection = (EditText) v.findViewById(R.id.institution);
        sessionSelection = (EditText) v.findViewById(R.id.sessions);
        studentIDEditText = (EditText) v.findViewById(R.id.student_id);
        nextButton = (Button) v.findViewById(R.id.button_next);

        mProgressDialog = new ProgressDialog(getActivity());
        nextButton = (Button) v.findViewById(R.id.button_next);
        mSelectedInstitutionId = -1;

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs())
                    getStudentInfoByStudentID();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        getAllInstitutions();
    }

    private boolean validateInputs() {
        if (mSelectedInstitutionId == -1) {
            institutionSelection.setError(getString(R.string.please_select_institution));
            return false;
        }
        if (mSelectedSessionId == -1) {
            sessionSelection.setError(getString(R.string.please_select_session));
            return false;
        }
        if (studentIDEditText.getText().toString().trim().length() == 0) {
            studentIDEditText.setError(getString(R.string.please_enter_student_id));
            studentIDEditText.requestFocus();
            return false;
        }

        Utilities.hideKeyboard(getContext(), studentIDEditText.getRootView());
        EducationPaymentActivity.institutionID = mSelectedInstitutionId;
        EducationPaymentActivity.institutionName = mSelectedInstitutionName;
        EducationPaymentActivity.sessionID = mSelectedSessionId;
        EducationPaymentActivity.sessionName = mSelectedSessionName;
        EducationPaymentActivity.studentID = studentIDEditText.getText().toString().trim();

        for (Institution institution : mInstitutions) {
            if (institution.getId() == mSelectedInstitutionId) {
                EducationPaymentActivity.selectedInstitution = institution;
                break;
            }
        }

        for (SemesterOrSession session : mSemesterOrSessions) {
            if (session.getId() == mSelectedSessionId) {
                EducationPaymentActivity.selectedSession = session;
                break;
            }
        }

        return true;
    }

    private void setInstitutionsAdapter() {

        List<Institution> mInstitutionsList = new ArrayList<>();

        for (Institution institution : mInstitutions) {
            mInstitutionsList.add(institution);
        }

        institutionsSelectorDialog = new ResourceSelectorDialog(getActivity(), getString(R.string.select_an_institution), mInstitutionsList);
        institutionsSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                institutionSelection.setError(null);
                institutionSelection.setText(name);
                mSelectedInstitutionId = id;
                mSelectedInstitutionName = name;
                mSelectedSessionId = -1;
                getSessionsByInstituteID(mSelectedInstitutionId);
            }
        });

        institutionSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                institutionsSelectorDialog.show();
            }
        });
    }

    private void setSessionsAdapter() {

        List<SemesterOrSession> mSessionsList = new ArrayList<>();

        for (SemesterOrSession session : mSemesterOrSessions) {
            mSessionsList.add(session);
        }

        sessionsSelectorDialog = new ResourceSelectorDialog(getActivity(), getString(R.string.select_session), mSessionsList);
        sessionsSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                sessionSelection.setError(null);
                sessionSelection.setText(name);
                mSelectedSessionId = id;
                mSelectedSessionName = name;
            }
        });

        sessionSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionsSelectorDialog.show();
            }
        });
    }

    private void getAllInstitutions() {
        if (mGetAllInstitutionsTask != null) {
            return;
        }

        mGetAllInstitutionsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INSTITUTION_LIST,
                Constants.BASE_URL_EDU + Constants.URL_GET_ALL_INSTITUTIONS_LIST, getActivity(), this);
        mGetAllInstitutionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.education_not_available)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void getSessionsByInstituteID(long instituteID) {
        if (mGetSessionsByInstitutionTask != null) {
            return;
        }

        // Reset the session selection spinner
        mSelectedSessionId = -1;
        sessionSelection.setText("");

        GetSessionRequestBuilder mGetSessionRequestBuilder = new GetSessionRequestBuilder(instituteID);
        String mUrl = mGetSessionRequestBuilder.getGeneratedUrl();

        mGetSessionsByInstitutionTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SESSION_LIST,
                mUrl, getActivity());
        mGetSessionsByInstitutionTask.mHttpResponseListener = this;

        mGetSessionsByInstitutionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStudentInfoByStudentID() {
        if (mGetStudentInfoByStudentIDTask != null) {
            return;
        }

        GetStudentInfoByStudentIDRequestBuilder mGetStudentInfoByStudentIDRequestBuilder = new GetStudentInfoByStudentIDRequestBuilder
                (EducationPaymentActivity.institutionID, EducationPaymentActivity.studentID);
        String mUrl = mGetStudentInfoByStudentIDRequestBuilder.getGeneratedUrl();

        mGetStudentInfoByStudentIDTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_STUDENT_INFO_BY_STUDENT_ID,
                mUrl, getActivity());
        mGetStudentInfoByStudentIDTask.mHttpResponseListener = this;

        mGetStudentInfoByStudentIDTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();

        if (HttpErrorHandler.isErrorFound(result,getContext(),mProgressDialog)) {
            mGetAllInstitutionsTask = null;
            mGetSessionsByInstitutionTask = null;
            mGetStudentInfoByStudentIDTask = null;
            return;
        }

        Gson gson = new Gson();
        if (this.isAdded()) setContentShown(true);

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_INSTITUTION_LIST:

                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        try {
                            mInstitutions = gson.fromJson(result.getJsonString(), Institution[].class);
                            setInstitutionsAdapter();

                        } catch (Exception e) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                            e.printStackTrace();
                        }
                    } else {
                        showErrorDialog();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.get_all_institution_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.get_all_institution_failed, Toast.LENGTH_SHORT).show();
                }

                mGetAllInstitutionsTask = null;

                break;
            case Constants.COMMAND_GET_SESSION_LIST:

                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        try {
                            mSemesterOrSessions = gson.fromJson(result.getJsonString(), SemesterOrSession[].class);
                            setSessionsAdapter();
                        } catch (Exception e) {
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), R.string.get_sessions_failed, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.get_sessions_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.get_sessions_failed, Toast.LENGTH_SHORT).show();
                }

                mGetSessionsByInstitutionTask = null;

                break;
            case Constants.COMMAND_GET_STUDENT_INFO_BY_STUDENT_ID:

                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        try {
                            mStudent = gson.fromJson(result.getJsonString(), EventParticipant.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(EducationPaymentActivity.STUDENT_NAME, mStudent.getParticipantName());
                            bundle.putString(EducationPaymentActivity.STUDENT_MOBILE_NUMBER, mStudent.getParticipantMobileNumber());
                            bundle.putString(EducationPaymentActivity.STUDENT_DEPARTMENT, mStudent.getDepartmentName());
                            EducationPaymentActivity.selectedStudent = mStudent;

                            ((EducationPaymentActivity) getActivity()).switchToStudentInfoFragment(bundle);
                        } catch (Exception e) {
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), R.string.get_student_failed, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.get_student_not_found, Toast.LENGTH_SHORT).show();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.get_student_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.get_student_failed, Toast.LENGTH_SHORT).show();
                }

                mGetStudentInfoByStudentIDTask = null;
                break;
        }
    }

}
