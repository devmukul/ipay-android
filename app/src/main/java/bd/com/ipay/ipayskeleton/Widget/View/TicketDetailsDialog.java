package bd.com.ipay.ipayskeleton.Widget.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;

public class TicketDetailsDialog {
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private AlertDialog alertDialog;
    private TextView titleTextView;

    private TextView dateTextView;
    private TextView tarinNameTextView;
    private TextView classNameTextView;
    private TextView fareAmountTextView;
    private TextView vatAmountTextView;

    private TextView bankChargeAmountTextView;
    private TextView netAmountTextView;
    private final Button buyTicketButton;
    private final ImageButton closeButton;

    public TicketDetailsDialog(Context context) {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        @SuppressLint("InflateParams") final View customTitleView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_custom_title, null, false);
        @SuppressLint("InflateParams") final View customView = LayoutInflater.from(context).inflate(R.layout.layout_ticket_info_bill, null, false);

        closeButton = customTitleView.findViewById(R.id.close_button);
        titleTextView = customTitleView.findViewById(R.id.title_text_view);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        dateTextView = customView.findViewById(R.id.account_title_enter_text_view);
        tarinNameTextView = customView.findViewById(R.id.account_number_enter_text_view);
        classNameTextView = customView.findViewById(R.id.branch_id_enter_text_view);
        fareAmountTextView = customView.findViewById(R.id.installment_amount_enter_text_view);
        vatAmountTextView = customView.findViewById(R.id.account_maturity_date_enter_text_view);
        buyTicketButton = customView.findViewById(R.id.buy_ticket_button);
        bankChargeAmountTextView = customView.findViewById(R.id.bill_title_text_view);
        netAmountTextView = customView.findViewById(R.id.bill_sub_title_text_view);
        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(customTitleView)
                .setView(customView)
                .setCancelable(false)
                .create();
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setDate(String date) {
        dateTextView.setText(date);
    }

    public void setTrainName(String trainName) {
        tarinNameTextView.setText(trainName);
    }

    public void setClassName(String className) {
        classNameTextView.setText(className);
    }

    public void setFareAmount(Double fareAmount) {
        fareAmountTextView.setText(String.format("Tk. %s", numberFormat.format(BigDecimal.valueOf(fareAmount))));
    }

    public void setVatAmount(Double vatAmount) {
        vatAmountTextView.setText(String.format("Tk. %s", numberFormat.format(BigDecimal.valueOf(vatAmount))));
    }

    public void setBankChargeAmount(Double bankChargeAmount) {
        bankChargeAmountTextView.setText(String.format("Tk. %s", numberFormat.format(BigDecimal.valueOf(bankChargeAmount))));
    }

    public void setNetAmount(Double netAmount) {
        netAmountTextView.setText(String.format("Tk. %s", numberFormat.format(BigDecimal.valueOf(netAmount))));
    }

    public void setBuyTicketButtonAction(final View.OnClickListener onClickListener) {
        buyTicketButton.setOnClickListener(onClickListener);
    }

    public void setCloseButtonAction(final View.OnClickListener onClickListener) {
        closeButton.setOnClickListener(onClickListener);
    }

    public void show() {
        if (!alertDialog.isShowing())
            alertDialog.show();
    }

    public void cancel() {
        if (alertDialog.isShowing())
            alertDialog.cancel();
    }
}
