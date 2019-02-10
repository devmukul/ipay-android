package bd.com.ipay.ipayskeleton.SourceOfFund;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;


public class IpayProgressDialog extends AlertDialog {
    private TextView messageTextView;

    public IpayProgressDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_ipay_progress_dialog, null, false);
        messageTextView = (TextView) view.findViewById(R.id.progress_text_view);
        this.setView(view);

    }

    public void setMessage(String message) {
        this.messageTextView.setText(message);
    }
}
