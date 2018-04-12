package bd.com.ipay.ipayskeleton.HomeFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer.OfferResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OfferFragment extends BaseFragment {

    private RecyclerView mOfferListRecyclerView;
    private OfferListAdapter mOfferListAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mEmptyListTextView;
    private FirebaseDatabase database;
    private DatabaseReference myRef ;
    private List<OfferResponse> mOfferList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offer, container, false);
        getActivity().setTitle(R.string.offer);

        initializeViews(v);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOfferList = new ArrayList<OfferResponse>();
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    OfferResponse value = dataSnapshot1.getValue(OfferResponse.class);

                    Calendar calendar = Calendar.getInstance();
                    long currentTime = calendar.getTimeInMillis();

                    if(currentTime < value.getExpire_date()) {
                        mOfferList.add(value);
                    }
                }


                setupRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initializeViews(View v) {
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mOfferListRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);
    }

    private void setupRecyclerView() {
        mOfferListAdapter = new OfferListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mOfferListRecyclerView.setLayoutManager(mLayoutManager);
        mOfferListRecyclerView.setAdapter(mOfferListAdapter);
    }

    private class OfferListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mOfferTitleView;
            private final TextView mOfferDetailsView;
            private final TextView mExpireDateView;
            private final ProfileImageView mOfferImageView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mOfferTitleView = (TextView) itemView.findViewById(R.id.title);
                mOfferDetailsView = (TextView) itemView.findViewById(R.id.sub_title);
                mExpireDateView = (TextView) itemView.findViewById(R.id.offer_expire);
                mOfferImageView = (ProfileImageView) itemView.findViewById(R.id.offer_image);
                mOfferImageView.setBusinessLogoPlaceHolder();
            }

            public void bindView(int pos) {
                final OfferResponse transactionHistory = mOfferList.get(pos);

                final String description = transactionHistory.getTitle();
                final String receiver = transactionHistory.getSubtitle();
                final String status = transactionHistory.getImage_url();
                final long expire = transactionHistory.getExpire_date();

                mOfferTitleView.setText(description);
                mOfferDetailsView.setText(receiver);
                mExpireDateView.setText("This offer will expire in "+Utilities.formatDateWithTimeWithDevider(expire));
                mOfferImageView.setProfilePicture(status, false);
            }
        }


        // Now define the view holder for Normal mOfferList item
        class NormalViewHolder extends ViewHolder {
            NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offer, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mOfferList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

    }
}
