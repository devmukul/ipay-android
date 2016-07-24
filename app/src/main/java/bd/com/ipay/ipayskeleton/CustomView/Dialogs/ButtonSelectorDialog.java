package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.R;

public class ButtonSelectorDialog extends AlertDialog.Builder {
    private Context context;

    private AlertDialog dialog;

    private List<String> resources;
    private String mName;
    private List<Integer> ids;

    private OnResourceSelectedListener onResourceSelectedListener;
    private ArrayAdapter<String> arrayAdapter;
    private LayoutInflater inflater;
    private View view;
    private TextView textViewTitle;


    public ButtonSelectorDialog(Context context, String mName, List<String> resources, int selectedItemId) {
        super(context);
        this.context = context;
        this.resources = resources;
        this.mName = mName;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_button_selector_header, null);
        textViewTitle = (TextView) view.findViewById(R.id.textviewTitle);
        textViewTitle.setText(mName);
        this.setCustomTitle(view);

        setItems(resources, selectedItemId);

    }

    public void setItems(final List<String> resources, int selectedItemId) {
        ids = new ArrayList<>();

        for (String resource : resources) {
            ids.add(resources.indexOf(resource));
        }

        int selectedItemPosition = getSelectedItemPosition(selectedItemId);


        arrayAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_dropdown_custom_item, resources);
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
