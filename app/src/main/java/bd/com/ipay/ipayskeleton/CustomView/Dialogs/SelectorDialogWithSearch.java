package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SelectorDialogWithSearch extends AlertDialog implements SearchView.OnQueryTextListener{

    private final List<String> resources;
    private final Context context;
    private List<String> stringIds;
    private List<String> names;
    private int selectedItemId;

    private OnResourceSelectedListener onResourceSelectedListener;
    private ArrayAdapter<String> arrayAdapter;

    private LayoutInflater inflater;
    private View view, viewTitle;
    private TextView textViewTitle;
    private RecyclerView popUpList;
    private MerchantOutletAdapter mMerchantBranchAdapter;

    private SearchView mSearchView;

    public SelectorDialogWithSearch(Context context, String mTitle, List<String> resources) {
        super(context);
        this.context = context;
        this.resources = resources;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewTitle = inflater.inflate(R.layout.dialog_selector_header, null);
        textViewTitle = viewTitle.findViewById(R.id.textviewTitle);
        textViewTitle.setText(mTitle);
        this.setCustomTitle(viewTitle);

        view = inflater.inflate(R.layout.dialogue_custom_recyclerview, null);
        this.setView(view);

        setItems(resources);
    }

    private void setItems(List<String> resources) {
        names = new ArrayList<>();

        for (String resource : resources) {
            names.add(resource);
        }
        popUpList = view.findViewById(R.id.custom_list);

        popUpList = view.findViewById(R.id.custom_list);
        mMerchantBranchAdapter = new MerchantOutletAdapter(context, names);
        popUpList.setLayoutManager(new LinearLayoutManager(context));
        popUpList.setAdapter(mMerchantBranchAdapter);

        mSearchView = view.findViewById(R.id.search);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();

        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );



//        CustomAdapter adapter = new CustomAdapter(context, names);
//        popUpList.setAdapter(adapter);
//        popUpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String name = names.get(i);
//                if (onResourceSelectedListener != null)
//                    onResourceSelectedListener.onResourceSelected(name);
//                dismiss();
//            }
//        });
    }

//    private void setItemsWithStringID(List<String> resources) {
//        stringIds = new ArrayList<>();
//        names = new ArrayList<>();
//
//        for (String resource : resources) {
//            names.add(resource);
//        }
//        popUpList = view.findViewById(R.id.custom_list);
//        CustomAdapter adapter = new CustomAdapter(context, names);
//        popUpList.setAdapter(adapter);
//        popUpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String name = names.get(i);
//                String stringID = stringIds.get(i);
//
//                if (onResourceSelectedListenerWithStringID != null)
//                    onResourceSelectedListenerWithStringID.onResourceSelectedWithStringID(name, selectedItemId);
//                dismiss();
//            }
//        });
//    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mMerchantBranchAdapter.getFilter().filter(query);
        return true;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(String name);
    }

    public class MerchantOutletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

        private List<String> mOutlets;
        private List<String> mFilteredOutlets;

        private static final int EMPTY_VIEW = 10;
        private static final int OUTLET_VIEW = 100;


        public MerchantOutletAdapter(Context context, List<String> mOutlets) {
            this.mOutlets = mOutlets;
            this.mFilteredOutlets = mOutlets;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empty_description, parent, false);
                return new MerchantOutletAdapter.EmptyViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selector_with_search, parent, false);
                return new MerchantOutletAdapter.ViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {

                if (holder instanceof ViewHolder) {
                    ViewHolder vh = (ViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof EmptyViewHolder) {
                    EmptyViewHolder vh = (EmptyViewHolder) holder;
                    vh.mEmptyDescription.setText(context.getString(R.string.no_outlets));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mFilteredOutlets == null)
                return 0;
            else
                return mFilteredOutlets.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == 0)
                return EMPTY_VIEW;
            else
                return OUTLET_VIEW;
        }

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public final TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;
            private TextView mNameTextView;

            public ViewHolder(View itemView) {

                super(itemView);

                this.itemView = itemView;
                mNameTextView = itemView.findViewById(R.id.textViewSelectorName);
            }

            public void bindView(final int position) {
                mNameTextView.setText(mFilteredOutlets.get(position));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if (onResourceSelectedListener != null)
                        onResourceSelectedListener.onResourceSelected(mFilteredOutlets.get(position));
                    dismiss();
                    }
                });
            }
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();

                    if (charString.isEmpty()) {
                        mFilteredOutlets = mOutlets;
                    } else {
                        List<String> filteredList = new ArrayList<>();

                        for (String outletsList : mOutlets) {

                            if (outletsList.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(outletsList);
                            }
                        }

                        mFilteredOutlets = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredOutlets;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mFilteredOutlets = (List<String>) filterResults.values;
//
//                    if (mFilteredOutlets.size() == 0) {
//                        noResultTextView.setVisibility(View.VISIBLE);
//                    }else{
//                        noResultTextView.setVisibility(View.GONE);
//                    }
                    notifyDataSetChanged();
                }
            };
        }

//        private void switchToMakePaymentActivity(int position) {
//            TrendingBusinessOutletSelectorDialog.this.dismiss();
//            customItemClickListener.onItemClick(merchantDetails.getMerchantName(), merchantDetails.getMerchantMobileNumber(),
//                    merchantDetails.getBusinessLogo(), mFilteredOutlets.get(position).getAddressString(), mFilteredOutlets.get(position).getOutletId());
//        }
    }
}
