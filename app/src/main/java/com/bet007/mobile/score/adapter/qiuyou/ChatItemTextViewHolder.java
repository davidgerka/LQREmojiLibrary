package com.bet007.mobile.score.adapter.qiuyou;

import android.view.View;
import android.widget.TextView;

import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.lqr.R;

/**
 * 聊天列表中文本消息ViewHolder
 *
 * @param <MESSAGE>
 */
public class ChatItemTextViewHolder<MESSAGE extends IChatMessage>
        extends BaseChatItemViewHolder<MESSAGE> {


    protected TextView tvContent;
    protected View layoutContent;


    public ChatItemTextViewHolder(View itemView, boolean isSender) {
        super(itemView, isSender);
        tvContent = getView(R.id.tv_content_text);
        layoutContent = getView(R.id.content_layout_text);

        ivUnread.setVisibility(View.GONE);
        getView(R.id.tv_action).setVisibility(View.GONE);
        getView(R.id.content_layout_record).setVisibility(View.GONE);
        getView(R.id.content_layout_pic).setVisibility(View.GONE);

    }

    @Override
    public void onBind(final MESSAGE message) {
        super.onBind(message);

        tvContent.setText(message.getContentText());

        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMsgClickListener != null) {
                    mMsgClickListener.onMessageClick(message);
                }
            }
        });

        layoutContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMsgLongClickListener != null) {
                    mMsgLongClickListener.onMessageLongClick(message);
                } else {

                }
                return true;
            }
        });
    }

}