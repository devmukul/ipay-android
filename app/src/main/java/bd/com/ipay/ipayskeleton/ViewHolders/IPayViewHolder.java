package bd.com.ipay.ipayskeleton.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class IPayViewHolder<T> extends RecyclerView.ViewHolder {
	public IPayViewHolder(View itemView) {
		super(itemView);
	}

	public abstract void bindTo(T t);
}
