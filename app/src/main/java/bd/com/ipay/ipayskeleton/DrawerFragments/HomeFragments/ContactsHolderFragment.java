package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.AskForRecommendationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.AskForRecommendationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.GetInviteInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.SendInviteRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class ContactsHolderFragment extends Fragment {

    private TextView allContactsTab;
    private TextView iPayContactsTab;

    private Fragment mSelectedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);
        getActivity().setTitle(R.string.contacts);

        allContactsTab = (TextView) v.findViewById(R.id.all_contacts_tab);
        iPayContactsTab = (TextView) v.findViewById(R.id.ipay_contacts_tab);

        allContactsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllContacts();
            }
        });

        iPayContactsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadIPayContacts();
            }
        });

        loadIPayContacts();

        return v;
    }

    public void loadIPayContacts() {
        iPayContactsTab.setBackgroundResource(R.drawable.contacts_tab_selected_background);
        allContactsTab.setBackgroundResource(android.R.color.transparent);
        mSelectedFragment = new IPayContactsFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contact_list_container, mSelectedFragment).commit();
    }

    public void loadAllContacts() {
        allContactsTab.setBackgroundResource(R.drawable.contacts_tab_selected_background);
        iPayContactsTab.setBackgroundResource(android.R.color.transparent);
        mSelectedFragment = new AllContactsFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contact_list_container, mSelectedFragment).commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mSelectedFragment != null)
            mSelectedFragment.setMenuVisibility(false);
    }
}