
package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PhotoSelectionHelperDialog extends AlertDialog {
    private static final String MALE_GENDER = "M";
    private static final String FEMALE_GENDER = "F";

    private Context context;
    private OnResourceSelectedListener onResourceSelectedListener;
    private LayoutInflater inflater;

    private View mSelectImageHeaderView;
    private ImageView mPhotoHelperView;
    private TextView mPhotoHelperTextView;
    private TextView mSelectImageHeaderTitle;
    private ListView mImageSelectorOptionsListView;

    public PhotoSelectionHelperDialog(Context context, String mTitle, List<String> resources, int selectOption) {
        super(context);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_profile_picture_helper, null);
        mPhotoHelperView = (ImageView) view.findViewById(R.id.pro_pic_helper_view);
        mPhotoHelperTextView = (TextView) view.findViewById(R.id.pro_pic_help_text_view);
        mSelectImageHeaderView = inflater.inflate(R.layout.dialog_selector_header, null);
        mSelectImageHeaderTitle = (TextView) mSelectImageHeaderView.findViewById(R.id.textviewTitle);
        mSelectImageHeaderTitle.setText(mTitle);

        if (selectOption == Constants.TYPE_PROFILE_PICTURE)
            setAppropriateProfilePictureHelperPhoto();
        else
            setAppropriateBusinessLogoHelperPhoto();

        this.setCustomTitle(mSelectImageHeaderView);
        this.setView(view);

        setItems(resources, view);
    }

    private void setAppropriateProfilePictureHelperPhoto() {
        if (ProfileInfoCacheManager.getGender() != null) {
            if (ProfileInfoCacheManager.getGender().equalsIgnoreCase(MALE_GENDER))
                mPhotoHelperView.setImageDrawable(context.getResources().getDrawable(R.drawable.profile_pic_helper_male));
            else if (ProfileInfoCacheManager.getGender().equalsIgnoreCase(FEMALE_GENDER))
                mPhotoHelperView.setImageDrawable(context.getResources().getDrawable(R.drawable.profile_pic_helper_female));
        }
    }

    private void setAppropriateBusinessLogoHelperPhoto() {
        mPhotoHelperTextView.setVisibility(View.GONE);
        mPhotoHelperView.setImageDrawable(context.getResources().getDrawable(R.drawable.business_logo));
    }

    public void setItems(final List<String> resources, final View view) {

        mImageSelectorOptionsListView = (ListView) view.findViewById(R.id.image_selection_option_list);

        SelectorAdapter adapter = new SelectorAdapter(context, resources);
        mImageSelectorOptionsListView.setAdapter(adapter);

        mImageSelectorOptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = resources.get(i);

                if (onResourceSelectedListener != null)
                    onResourceSelectedListener.onResourceSelected(i, name);
                dismiss();
            }
        });
    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(int id, String name);
    }

    private class SelectorAdapter extends ArrayAdapter<String> {

        private LayoutInflater inflater;

        public SelectorAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String mSelectorName = getItem(position);
            View view = convertView;

            if (view == null)
                view = inflater.inflate(R.layout.custom_selector_list_item_centered, null);

            TextView selectorView = (TextView) view.findViewById(R.id.textViewSelectorName);
            selectorView.setText(mSelectorName);
            return view;
        }
    }
}