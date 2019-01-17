package bd.com.ipay.android.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class PagedListViewHolder<Model> extends RecyclerView.ViewHolder {

	protected PagedListViewHolder(@NonNull View itemView) {
		super(itemView);
	}


	public abstract void bindTo(Model model);
}
