package com.bet007.mobile.score.adapter.qiuyou;

import android.view.View;

import com.github.jdsjlzx.base.SuperViewHolder;

/**
 *
 */

public abstract class BaseViewHolder<DATA> extends SuperViewHolder{

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(DATA data);
}
