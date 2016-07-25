package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class ResourceSelectorDialog<E extends Resource> extends AlertDialog.Builder {

    private final List<E> resources;
    private final Context context;

    private List<Integer> ids;
    private List<String> names;

    private OnResourceSelectedListener onResourceSelectedListener;
    private ArrayAdapter<String> arrayAdapter;

    public ResourceSelectorDialog(Context context, List<E> resources, int selectedItemId) {
        super(context);
        this.context = context;
        this.resources = resources;
        setItems(resources, selectedItemId);
    }

    private void setItems(List<E> resources, int selectedItemId) {
        ids = new ArrayList<>();
        names = new ArrayList<>();

        for (Resource resource : resources) {
            ids.add(resource.getId());
            names.add(resource.getName());
        }

        int selectedItemPosition = getSelectedItemPosition(selectedItemId);

        arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, names);
        setSingleChoiceItems(arrayAdapter, selectedItemPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = names.get(which);
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

    private int getSelectedItemPosition(int id) {
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                return i;
            }
        }

        return 0;
    }
}
