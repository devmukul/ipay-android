package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.AskForRecommendationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.AskForRecommendationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.SendInviteRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public abstract class BaseContactsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener,
        HttpResponseListener {

    private BottomSheetLayout mBottomSheetLayout;

    protected final int[] COLORS = {
            R.color.background_default,
            R.color.background_blue,
            R.color.background_bright_pink,
            R.color.background_cyan,
            R.color.background_magenta,
            R.color.background_orange,
            R.color.background_red,
            R.color.background_spring_green,
            R.color.background_violet,
            R.color.background_yellow,
            R.color.background_azure
    };

    // When a contact item is clicked, we need to access its name and number from the sheet view.
    // So saving these in these two variables.
    private String mSelectedName;
    private String mSelectedNumber;

    private View mSheetViewNonSubscriber;
    private View mSheetViewSubscriber;
    private View selectedBottomSheetView;

    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;

    private HttpRequestPostAsyncTask mAskForRecommendationTask = null;
    private AskForRecommendationResponse mAskForRecommendationResponse;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contact, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_contacts);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemsVisibility(menu, searchItem, false);
                searchView.requestFocus();
                if (mBottomSheetLayout != null && mBottomSheetLayout.isSheetShowing())
                    mBottomSheetLayout.dismissSheet();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery("", true);
                setItemsVisibility(menu, searchItem, true);
                return false;
            }
        });
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != null && item != exception) item.setVisible(visible);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        mProgressDialog = new ProgressDialog(getActivity());

        if (mBottomSheetLayout != null)
            setUpBottomSheet();

        return v;
    }

    /**
     * Must be called after show(Non)SubscriberSheet
     */
    protected void setContactInformationInSheet(String contactName, String contactNumber,
                                                String imageUrl, final int backgroundColor) {
        if (selectedBottomSheetView == null)
            return;

        final TextView contactNameView = (TextView) selectedBottomSheetView.findViewById(R.id.textview_contact_name);
        final ImageView contactImage = (ImageView) selectedBottomSheetView.findViewById(R.id.image_contact);

        contactImage.setBackgroundResource(backgroundColor);
        contactNameView.setText(contactName);

        if (imageUrl != null && !imageUrl.equals("")) {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            setPlaceHolderImage(contactImage, backgroundColor);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(contactImage);
        } else {
            contactImage.setBackgroundResource(backgroundColor);
            setPlaceHolderImage(contactImage, backgroundColor);
        }
    }

    protected void setPlaceHolderImage(ImageView contactImage, int backgroundColor) {
        contactImage.setBackgroundResource(backgroundColor);
        Glide.with(getActivity())
                .load(R.drawable.people)
                .fitCenter()
                .into(contactImage);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    public void setBottomSheetLayout(BottomSheetLayout bottomSheetLayout) {
        this.mBottomSheetLayout = bottomSheetLayout;
    }

    private void setUpBottomSheet() {
        mSheetViewNonSubscriber = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_non_subscriber, null);
        Button mInviteButton = (Button) mSheetViewNonSubscriber.findViewById(R.id.button_invite);
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }

                sendInvite(mSelectedNumber);
            }
        });

        mSheetViewSubscriber = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_subscriber, null);

        Button mSendMoneyButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_send_money);
        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                intent.putExtra(Constants.MOBILE_NUMBER, mSelectedNumber);
                startActivity(intent);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        Button mRequestMoneyButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_request_money);
        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestMoneyActivity.class);
                intent.putExtra(Constants.MOBILE_NUMBER, mSelectedNumber);
                intent.putExtra(RequestMoneyActivity.LAUNCH_NEW_REQUEST, true);
                startActivity(intent);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        Button mAskForRecommendationButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_ask_for_introduction);
        mAskForRecommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecommendationRequest(mSelectedNumber);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });
    }

    protected void sendRecommendationRequest(String mobileNumber) {
        if (mAskForRecommendationTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_send_for_recommendation));
        mProgressDialog.show();
        AskForRecommendationRequest mAskForRecommendationRequest =
                new AskForRecommendationRequest(mobileNumber);
        Gson gson = new Gson();
        String json = gson.toJson(mAskForRecommendationRequest);
        mAskForRecommendationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ASK_FOR_RECOMMENDATION,
                Constants.BASE_URL_POST_MM + Constants.URL_ASK_FOR_RECOMMENDATION, json, getActivity());
        mAskForRecommendationTask.mHttpResponseListener = this;
        mAskForRecommendationTask.execute((Void) null);
    }

    private void sendInvite(String phoneNumber) {
        if (ContactsHolderFragment.mGetInviteInfoResponse == null || ContactsHolderFragment.mGetInviteInfoResponse.invitees == null) {
            Toast.makeText(getActivity(), R.string.failed_sending_invitation,
                    Toast.LENGTH_LONG).show();
            return;
        }

        int numberOfInvitees = ContactsHolderFragment.mGetInviteInfoResponse.invitees.size();
        if (numberOfInvitees >= ContactsHolderFragment.mGetInviteInfoResponse.totalLimit) {
            Toast.makeText(getActivity(), R.string.invitaiton_limit_exceeded,
                    Toast.LENGTH_LONG).show();
        } else if (ContactsHolderFragment.mGetInviteInfoResponse.invitees.contains(phoneNumber)) {
            Toast.makeText(getActivity(), R.string.invitation_already_sent,
                    Toast.LENGTH_LONG).show();
        } else {
            mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_sending_invite));
            mProgressDialog.show();

            SendInviteRequest sendInviteRequest = new SendInviteRequest(phoneNumber);
            Gson gson = new Gson();
            String json = gson.toJson(sendInviteRequest);
            mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                    Constants.BASE_URL_POST_MM + Constants.URL_SEND_INVITE, json, getActivity(), this);
            mSendInviteTask.execute();
        }
    }


    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mSendInviteTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();

            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SEND_INVITE)) {
            try {
                if (resultList.size() > 2) {
                    mSendInviteResponse = gson.fromJson(resultList.get(2), SendInviteResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.invitation_sent, Toast.LENGTH_LONG).show();
                        }

                        ContactsHolderFragment.mGetInviteInfoResponse.invitees.add(mSelectedNumber);

                    } else if (getActivity() != null) {
                        Toast.makeText(getActivity(), mSendInviteResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mSendInviteTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ASK_FOR_RECOMMENDATION)) {
            try {

                if (resultList.size() > 2) {
                    mAskForRecommendationResponse = gson.fromJson(resultList.get(2), AskForRecommendationResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.ask_for_recommendation_sent, Toast.LENGTH_LONG).show();
                        }
                    } else if (getActivity() != null) {
                        Toast.makeText(getActivity(), mAskForRecommendationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_asking_recommendation, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_asking_recommendation, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mAskForRecommendationTask = null;
        }
    }

    protected void showSubscriberSheet(int verificationStatus) {
        if (mBottomSheetLayout == null)
            return;

        selectedBottomSheetView = mSheetViewSubscriber;

        // TODO: Show a green tick for the verified users
        Button askForConfirmationButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_ask_for_introduction);
        if (verificationStatus == DBConstants.NOT_VERIFIED_USER) {
            if (askForConfirmationButton != null) askForConfirmationButton.setVisibility(View.GONE);

        } else if (askForConfirmationButton != null)
            askForConfirmationButton.setVisibility(View.VISIBLE);

        mBottomSheetLayout.showWithSheetView(mSheetViewSubscriber);
        mBottomSheetLayout.expandSheet();
    }

    protected void showNonSubscriberSheet() {
        if (mBottomSheetLayout == null)
            return;

        selectedBottomSheetView = mSheetViewNonSubscriber;
        mBottomSheetLayout.showWithSheetView(mSheetViewNonSubscriber);
        mBottomSheetLayout.expandSheet();
    }

    protected void setSelectedName(String name) {
        this.mSelectedName = name;
    }

    protected void setSelectedNumber(String contactNumber) {
        this.mSelectedNumber = contactNumber;
    }
}
