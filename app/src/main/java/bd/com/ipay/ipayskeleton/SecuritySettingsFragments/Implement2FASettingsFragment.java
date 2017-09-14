package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2fa_implement, container, false);
        getTwoFaSettings();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyler_2fa);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        Implement2FaAdapter adapter = new Implement2FaAdapter(mTwoFaServiceList);
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
        mTwoFaServiceList = new ArrayList<>();
        List<TwoFAService> twoFAServiceLists = new ArrayList<>();

        TwoFAService twoFAService = new TwoFAService("1", "send money 1", true);
        TwoFAService twoFAService2 = new TwoFAService("11", "send money 11", false);
        TwoFAService twoFAService3 = new TwoFAService("111", "send money 111", true);
        TwoFAService twoFAService4 = new TwoFAService("111", "send money 111", true);
        twoFAServiceLists.add(twoFAService);
        twoFAServiceLists.add(twoFAService2);
        twoFAServiceLists.add(twoFAService3);
        mTwoFaServiceList.add(new TwoFAServiceGroup("group 1", twoFAServiceLists));


        twoFAService = new TwoFAService("2", "request money 2", false);
        twoFAService2 = new TwoFAService("22", "request money 22", true);
        twoFAServiceLists = new ArrayList<>();
        twoFAServiceLists.add(twoFAService);
        twoFAServiceLists.add(twoFAService2);
        mTwoFaServiceList.add(new TwoFAServiceGroup("group 2", twoFAServiceLists));

        twoFAService = new TwoFAService("3", "add money 2", false);
        twoFAService2 = new TwoFAService("33", "add money 22", true);
        twoFAService3 = new TwoFAService("333", "add money 333", true);
        twoFAService4 = new TwoFAService("3333", "add money 3333", true);

        twoFAServiceLists = new ArrayList<>();
        twoFAServiceLists.add(twoFAService);
        twoFAServiceLists.add(twoFAService2);
        twoFAServiceLists.add(twoFAService3);
        twoFAServiceLists.add(twoFAService4);
        mTwoFaServiceList.add(new TwoFAServiceGroup("group 3", twoFAServiceLists));

    }

    public class Implement2FaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int IMPLEMENT_2FA_HEADER_VIEW = 1;
        private static final int IMPLEMENT_2FA_ITEM_VIEW = 2;
        private static final int FOOTER_VIEW = 3;

        private List<TwoFAServiceGroup> mTwoFAServiceGroupList;
        private List<Integer> headerPositionList;
        private int footerPosition;
        private int itemCount;

        public Implement2FaAdapter(List<TwoFAServiceGroup> twoFAServiceGroupList) {
            this.mTwoFAServiceGroupList = twoFAServiceGroupList;
            this.headerPositionList = new ArrayList<>();
            footerPosition = 0;

            itemCount = 0;
            headerPositionList = new ArrayList<>();
            if (this.mTwoFAServiceGroupList != null) {
                for (TwoFAServiceGroup twoFAServiceGroup : this.mTwoFAServiceGroupList) {
                    headerPositionList.add(itemCount);
                    itemCount++;
                    if (twoFAServiceGroup != null && twoFAServiceGroup.getServices() != null) {
                        itemCount += twoFAServiceGroup.getServices().size();
                    }
                }
            }
            footerPosition = itemCount;
            itemCount++;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mHeaderTextView;
            private TextView mDescriptionTextView;
            private Switch mSwitch;
            private Button mButtonSave;
            private View mMargin;

            public ViewHolder(final View itemView) {
                super(itemView);
                mHeaderTextView = (TextView) itemView.findViewById(R.id.header_text_view);
                mDescriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
                mSwitch = (Switch) itemView.findViewById(R.id.switch_list_item);
                mButtonSave = (Button) itemView.findViewById(R.id.button_footer_save);
                mMargin = itemView.findViewById(R.id.divider_below);
            }

            public void bindViewListItem(int position) {
                int twoFaSettingsGroupIndex = 0;
                int selectedHeaderPosition = 0;
                for (int i = 0; i < headerPositionList.size(); i++) {
                    if (position > headerPositionList.get(i)) {
                        if (i < headerPositionList.size() - 1) {
                            if (position < headerPositionList.get(i + 1)) {
                                twoFaSettingsGroupIndex = i;
                                selectedHeaderPosition = headerPositionList.get(i);
                                break;
                            }
                        } else if (i == headerPositionList.size() - 1) {
                            twoFaSettingsGroupIndex = i;
                            selectedHeaderPosition = headerPositionList.get(i);
                            break;
                        }
                    }
                }
                TwoFAService twoFAService = mTwoFAServiceGroupList.get(twoFaSettingsGroupIndex).getServices().get(position - selectedHeaderPosition - 1);
                mDescriptionTextView.setText(twoFAService.getServiceName());
                mSwitch.setChecked(twoFAService.getIsEnabled());
            }

            public void bindHeader(int position) {
                mHeaderTextView.setText(mTwoFAServiceGroupList.get(headerPositionList.indexOf(position)).getGroupName());
            }

            public void bindFooter() {
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
            switch (viewType) {
                case IMPLEMENT_2FA_HEADER_VIEW:
                    return new ServiceHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header_2fa, parent, false));
                case IMPLEMENT_2FA_ITEM_VIEW:
                    return new ServiceListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_2fa, parent, false));
                case FOOTER_VIEW:
                    return new ServiceFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_button_two_fa_footer, parent, false));
                default:
                    return null;
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
                vh.bindFooter();
            }
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            int headerPositionIndex = headerPositionList.indexOf(position);
            if (headerPositionIndex != -1) {
                return IMPLEMENT_2FA_HEADER_VIEW;
            } else if (position == footerPosition) {
                return FOOTER_VIEW;
            } else {
                return IMPLEMENT_2FA_ITEM_VIEW;
            }
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
