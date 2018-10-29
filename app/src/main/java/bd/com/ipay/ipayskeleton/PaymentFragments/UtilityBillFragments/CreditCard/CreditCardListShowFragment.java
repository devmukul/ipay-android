package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.R;

public class CreditCardListShowFragment extends Fragment {
    private RecyclerView mCardListRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_my_card_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardListRecyclerView = view.findViewById(R.id.card_list);
    }

    class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardListViewHolder> {
        @NonNull
        @Override
        public CardListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_my_cards, parent, false);
            return new CardListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardListViewHolder holder, int position) {

        }

        @NonNull
        @Override
        public int getItemCount() {
            return 0;
        }

        class CardListViewHolder extends RecyclerView.ViewHolder {

            public CardListViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
