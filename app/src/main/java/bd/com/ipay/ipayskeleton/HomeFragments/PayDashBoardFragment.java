package bd.com.ipay.ipayskeleton.HomeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Pay.PayDashBoardIconProperty;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Pay.PayPropertyConstants;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class PayDashBoardFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PayIconAdapter mAdapter;
    private PinChecker pinChecker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_pay_dashboard, container, false);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_services);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new PayIconAdapter(PayPropertyConstants.getPayIconDataSet());
        mRecyclerView.setAdapter(mAdapter);

        return v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

        if (menu.findItem(R.id.action_filter_by_service) != null)
            menu.findItem(R.id.action_filter_by_service).setVisible(false);
        if (menu.findItem(R.id.action_filter_by_date) != null)
            menu.findItem(R.id.action_filter_by_date).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public class PayIconAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;
        private static final int CONTACT_VIEW = 100;

        ArrayList<PayDashBoardIconProperty> dataList;

        public PayIconAdapter(ArrayList<PayDashBoardIconProperty> dataList) {

            this.dataList = dataList;
        }

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public final TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;

            private final TextView titleView;
            private final ProfileImageView profilePictureView;

            public ViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                titleView = (TextView) itemView.findViewById(R.id.pay_text_view);
                profilePictureView = (ProfileImageView) itemView.findViewById(R.id.pay_icon_view);
            }

            public void bindView(int pos) {
                final String title = dataList.get(pos).getTitle();
                final String imageUrl = dataList.get(pos).getImageUrl();
                final int serviceId = dataList.get(pos).getServiceId();
                final String operatorPrefix = dataList.get(pos).getOperatorPrefix();

                if (title != null && !title.isEmpty()) {
                    titleView.setText(title);
                }

                profilePictureView.setProfilePicture(imageUrl, false);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (serviceId == Constants.SERVICE_ID_MAKE_PAYMENT) {
                            if (title.equals(PayPropertyConstants.PAY_BY_QR_CODE)) {
                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent;
                                        intent = new Intent(getActivity(), QRCodePaymentActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                            } else {
                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent;
                                        intent = new Intent(getActivity(), PaymentActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                            }

                        } else if (serviceId == Constants.SERVICE_ID_TOP_UP) {
                            if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.TOP_UP)) {
                                DialogUtils.showServiceNotAllowedDialog(getContext());
                                return;
                            }
                            pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                @Override
                                public void ifPinAdded() {
                                    Intent intent = new Intent(getActivity(), TopUpActivity.class);
                                    intent.putExtra(Constants.MOBILE_NUMBER_REGEX, operatorPrefix);
                                    startActivity(intent);
                                }
                            });
                            pinChecker.execute();
                        }
                        else if (serviceId == Constants.SERVICE_ID_REQUEST_PAYMENT) {

                        }
                    }
                });
            }

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empty_description, parent, false);
                return new PayIconAdapter.EmptyViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pay_icon, parent, false);
                return new PayIconAdapter.ViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {

                if (holder instanceof PayIconAdapter.ViewHolder) {
                    PayIconAdapter.ViewHolder vh = (PayIconAdapter.ViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof PayIconAdapter.EmptyViewHolder) {
                    PayIconAdapter.EmptyViewHolder vh = (PayIconAdapter.EmptyViewHolder) holder;
                    vh.mEmptyDescription.setText(getString(R.string.no_contacts));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
//            if (mCursor == null || mCursor.isClosed())
//                return 0;
//            else
            return dataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == 0)
                return EMPTY_VIEW;
            else
                return CONTACT_VIEW;
        }
    }


}
