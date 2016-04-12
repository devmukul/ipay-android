package bd.com.ipay.ipayskeleton.SendMoneyFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.R;

public class SendMoneyReviewFragment extends Fragment implements HttpResponseListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_money_review, container, false);



        return v;
    }

    @Override
    public void httpResponseReceiver(String result) {

    }
}
