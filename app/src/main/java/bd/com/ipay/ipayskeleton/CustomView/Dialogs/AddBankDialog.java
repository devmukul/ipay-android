package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddBankDialog<E extends String> extends AlertDialog.Builder {

    private final Context context;

    private List<Integer> ids;
    private List<String> names;

    private OnDistrictSelectedListener onDistrictSelectedListener;
    private ArrayAdapter<String> arrayAdapter;

    public AddBankDialog(Context context, List<E> stringList, int selectedItemId) {
        super(context);
        this.context = context;
        setItems(stringList, selectedItemId);
    }

    private void setItems(List<E> stringList, int selectedItemId) {
        ids = new ArrayList<>();
        names = new ArrayList<>();

        for (String string : stringList) {
            ids.add(string.indexOf(string));
            names.add(string);
        }

        int selectedItemPosition = getSelectedItemPosition(selectedItemId);

        arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, names);
        setSingleChoiceItems(arrayAdapter, selectedItemPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = names.get(which);
                int id = ids.get(which);

                if (onDistrictSelectedListener != null)
                    onDistrictSelectedListener.onDistrictSelected(id, name);
                dialog.dismiss();
            }
        });
    }

    public void setOnDistrictSelectedListener(OnDistrictSelectedListener onDistrictSelectedListener) {
        this.onDistrictSelectedListener = onDistrictSelectedListener;
    }

    public interface OnDistrictSelectedListener {
        void onDistrictSelected(int id, String name);
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
