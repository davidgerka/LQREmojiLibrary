package com.github.jdsjlzx.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 可以实现多种类型的列表基类adapter
 *
 * @param <T>
 */

public abstract class MultiListBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context mContext;
    protected List<T> mDataList = new ArrayList<>();
    protected LayoutInflater mInflater;
    private boolean needModifyRootView;//是否需要对布局进行添加View或者其他操作

    public MultiListBaseAdapter(Context context, boolean needModifyRootView) {
        mContext = context;
        this.needModifyRootView = needModifyRootView;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(getLayoutId(viewType), parent, false);
        if (needModifyRootView) {
            modifyItemViewGroup((ViewGroup) itemView, mInflater, viewType);
        }
        return new SuperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindItemHolder(holder, position);
    }

    //局部刷新关键：带payload的这个onBindViewHolder方法必须实现
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            onBindItemHolder(holder, position, payloads);
        }

    }

    public abstract int getLayoutId(int viewType);

    public abstract void onBindItemHolder(RecyclerView.ViewHolder holder, int position);

    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {

    }

    //需要对布局进行添加View或者其他操作的，可以重写改方法
    public void modifyItemViewGroup(ViewGroup parent, LayoutInflater inflater, int viewType) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 设置List的数据集，如果用这个方法来设置数据，要维护两个List，List数据要同步好
     *
     * @param list
     */
    public void setDataList(Collection<T> list) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 设置List的指针，如果用这个方法来设置数据，List有更改时，必须调用notifyDataSetChanged();否则报错
     *
     * @param list
     */
    public void setDataListPointer(List<T> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void remove(T object) {
        if (object == null) {
            return;
        }
        int position = this.mDataList.indexOf(object);
        if (position != -1) {
            remove(position);
        }
    }

    public void remove(int position) {
        this.mDataList.remove(position);
        notifyItemRemoved(position);

        if (position != (getDataList().size())) { // 如果移除的是最后一个，忽略
            notifyItemRangeChanged(position, this.mDataList.size() - position);
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }
}
