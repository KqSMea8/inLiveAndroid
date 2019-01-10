package tw.chiae.inlive.presentation.ui.base.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public abstract class SimpleRecyclerHolder<DataType> extends RecyclerView.ViewHolder {

    public SimpleRecyclerHolder(View itemView) {
        super(itemView);
    }

    public abstract void displayData(DataType data);
}