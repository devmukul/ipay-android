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
import com.bumptech.glide.signature.StringSignature;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PayDashBoardItemAdapter extends RecyclerView.Adapter<PayDashBoardItemAdapter.CardViewHolder> {

    private List<BusinessAccountEntry> mBusinessAccountEntryList;
    Context context;

    public PayDashBoardItemAdapter(List<BusinessAccountEntry> mBusinessAccountEntryList, Context context) {
        this.mBusinessAccountEntryList = mBusinessAccountEntryList;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dashboard_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.mTextView.setText(mBusinessAccountEntryList.get(position).getBusinessName());
        setImageView(holder.mImageView, Constants.BASE_URL_FTP_SERVER + mBusinessAccountEntryList.get(position).getProfilePictureUrl(), true);
    }

    private void setImageView(ImageView imageView, String attachmentUri, boolean forceLoad) {
        try {
            final DrawableTypeRequest<String> glide = Glide.with(context).load(attachmentUri);

            glide
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (forceLoad) {
                glide
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())));
            }

            glide
                    .placeholder(R.drawable.ic_business_logo_round)
                    .error(R.drawable.ic_business_logo_round)
                    .crossFade()
                    .dontAnimate()
                    .into(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mBusinessAccountEntryList != null) {
            return mBusinessAccountEntryList.size();
        } else {
            return 0;
        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;

        public CardViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTextView = (TextView) itemView.findViewById(R.id.nameView);

        }

    }
}

