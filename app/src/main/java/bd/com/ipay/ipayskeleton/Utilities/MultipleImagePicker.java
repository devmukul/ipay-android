package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Intent;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.R;

public class MultipleImagePicker {
    public static Intent getMultipleImagePickerIntent(Activity activity, int limit) {
        ArrayList<Image> images = new ArrayList<>();
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
        intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_MULTIPLE);
        intent.putExtra(ImagePicker.EXTRA_LIMIT, limit);
        intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGES, images);
        intent.putExtra(ImagePicker.EXTRA_FOLDER_TITLE, activity.getString(R.string.album));
        intent.putExtra(ImagePicker.EXTRA_IMAGE_TITLE, activity.getString(R.string.tap_to_select_images));
        intent.putExtra(ImagePicker.EXTRA_IMAGE_DIRECTORY, activity.getString(R.string.camera));

        /* Will force ImagePicker to single pick */
        intent.putExtra(ImagePicker.EXTRA_RETURN_AFTER_FIRST, true);
        return intent;
    }
}
