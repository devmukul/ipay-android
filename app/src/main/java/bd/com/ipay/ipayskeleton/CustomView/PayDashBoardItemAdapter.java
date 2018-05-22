package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;


public class PayDashBoardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BusinessAccountEntry> mBusinessAccountEntryList;
    Context context;

    public PayDashBoardItemAdapter(List<BusinessAccountEntry> mBusinessAccountEntryList, Context context) {
        this.mBusinessAccountEntryList = mBusinessAccountEntryList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTextView = (TextView) itemView.findViewById(R.id.nameView);

        }

        public void bindView(int pos) {
            BusinessAccountEntry businessAccountEntry = mBusinessAccountEntryList.get(pos);
            final String name = businessAccountEntry.getBusinessName();
            final String imageUrl = Constants.BASE_URL_FTP_SERVER + businessAccountEntry.getProfilePictureUrl();
            mTextView.setText(name);

            try {
                final DrawableTypeRequest<String> glide = Glide.with(context).load(imageUrl);

                glide
                        .diskCacheStrategy(DiskCacheStrategy.ALL);


                glide
                        .placeholder(R.drawable.ic_business_logo_round)
                        .error(R.drawable.ic_business_logo_round)
                        .crossFade()
                        .dontAnimate()
                        .into(mImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    // Now define the view holder for Normal  item
    class NormalViewHolder extends PayDashBoardItemAdapter.ViewHolder {
        NormalViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dashboard_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            NormalViewHolder vh = (NormalViewHolder) holder;
            vh.bindView(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mBusinessAccountEntryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


}


