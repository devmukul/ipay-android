package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddCardResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.CardDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BankBranch;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddCardActivity extends BaseActivity implements HttpResponseListener {

    private boolean switchedFromBankVerification = false;
    private HttpRequestGetAsyncTask mGetAllAddedCards;
    private AddCardResponse addCardResponse;

    private List<CardDetails> mCardList;

    public FloatingActionButton mFabAddNewBank;
    private TextView mDescriptionTextView;

    public ArrayList<String> mDistrictNames;
    public ArrayList<BankBranch> mBranches;
    public ArrayList<String> mBranchNames;

    private RecyclerView mAllCardListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        mDistrictNames = new ArrayList<>();
        mBranches = new ArrayList<>();
        mBranchNames = new ArrayList<>();

        mFabAddNewBank = (FloatingActionButton) findViewById(R.id.fab_add_new_bank);
        mDescriptionTextView = (TextView) findViewById(R.id.header_text_view);
        mAllCardListRecyclerView = (RecyclerView) findViewById(R.id.card_list);
        setTitle(this.getClass().getSimpleName());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Added Cards");
        getAddedCards();
        mFabAddNewBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCardActivity.this, AddMoneyActivity.class);
                intent.putExtra(Constants.TAG, "CARD");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getAddedCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void getAddedCards() {
        if (mGetAllAddedCards != null) return;
        else {
            mGetAllAddedCards = new HttpRequestGetAsyncTask(Constants.COMMAND_ADD_CARD,
                    Constants.BASE_URL_MM + Constants.URL_GET_CARD, this, this);
            mGetAllAddedCards.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private int getAppropriateCardIcon(String imageUrl) {
        return getResources().getIdentifier(imageUrl, "drawable", this.getPackageName());

    }

    @Override
    protected Context setContext() {
        return AddCardActivity.this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllAddedCards = null;
            if (this != null) {
                Toaster.makeText(this, "Card List Fetch Failed", Toast.LENGTH_LONG);
            }
            return;
        } else {
            Gson gson = new Gson();
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                addCardResponse = gson.fromJson(result.getJsonString(), AddCardResponse.class);
                mCardList = addCardResponse.getUserCardList();
                if (mCardList.size() == 0) mDescriptionTextView.setVisibility(View.VISIBLE);
                else mDescriptionTextView.setVisibility(View.GONE);
                CardAdapter cardAdapter = new CardAdapter();
                mAllCardListRecyclerView.setAdapter(cardAdapter);
                mAllCardListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                cardAdapter.notifyDataSetChanged();
            }
            mGetAllAddedCards = null;
        }

    }

    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_added_cards, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {
            holder.mTitleTextView.setText(mCardList.get(position).getCardInfo());
            holder.mCardImageView.setImageDrawable(getResources().
                    getDrawable(getAppropriateCardIcon(mCardList.get(position).getCardType().toLowerCase())));
            if (mCardList.get(position).getCardStatus().equals(Constants.VERIFIED)) {
                holder.mVerifyImageView.setVisibility(View.VISIBLE);
            } else {
                holder.mVerifyImageView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (mCardList != null) {
                return mCardList.size();
            } else {
                return 0;
            }
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            private TextView mTitleTextView;
            private ImageView mVerifyImageView;
            private ImageView mCardImageView;

            public CardViewHolder(View itemView) {
                super(itemView);
                mTitleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
                mVerifyImageView = (ImageView) itemView.findViewById(R.id.verify_icon);
                mCardImageView = (ImageView) itemView.findViewById(R.id.icon_card);
            }
        }
    }
}