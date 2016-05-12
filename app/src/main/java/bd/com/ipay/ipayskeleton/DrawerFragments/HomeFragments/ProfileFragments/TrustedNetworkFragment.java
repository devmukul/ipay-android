package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.AddTrustedPersonRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.AddTrustedPersonResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.GetTrustedPersonsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.SetAccountRecoveryPersonRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.SetAccountRecoveryPersonResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.TrustedPerson;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TrustedNetworkFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTrustedPersonsTask = null;
    private GetTrustedPersonsResponse mGetTrustedPersonsResponse = null;

    private HttpRequestPostAsyncTask mAddTrustedPersonTask = null;
    private AddTrustedPersonResponse mAddTrustedPersonResponse = null;

    private HttpRequestPostAsyncTask mSetAccountRecoveryPersonTask = null;
    private SetAccountRecoveryPersonResponse mSetAccountRecoveryPersonResponse = null;


    private List<TrustedPerson> mTrustedPersons;
    private TrustedPersonListAdapter mTrustedPersonListAdapter;

    private Button mAddTrustedPersonButton;
    private RecyclerView mTrustedPersonListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trusted_network, container, false);

        getActivity().setTitle(R.string.trusted_network);

        mAddTrustedPersonButton = (Button) v.findViewById(R.id.button_add_trusted_person);
        mTrustedPersonListRecyclerView = (RecyclerView) v.findViewById(R.id.list_trusted_person);

        mProgressDialog = new ProgressDialog(getActivity());

        mTrustedPersonListAdapter = new TrustedPersonListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTrustedPersonListRecyclerView.setLayoutManager(mLayoutManager);
        mTrustedPersonListRecyclerView.setAdapter(mTrustedPersonListAdapter);

        mAddTrustedPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTrustedPersonDialog();
            }
        });

        loadTrustedPersons();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);
    }

    private void loadTrustedPersons() {
        if (mGetTrustedPersonsTask != null) {
            return;
        }

        mGetTrustedPersonsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRUSTED_PERSONS,
                Constants.BASE_URL + Constants.URL_GET_TRUSTED_PERSONS, getActivity(), this);
        mGetTrustedPersonsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addTrustedPerson(AddTrustedPersonRequest addTrustedPersonRequest) {
        if (mAddTrustedPersonTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_adding_as_trusted_person));
        mProgressDialog.show();

        Gson gson = new Gson();
        String json = gson.toJson(addTrustedPersonRequest);

        mAddTrustedPersonTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_TRUSTED_PERSON,
                Constants.BASE_URL + Constants.URL_POST_TRUSTED_PERSONS, json, getActivity(), this);
        mAddTrustedPersonTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setAccountRecoveryPerson(long id) {
        if (mSetAccountRecoveryPersonTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_adding_as_account_recovery_person));
        mProgressDialog.show();

        SetAccountRecoveryPersonRequest setAccountRecoveryPersonRequest = new SetAccountRecoveryPersonRequest();
        Gson gson = new Gson();
        String json = gson.toJson(setAccountRecoveryPersonRequest);

        mSetAccountRecoveryPersonTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_ACCOUNT_RECOVERY_PERSON,
                Constants.BASE_URL + Constants.URL_POST_TRUSTED_PERSONS + id + Constants.URL_SET_RECOVERY_PERSON,
                json, getActivity(), this);
        mSetAccountRecoveryPersonTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showAddTrustedPersonDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.add_a_trusted_person)
                .customView(R.layout.dialog_add_new_trusted_person, true)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .build();

        View view = dialog.getCustomView();

        final TextView nameView = (TextView) view.findViewById(R.id.edit_text_name);
        final TextView mobileNumberView = (TextView) view.findViewById(R.id.edit_text_mobile_number);
        final Spinner selectRelationshipSpinner = (Spinner) view.findViewById(R.id.spinner_select_relationship);

        ArrayAdapter<CharSequence> relationshipAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.relationship, android.R.layout.simple_spinner_item);
        relationshipAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        selectRelationshipSpinner.setAdapter(relationshipAdapter);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                //hiding keyboard after add pressed in add a trusted person
                Utilities.hideKeyboard(getContext(), nameView);

                String name = nameView.getText().toString();
                String mobileNumber = mobileNumberView.getText().toString();
                String relationShip = (String) selectRelationshipSpinner.getSelectedItem();

                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.error_invalid_name, Toast.LENGTH_LONG).show();
                } else if (!ContactEngine.isValidNumber(mobileNumber)) {
                    Toast.makeText(getActivity(), R.string.error_invalid_mobile_number, Toast.LENGTH_LONG).show();
                } else {

                    AddTrustedPersonRequest addTrustedPersonRequest = new AddTrustedPersonRequest(name,
                            ContactEngine.formatMobileNumberBD(mobileNumber), relationShip.toUpperCase());
                    addTrustedPerson(addTrustedPersonRequest);
                }
            }
        });

        dialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                //hiding keyboard after cancel pressed in add a trusted person
                Utilities.hideKeyboard(getContext(), nameView);
            }
        });

        dialog.show();
    }

    private void showSetRecoveryPersonConfirmationDialog(final long id) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.are_you_sure)
                .setMessage(getString(R.string.confirmation_set_trusted_member))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setAccountRecoveryPerson(id);
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        dialog.show();
    }

    @Override
    public void httpResponseReceiver(String result) {

        mProgressDialog.dismiss();

        if (result == null) {
            mGetTrustedPersonsTask = null;
            mAddTrustedPersonTask = null;
            mSetAccountRecoveryPersonTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_TRUSTED_PERSONS)) {
            try {
                mGetTrustedPersonsResponse = gson.fromJson(resultList.get(2), GetTrustedPersonsResponse.class);

                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    mTrustedPersons = mGetTrustedPersonsResponse.getTrustedPersons();
                    mTrustedPersonListAdapter.notifyDataSetChanged();

                    setContentShown(true);
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mGetTrustedPersonsResponse.getMessage(), Toast.LENGTH_LONG).show();
                    ((HomeActivity) getActivity()).switchToDashBoard();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_loading_trusted_person_list, Toast.LENGTH_LONG).show();
                ((HomeActivity) getActivity()).switchToDashBoard();
            }

            mGetTrustedPersonsTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_ADD_TRUSTED_PERSON)) {
            try {
                mAddTrustedPersonResponse = gson.fromJson(resultList.get(2), AddTrustedPersonResponse.class);

                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mAddTrustedPersonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    loadTrustedPersons();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mAddTrustedPersonResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_adding_trusted_person, Toast.LENGTH_LONG).show();
            }

            mAddTrustedPersonTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_SET_ACCOUNT_RECOVERY_PERSON)) {
            try {
                mSetAccountRecoveryPersonResponse = gson.fromJson(resultList.get(2), SetAccountRecoveryPersonResponse.class);

                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetAccountRecoveryPersonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    loadTrustedPersons();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetAccountRecoveryPersonResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            }

            mSetAccountRecoveryPersonTask = null;
        }
    }


    public class TrustedPersonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public TrustedPersonListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mNameView;
            private TextView mMobileNumberView;
            private TextView mRelationshipView;
            private TextView mIsTrustedMemberView;
            private ImageButton mSetRecoveryPersonButton;

            public ViewHolder(final View itemView) {
                super(itemView);

                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mRelationshipView = (TextView) itemView.findViewById(R.id.textview_relationship);
                mIsTrustedMemberView = (TextView) itemView.findViewById(R.id.textview_is_trusted_member);
                mSetRecoveryPersonButton = (ImageButton) itemView.findViewById(R.id.button_make_recovery_person);
            }

            public void bindView(int pos) {

                final TrustedPerson trustedPerson = mTrustedPersons.get(pos);

                mNameView.setText(trustedPerson.getName());
                mMobileNumberView.setText(trustedPerson.getMobileNumber());
                mRelationshipView.setText(trustedPerson.getRelationship());

                if (trustedPerson.isEligibleForAccountRecovery()) {
                    mIsTrustedMemberView.setVisibility(View.VISIBLE);
                    mSetRecoveryPersonButton.setVisibility(View.GONE);
                } else {
                    mIsTrustedMemberView.setVisibility(View.GONE);
                    mSetRecoveryPersonButton.setVisibility(View.VISIBLE);
                }

                mSetRecoveryPersonButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSetRecoveryPersonConfirmationDialog(trustedPerson.getPersonId());
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trusted_network,
                    parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mTrustedPersons != null)
                return mTrustedPersons.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
