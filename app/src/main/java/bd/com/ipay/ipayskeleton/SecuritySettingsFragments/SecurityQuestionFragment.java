package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.Security.GetSecurityQuestionRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.Security.GetSecurityQuestionResponse;
import bd.com.ipay.ipayskeleton.Model.Security.SecurityAnswerClass;
import bd.com.ipay.ipayskeleton.Model.Security.SecurityQuestionClass;
import bd.com.ipay.ipayskeleton.Model.Security.SecurityQuestionValidationClass;
import bd.com.ipay.ipayskeleton.Model.Security.SetSecurityAnswerRequest;
import bd.com.ipay.ipayskeleton.Model.Security.SetSecurityAnswerResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SecurityQuestionFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mgetQuesTask = null;
    private GetSecurityQuestionResponse mSecurityQuestionResponse;

    private HttpRequestPostAsyncTask mSetSecurityAnswerTask = null;
    private SetSecurityAnswerResponse mSetSecurityAnswerResponse;

    private ProgressDialog mProgressDialog;

    private RecyclerView mSecurityQuesRecyclerView;
    private SecurityQuestionAdapter mSecurityQuesLogAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<SecurityQuestionClass> mSecurityQuestionsList;
    private List<SecurityAnswerClass> mSecurityAnswerClassList;
    private List<SecurityQuestionValidationClass> mSecurityQuestionAnswerValidationClassList;

    private List<ResourceSelectorDialog<SecurityQuestionClass>> questionClassResourceSelectorDialog;

    private TextView mEmptyListTextView;

    private int mRequiredQuestions;
    private String mPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_security_question, container, false);

        setTitle();
        mSecurityQuesRecyclerView = (RecyclerView) v.findViewById(R.id.list_security_questions);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

        mSecurityQuesLogAdapter = new SecurityQuestionAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSecurityQuesRecyclerView.setLayoutManager(mLayoutManager);
        mSecurityQuesRecyclerView.setAdapter(mSecurityQuesLogAdapter);

        mProgressDialog = new ProgressDialog(getActivity());

        if (Utilities.isConnectionAvailable(getActivity())) {
            getSecurityQuestions();
        }

        return v;
    }

    private void setTitle() {
        getActivity().setTitle(R.string.security_questions);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void getSecurityQuestions() {
        if (mgetQuesTask != null) {
            return;
        }
        GetSecurityQuestionRequestBuilder getSecurityQuestionBuilder = new GetSecurityQuestionRequestBuilder();
        String url = getSecurityQuestionBuilder.getGeneratedUri();
        mgetQuesTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SECURITY_QUESTIONS,
                url, getActivity());
        mgetQuesTask.mHttpResponseListener = this;
        mgetQuesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setSecurityQuestionAnswerList() {
        questionClassResourceSelectorDialog = new ArrayList<>();
        mSecurityQuestionAnswerValidationClassList = new ArrayList<>();
        mSecurityAnswerClassList = new ArrayList<>();

        for (int mIndex = 0; mIndex < mRequiredQuestions; mIndex++) {
            mSecurityQuestionAnswerValidationClassList.add(new SecurityQuestionValidationClass());
            mSecurityAnswerClassList.add(new SecurityAnswerClass());

            questionClassResourceSelectorDialog.add(mIndex, new ResourceSelectorDialog<>(getActivity(),
                    getString(R.string.select_a_question), mSecurityQuestionsList, mIndex, true));

            questionClassResourceSelectorDialog.get(mIndex).
                    setOnResourceSelectedListenerWithSelectedIndex(new ResourceSelectorDialog.OnResourceSelectedListenerWithStringID() {
                        @Override
                        public void onResourceSelectedWithStringID(String id, String question, int mSelectedQuestionId) {
                            mSecurityQuestionAnswerValidationClassList.get(mSelectedQuestionId).setQuestion(question);
                            mSecurityQuestionAnswerValidationClassList.get(mSelectedQuestionId).setQuestion_available(true);

                            mSecurityAnswerClassList.get(mSelectedQuestionId).setQid(id);
                            mSecurityQuesLogAdapter.notifyDataSetChanged();
                        }
                    });
        }

        mSecurityQuesLogAdapter.notifyDataSetChanged();
    }

    private boolean verifyQuestionAnswers() {
        boolean isValid = true;
        for (int mIndex = 0; mIndex < mSecurityQuestionAnswerValidationClassList.size(); mIndex++) {
            if (mSecurityQuestionAnswerValidationClassList.get(mIndex).getQuestion() == null) {
                isValid = false;
                mSecurityQuestionAnswerValidationClassList.get(mIndex).setQuestion_available(false);

            }
            if (mSecurityAnswerClassList.get(mIndex).getAnswer() == null) {
                isValid = false;
                mSecurityQuestionAnswerValidationClassList.get(mIndex).setAnswer_available(false);
            }
        }
        if (!isValid) {
            mSecurityQuesLogAdapter.notifyDataSetChanged();
        }
        return isValid;

    }

    private void saveSecurityQuestions(String password) {

        mProgressDialog.setMessage(getString(R.string.set_security_answer));
        mProgressDialog.show();

        SetSecurityAnswerRequest setSecurityAnswerRequest = new SetSecurityAnswerRequest(mSecurityAnswerClassList, password);
        Gson gson = new Gson();
        String json = gson.toJson(setSecurityAnswerRequest, SetSecurityAnswerRequest.class);
        mSetSecurityAnswerTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_SECURITY_ANSWERS,
                Constants.BASE_URL_MM + Constants.URL_SET_SECURITY_ANSWERS, json, getActivity());
        mSetSecurityAnswerTask.mHttpResponseListener = this;
        mSetSecurityAnswerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mgetQuesTask = null;
            mSetSecurityAnswerTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.security_question_get_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_SECURITY_QUESTIONS)) {
            if (this.isAdded()) setContentShown(true);
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mSecurityQuestionResponse = gson.fromJson(result.getJsonString(), GetSecurityQuestionResponse.class);
                    mSecurityQuestionsList = mSecurityQuestionResponse.getList();
                    mRequiredQuestions = mSecurityQuestionResponse.getRequired();
                    if (mRequiredQuestions > 0)
                        setSecurityQuestionAnswerList();
                    else mEmptyListTextView.setVisibility(View.VISIBLE);

                    setContentShown(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.security_question_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.security_question_get_failed, Toast.LENGTH_LONG).show();
            }

            mgetQuesTask = null;
        }

        if (result.getApiCommand().equals(Constants.COMMAND_SET_SECURITY_ANSWERS)) {
            try {
                mSetSecurityAnswerResponse = gson.fromJson(result.getJsonString(), SetSecurityAnswerResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Toast.makeText(getActivity(), mSetSecurityAnswerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(),mSetSecurityAnswerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.security_answer_set_failed, Toast.LENGTH_SHORT).show();
            }

            mSetSecurityAnswerTask = null;
            mProgressDialog.dismiss();
        }

    }

    public class SecurityQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 1;
        private static final int SECURITY_QUESTION_LIST_ITEM_VIEW = 2;
        private static final int FOOTER_VIEW = 3;

        private EditText mPasswordView;
        private Button mSaveButton;


        public class SecurityQuestionHolder extends RecyclerView.ViewHolder {
            private final EditText mQuestionEditText;
            private final EditText mAnswerEditText;
            private CustomWatcher customWatcher;


            public SecurityQuestionHolder(final View itemView, CustomWatcher customWatcher) {
                super(itemView);

                mQuestionEditText = (EditText) itemView.findViewById(R.id.question);
                mAnswerEditText = (EditText) itemView.findViewById(R.id.answer);
                this.customWatcher = customWatcher;
                this.mAnswerEditText.addTextChangedListener(customWatcher);
            }


            public void bindView(final int pos) {

                if (!mSecurityQuestionAnswerValidationClassList.get(pos - 1).isQuestion_available())
                    mQuestionEditText.setError(getString(R.string.enter_question));
                else
                    mQuestionEditText.setError(null);

                if (!mSecurityQuestionAnswerValidationClassList.get(pos - 1).isAnswer_available())
                    mAnswerEditText.setError(getString(R.string.enter_answer));
                else
                    mAnswerEditText.setError(null);

                if (mSecurityQuestionAnswerValidationClassList.get(pos - 1).getQuestion() != null)
                    mQuestionEditText.setText(mSecurityQuestionAnswerValidationClassList.get(pos - 1).getQuestion());
                else
                    mQuestionEditText.setText("");

                if (mSecurityAnswerClassList.get(pos - 1).getAnswer() != null)
                    mAnswerEditText.setText(mSecurityAnswerClassList.get(pos - 1).getAnswer());
                else
                    mAnswerEditText.setText("");

                mQuestionEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        questionClassResourceSelectorDialog.get(pos - 1).show();
                    }
                });
            }

        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {

            public HeaderViewHolder(View itemView) {
                super(itemView);

                mPasswordView = (EditText) itemView.findViewById(R.id.password);
            }

            public void bindView() {
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);

                mSaveButton = (Button) itemView.findViewById(R.id.button_save);
            }

            public void bindView() {

                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPasswordView.getText().toString().isEmpty())
                            mPasswordView.setError(getString(R.string.enter_current_pass));

                        if (verifyQuestionAnswers() && !mPasswordView.getText().toString().isEmpty()) {
                            Utilities.hideKeyboard(getActivity());
                            mPassword = mPasswordView.getText().toString();
                            saveSecurityQuestions(mPassword);
                        }
                    }
                });

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_security_question_footer, parent, false);
                return new SecurityQuestionAdapter.FooterViewHolder(v);
            } else if (viewType == HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_security_question_header, parent, false);
                return new SecurityQuestionAdapter.HeaderViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_security_question, parent, false);
                SecurityQuestionHolder vh = new SecurityQuestionHolder(v, new CustomWatcher());
                return vh;
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof SecurityQuestionAdapter.SecurityQuestionHolder) {
                    SecurityQuestionAdapter.SecurityQuestionHolder vh = (SecurityQuestionAdapter.SecurityQuestionHolder) holder;
                    // So that it knows what item in dataset to update
                    vh.customWatcher.updatePosition(position);
                    vh.bindView(position);
                } else if (holder instanceof SecurityQuestionAdapter.FooterViewHolder) {
                    SecurityQuestionAdapter.FooterViewHolder vh = (SecurityQuestionAdapter.FooterViewHolder) holder;
                    vh.bindView();
                } else if (holder instanceof SecurityQuestionAdapter.HeaderViewHolder) {
                    SecurityQuestionAdapter.HeaderViewHolder vh = (SecurityQuestionAdapter.HeaderViewHolder) holder;
                    vh.bindView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mSecurityQuestionAnswerValidationClassList.size() == 0)
                return 0;
            else
                return mSecurityQuestionAnswerValidationClassList.size() + 2;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return HEADER_VIEW;
            } else if (position == getItemCount() - 1) {
                return FOOTER_VIEW;
            } else {
                return SECURITY_QUESTION_LIST_ITEM_VIEW;
            }
        }


        public class CustomWatcher implements TextWatcher {

            private int position;

            public CustomWatcher() {
            }

            public void updatePosition(int position) {
                this.position = position;
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    mSecurityAnswerClassList.get(position - 1).setAnswer(editable.toString());
                    mSecurityQuestionAnswerValidationClassList.get(position - 1).setAnswer_available(true);
                } else {
                    mSecurityQuestionAnswerValidationClassList.get(position - 1).setAnswer_available(false);
                }

            }
        }
    }


}
