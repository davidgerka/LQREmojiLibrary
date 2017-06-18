package com.bet007.mobile.score.adapter.qiuyou;


import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bet007.mobile.score.interfaces.ImageLoader;
import com.bet007.mobile.score.interfaces.ViewClick;
import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.github.jdsjlzx.base.MultiListBaseAdapter;
import com.github.jdsjlzx.base.SuperViewHolder;
import com.lqr.R;

/**
 * 聊天消息列表adapter
 */

public class ChatMessageAdapter<MESSAGE extends IChatMessage> extends MultiListBaseAdapter<MESSAGE> {

    private ViewClick mViewClick;

    private Context mContext;
    private String mSenderId;
//    private HoldersConfig mHolders;
//    private OnLoadMoreListener mListener;

    private ImageLoader mImageLoader;
    private boolean mIsSelectedMode;
    private OnMsgClickListener<MESSAGE> mMsgClickListener;
    private OnMsgLongClickListener<MESSAGE> mMsgLongClickListener;
    private OnAvatarClickListener<MESSAGE> mAvatarClickListener;
    private OnMsgResendListener<MESSAGE> mMsgResendListener;
    private SelectionListener mSelectionListener;
    private int mSelectedItemCount;
    private RecyclerView.LayoutManager mLayoutManager;
    //    private MessageListStyle mStyle;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    public ChatMessageAdapter(Context context, ViewClick viewClick, ImageLoader imageLoader) {
        super(context, true);
        mContext = context;
        mViewClick = viewClick;
        mImageLoader = imageLoader;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getMessageType();
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(getLayoutId(viewType), parent, false);

        switch (viewType) {
            case IChatMessage.TYPE_FROM_ACTION:
                return new ChatItemActionViewHolder<IChatMessage>(itemView, false);
            case IChatMessage.TYPE_FROM_TEXT:
                return new ChatItemTextViewHolder<IChatMessage>(itemView, false);
            case IChatMessage.TYPE_FROM_PIC:
                return new ChatItemPicViewHolder<IChatMessage>(itemView, false);
            case IChatMessage.TYPE_FROM_RECORD:
                return new ChatItemRecordViewHolder<IChatMessage>(itemView, false);
            case IChatMessage.TYPE_TO_ACTION:
                return new ChatItemActionViewHolder<IChatMessage>(itemView, true);
            case IChatMessage.TYPE_TO_TEXT:
                return new ChatItemTextViewHolder<IChatMessage>(itemView, true);
            case IChatMessage.TYPE_TO_PIC:
                return new ChatItemPicViewHolder<IChatMessage>(itemView, true);
            case IChatMessage.TYPE_TO_RECORD:
                return new ChatItemRecordViewHolder<IChatMessage>(itemView, true);
            default:
                return new ChatItemTipsViewHolder<IChatMessage>(itemView);
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case IChatMessage.TYPE_FROM_ACTION:
            case IChatMessage.TYPE_FROM_TEXT:
            case IChatMessage.TYPE_FROM_PIC:
            case IChatMessage.TYPE_FROM_RECORD:
                return R.layout.chatscene_item_chatfrom;
            case IChatMessage.TYPE_TO_ACTION:
            case IChatMessage.TYPE_TO_TEXT:
            case IChatMessage.TYPE_TO_PIC:
            case IChatMessage.TYPE_TO_RECORD:
                return R.layout.chatscene_item_chatto;
            default:
                return R.layout.chatscene_item_tips;
        }
    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final IChatMessage model = mDataList.get(position);

        ((BaseChatMessageViewHolder) holder).mPosition = holder.getAdapterPosition();
        ((BaseChatMessageViewHolder) holder).mContext = this.mContext;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        ((BaseChatMessageViewHolder) holder).mDensity = dm.density;
//        ((BaseChatMessageViewHolder) holder).mIsSelected = wrapper.isSelected;
        ((BaseChatMessageViewHolder) holder).mImageLoader = this.mImageLoader;
        ((BaseChatMessageViewHolder) holder).mMsgLongClickListener = this.mMsgLongClickListener;
        ((BaseChatMessageViewHolder) holder).mMsgClickListener = this.mMsgClickListener;
        ((BaseChatMessageViewHolder) holder).mAvatarClickListener = this.mAvatarClickListener;
        ((BaseChatMessageViewHolder) holder).mMsgResendListener = this.mMsgResendListener;
        ((BaseChatMessageViewHolder) holder).mMediaPlayer = this.mMediaPlayer;
        ((BaseViewHolder) holder).onBind(model);
    }

    public void setMsgResendListener(OnMsgResendListener<MESSAGE> listener) {
        this.mMsgResendListener = listener;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ViewHolderController.getInstance().remove(holder.getAdapterPosition());
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        ViewHolderController.getInstance().release();
    }

    public interface SelectionListener {
        void onSelectionChanged(int count);
    }

    /**
     * Callback will invoked when message item is clicked
     *
     * @param <MESSAGE>
     */
    public interface OnMsgClickListener<MESSAGE extends IChatMessage> {
        void onMessageClick(MESSAGE message);
    }

    /**
     * Callback will invoked when message item is long clicked
     *
     * @param <MESSAGE>
     */
    public interface OnMsgLongClickListener<MESSAGE extends IChatMessage> {
        void onMessageLongClick(MESSAGE message);
    }


    public interface OnAvatarClickListener<MESSAGE extends IChatMessage> {
        void onAvatarClick(MESSAGE message);
    }

    public interface OnMsgResendListener<MESSAGE extends IChatMessage> {
        void onMessageResend(MESSAGE message);
    }
}
