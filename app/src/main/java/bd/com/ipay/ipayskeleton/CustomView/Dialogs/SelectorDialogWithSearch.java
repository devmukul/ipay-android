package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.R;

public class SelectorDialogWithSearch extends AlertDialog implements SearchView.OnQueryTextListener{

    private final List<String> resources;
    private final Context context;
    private List<String> names;

    private OnResourceSelectedListener onResourceSelectedListener;
    private ArrayAdapter<String> arrayAdapter;

    private LayoutInflater inflater;
    private View view, viewTitle;
    private TextView textViewTitle;
    private RecyclerView popUpList;
    private RecyclerAdapter mMerchantBranchAdapter;

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
        mMerchantBranchAdapter = new RecyclerAdapter(context, names);
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
    }

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

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

        private List<String> mLists;
        private List<String> mFilteredLists;

        public RecyclerAdapter(Context context, List<String> mLists) {
            this.mLists = mLists;
            this.mFilteredLists = mLists;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selector_with_search, parent, false);
            return new ViewHolder(v);
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
            if (mFilteredLists == null)
                return 0;
            else
                return mFilteredLists.size();
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
                mNameTextView.setText(mFilteredLists.get(position));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if (onResourceSelectedListener != null)
                        onResourceSelectedListener.onResourceSelected(mFilteredLists.get(position));
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
                        mFilteredLists = mLists;
                    } else {
                        List<String> filteredList = new ArrayList<>();
                        for (String outletsList : mLists) {
                            if (outletsList.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(outletsList);
                            }
                        }
                        mFilteredLists = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredLists;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mFilteredLists = (List<String>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
