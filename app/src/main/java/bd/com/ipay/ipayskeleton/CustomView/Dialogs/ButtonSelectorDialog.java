package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ButtonSelectorDialog extends AlertDialog.Builder{
    private Context context;

    private List<String> resources;
    private List<Integer> ids;

    private OnResourceSelectedListener onResourceSelectedListener;
    private ArrayAdapter<String> arrayAdapter;

    public ButtonSelectorDialog(Context context, List<String> resources, int selectedItemId) {
        super(context);
        this.context = context;
        this.resources = resources;
        setItems(resources, selectedItemId);
    }

    public void setItems(final List<String> resources, int selectedItemId) {
        ids = new ArrayList<>();

        for (String resource : resources) {
            ids.add(resources.indexOf(resource));
        }

        int selectedItemPosition = getSelectedItemPosition(selectedItemId);

        arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, resources);
        setSingleChoiceItems(arrayAdapter, selectedItemPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = resources.get(which);
                int id = ids.get(which);

                if (onResourceSelectedListener != null)
                    onResourceSelectedListener.onResourceSelected(id, name);
                dialog.dismiss();
            }
        });
    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(int id, String name);
    }

    public int getSelectedItemPosition(int id) {
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                return i;
            }
        }

        return 0;
    }
}
