package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFAServiceListResponse;
import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFAService;
import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFAServiceGroup;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class Implement2FASettingsFragment extends Fragment implements HttpResponseListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private HttpRequestGetAsyncTask mGetTwoFaSettingsAsynctask;
    private TwoFAServiceListResponse mTwoFaServiceResponse;

    List<TwoFAServiceGroup> mTwoFaServiceList;

    private Uri mUri;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2fa_implement, container, false);
        getTwoFaSettings();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyler_2fa);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        Implement2FaAdapter adapter = new Implement2FaAdapter();
        testDataSet();
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    private void getTwoFaSettings() {
        String API_COMMAND = Constants.COMMAND_GET_TWO_FA_SETTING;
        String mUri = Constants.BASE_URL_MM + Constants.URL_TWO_FA_SETTINGS;
        mGetTwoFaSettingsAsynctask = new HttpRequestGetAsyncTask(API_COMMAND, mUri, getActivity());
        mGetTwoFaSettingsAsynctask.mHttpResponseListener = this;
        try {
            mGetTwoFaSettingsAsynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void testDataSet() {
        TwoFAService twoFAService = new TwoFAService("1", "send money", "true");
        TwoFAService twoFAService2 = new TwoFAService("1", "send money", "false");
        TwoFAService twoFAService3 = new TwoFAService("1", "send money", "true");
        List<TwoFAService> twoFAServiceLists = new ArrayList<>();
        twoFAServiceLists.add(twoFAService);
        twoFAServiceLists.add(twoFAService2);
        twoFAServiceLists.add(twoFAService3);
        List<TwoFAService> twoFAServiceLists2 = new ArrayList<>();
        twoFAServiceLists2.add(twoFAService);
        twoFAServiceLists2.add(twoFAService2);
        twoFAServiceLists2.add(twoFAService3);
        mTwoFaServiceList = new ArrayList<>();
        mTwoFaServiceList.add(new TwoFAServiceGroup("group 1", twoFAServiceLists));
        mTwoFaServiceList.add(new TwoFAServiceGroup("group 2", twoFAServiceLists2));

    }

    public class Implement2FaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int IMPLEMENT_2FA_HEADER_VIEW = 1;
        private static final int IMPLEMENT_2FA_ITEM_VIEW = 2;
        private static final int FOOTER_VIEW = 3;

        public Implement2FaAdapter() {

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mHeaderTextView;
            private TextView mDescriptionTextView;
            private Switch mSwitch;
            private Button mButtonSave;

            public ViewHolder(final View itemView) {
                super(itemView);
                mHeaderTextView = (TextView) itemView.findViewById(R.id.header_text_view);
                mDescriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
                mSwitch = (Switch) itemView.findViewById(R.id.switch_list_item);
                mButtonSave = (Button) itemView.findViewById(R.id.button_footer_save);
            }

            public void bindViewListItem(int pos) {
                if (pos <= mTwoFaServiceList.get(0).getServices().size()) {
                    mDescriptionTextView.setText(mTwoFaServiceList.get(0).getServices().get(pos - 1).getServiceName());
                    if (mTwoFaServiceList.get(0).getServices().get(pos - 1).getIsEnabled().equals("true")) {
                        mSwitch.setChecked(true);
                    }
                } else if (pos > mTwoFaServiceList.get(0).getServices().size() && pos <= mTwoFaServiceList.get(0).getServices().size()
                        + mTwoFaServiceList.get(1).getServices().size() + 1) {
                    mDescriptionTextView.setText(mTwoFaServiceList.get(1).getServices().
                            get(pos - mTwoFaServiceList.get(0).getServices().size() - 2).getServiceName());
                    if (mTwoFaServiceList.get(1).getServices().get(pos - mTwoFaServiceList.get(0).getServices().size() - 2).getIsEnabled().equals("true")) {
                        mSwitch.setChecked(true);
                    }
                }
            }

            public void bindHeader(int pos) {
                if (pos == 0) {
                    mHeaderTextView.setText(mTwoFaServiceList.get(0).getGroupName());
                } else {
                    mHeaderTextView.setText(mTwoFaServiceList.get(1).getGroupName());
                }
            }

            public void bindFooter(int pos) {
                mButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toaster.makeText(getActivity(), "this is footer", Toast.LENGTH_SHORT);
                    }
                });

            }
        }

        public class ServiceListViewHolder extends ViewHolder {
            public ServiceListViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ServiceHeaderViewHolder extends ViewHolder {
            public ServiceHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ServiceFooterViewHolder extends ViewHolder {
            public ServiceFooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == IMPLEMENT_2FA_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header_2fa, parent, false);
                return new ServiceHeaderViewHolder(v);
            } else if (viewType == IMPLEMENT_2FA_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_2fa, parent, false);
                return new ServiceListViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_button_two_fa_footer, parent, false);
                return new ServiceFooterViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ServiceHeaderViewHolder) {
                ServiceHeaderViewHolder vh = (ServiceHeaderViewHolder) holder;
                vh.bindHeader(position);
            } else if (holder instanceof ServiceListViewHolder) {
                ServiceListViewHolder vh = (ServiceListViewHolder) holder;
                vh.bindViewListItem(position);
            } else if (holder instanceof ServiceFooterViewHolder) {
                ServiceFooterViewHolder vh = (ServiceFooterViewHolder) holder;
                vh.bindFooter(position);
            }
        }

        @Override
        public int getItemCount() {
            return mTwoFaServiceList.get(0).getServices().size() + mTwoFaServiceList.get(1).getServices().size() + 3;

        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mTwoFaServiceList.get(0).getServices().size() + 1)
                return IMPLEMENT_2FA_HEADER_VIEW;
            else if (position == mTwoFaServiceList.get(0).getServices().size() + mTwoFaServiceList.get(1).getServices().size() + 2)
                return FOOTER_VIEW;
            else
                return IMPLEMENT_2FA_ITEM_VIEW;
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        Gson gson = new Gson();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            Toast.makeText(getActivity(), result.getApiCommand(), Toast.LENGTH_LONG).show();
            mGetTwoFaSettingsAsynctask = null;
            if (getActivity() != null)
                return;
        } else {
            if (result.getApiCommand().equals(Constants.COMMAND_GET_TWO_FA_SETTING)) {
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mTwoFaServiceResponse = gson.fromJson(result.getJsonString(), TwoFAServiceListResponse.class);
                        if (mTwoFaServiceResponse != null) {
                            Toaster.makeText(getActivity(), result.getJsonString(), Toast.LENGTH_LONG);
                        } else {

                        }
                        //mTwoFaServiceList = mTwoFaServiceResponse.getResponse();
                        //Toaster.makeText(getActivity(),Integer.toString(mTwoFaServiceList.size()),Toast.LENGTH_LONG);
                    }
                } catch (Exception e) {
                    Toaster.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }
    }

}
