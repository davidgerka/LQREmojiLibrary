package com.bet007.mobile.score.adapter.qiuyou;

import android.view.View;
import android.widget.TextView;

import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.lqr.R;

/**
 * 聊天列表中Action消息ViewHolder
 *
 * @param <MESSAGE>
 */
public class ChatItemActionViewHolder<MESSAGE extends IChatMessage>
        extends BaseChatItemViewHolder<MESSAGE> {

    protected TextView tvAction;

    public ChatItemActionViewHolder(View itemView, boolean isSender) {
        super(itemView, isSender);
        tvAction = getView(R.id.tv_action);


        ivUnread.setVisibility(View.GONE);
        getView(R.id.fl_content).setVisibility(View.GONE);
        getView(R.id.tv_record_time).setVisibility(View.GONE);
        getView(R.id.iv_fail).setVisibility(View.GONE);
        getView(R.id.progressbar).setVisibility(View.GONE);
    }

    @Override
    public void onBind(final MESSAGE message) {
        super.onBind(message);
        tvAction.setText(message.getContentText());
    }

}