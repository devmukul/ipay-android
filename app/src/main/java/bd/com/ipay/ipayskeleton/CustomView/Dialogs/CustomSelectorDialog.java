package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.R;

public class CustomSelectorDialog extends AlertDialog {

    private int mPosition;

    private OnResourceSelectedListener onResourceSelectedListener;
    private OnResourceSelectedListenerWithPosition mOnResourceSelectedListenerWithSelectedPosition;

    private View mRootView;
    private ListView mPopupListView;

    public CustomSelectorDialog(Context context, String mTitle, List<String> resources) {
        super(context);

        setupView(context, mTitle);

        setItems(resources);
    }

    public CustomSelectorDialog(Context context, String mTitle, List<String> resources, int position) {
        super(context);

        setupView(context, mTitle);

        this.mPosition = position;
        setItemsWithPosition(resources);

    }

    private void setupView(Context context, String mTitle) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewTitle = inflater.inflate(R.layout.dialog_selector_header, null);
        TextView textViewTitle = (TextView) viewTitle.findViewById(R.id.textviewTitle);
        textViewTitle.setText(mTitle);
        this.setCustomTitle(viewTitle);

        mRootView = inflater.inflate(R.layout.dialog_custom_listview, null);
        this.setView(mRootView);
    }

    private void setItems(final List<String> resources) {

        mPopupListView = (ListView) mRootView.findViewById(R.id.custom_list);
        SelectorAdapter adapter = new SelectorAdapter(getContext(), resources);
        mPopupListView.setAdapter(adapter);
        mPopupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = resources.get(i);

                if (onResourceSelectedListener != null)
                    onResourceSelectedListener.onResourceSelected(i, name);
                dismiss();
            }
        });
    }

    private void setItemsWithPosition(final List<String> resources) {

        mPopupListView = (ListView) mRootView.findViewById(R.id.custom_list);
        SelectorAdapter adapter = new SelectorAdapter(getContext(), resources);
        mPopupListView.setAdapter(adapter);
        mPopupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = resources.get(i);

                if (mOnResourceSelectedListenerWithSelectedPosition != null)
                    mOnResourceSelectedListenerWithSelectedPosition.onResourceSelectedWithPosition(i, name, mPosition);
                dismiss();
            }
        });
    }

    private class SelectorAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        @NonNull
        List<String> itemList = new ArrayList<>();

        SelectorAdapter(Context context, @Nullable List<String> itemList) {
            if (itemList != null) {
                this.itemList = itemList;
            }
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = inflater.inflate(R.layout.list_item_custom_selector, parent, false);

            if (!itemList.isEmpty() && position < itemList.size()) {
                final String mSelectorName = itemList.get(position);

                TextView selectorView = (TextView) view.findViewById(R.id.textViewSelectorName);
                selectorView.setText(mSelectorName);
            }

            return view;
        }

        @Nullable
        @Override
        public CharSequence[] getAutofillOptions() {
            CharSequence[] autoFillOptions = new CharSequence[itemList.size()];
            for (int i = 0; i < itemList.size(); i++) {
                autoFillOptions[0] = itemList.get(i);
            }
            return autoFillOptions;
        }
    }


    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    public void setOnResourceSelectedListenerWithSelectedPosition(OnResourceSelectedListenerWithPosition mOnResourceSelectedListenerWithSelectedPosition) {
        this.mOnResourceSelectedListenerWithSelectedPosition = mOnResourceSelectedListenerWithSelectedPosition;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(int id, String name);
    }

    public interface OnResourceSelectedListenerWithPosition {
        void onResourceSelectedWithPosition(int id, String name, int selectedIndex);
    }
}
