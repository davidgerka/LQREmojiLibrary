package com.bet007.mobile.score.adapter.qiuyou;

import android.view.View;
import android.widget.TextView;

import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.lqr.R;

/**
 * 聊天列表中Tips消息ViewHolder
 *
 * @param <MESSAGE>
 */
public class ChatItemTipsViewHolder<MESSAGE extends IChatMessage>
        extends BaseChatMessageViewHolder<MESSAGE> {

    protected TextView tvTime;
    protected TextView tvTips;

    public ChatItemTipsViewHolder(View itemView) {
        super(itemView);
        tvTime = getView(R.id.tv_time);
        tvTips = getView(R.id.tv_tips);
    }

    @Override
    public void onBind(final MESSAGE message) {
        tvTime.setText("" + message.getCreateTime());
        tvTips.setText(message.getContentText());
    }

}