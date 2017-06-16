package com.bet007.mobile.score.adapter.qiuyou;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bet007.mobile.score.interfaces.BitmapLoader;
import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.bet007.mobile.score.widget.BubbleImageView;
import com.lqr.R;

/**
 * 聊天列表中图片消息ViewHolder
 *
 * @param <MESSAGE>
 */
public class ChatItemPicViewHolder<MESSAGE extends IChatMessage>
        extends BaseChatItemViewHolder<MESSAGE> {


    protected View layoutContent;
    protected BubbleImageView bubbleImageView;
    protected TextView tvProgress;
    protected View viewProgress;
    private int mMaxWidth;
    private int mMaxHeight;


    public ChatItemPicViewHolder(View itemView, boolean isSender) {
        super(itemView, isSender);
        layoutContent = getView(R.id.content_layout_text);
        bubbleImageView = getView(R.id.biv_content_pic);
        tvProgress = getView(R.id.tv_pic_progress);
        viewProgress = getView(R.id.view_pic_mask);

        ivUnread.setVisibility(View.GONE);
        getView(R.id.tv_action).setVisibility(View.GONE);
        getView(R.id.content_layout_record).setVisibility(View.GONE);
        getView(R.id.content_layout_text).setVisibility(View.GONE);

    }

    @Override
    public void onBind(final MESSAGE message) {
        super.onBind(message);

//        setPictureScale(message.getContentText(), bubbleImageView);
//        ViewGroup.LayoutParams params = bubbleImageView.getLayoutParams();
//        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//        int maxWidth = dm.widthPixels / 2;
//        int maxHeight = dm.heightPixels / 3;
//        Bitmap bitmap = BitmapLoader.getCompressBitmap(message.getContentText(), maxWidth, maxHeight, mDensity);
//        if (bitmap != null) {
//            params.width = bitmap.getWidth();
//            params.height = bitmap.getHeight();
//            bubbleImageView.setLayoutParams(params);
//            bubbleImageView.setImageBitmap(bitmap);
//        }

        bubbleImageView.setImageResource(R.drawable.tls);

        bubbleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMsgClickListener != null) {
                    mMsgClickListener.onMessageClick(message);
                }
            }
        });

        bubbleImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMsgLongClickListener != null) {
                    mMsgLongClickListener.onMessageLongClick(message);
                }
                return true;
            }
        });

        tvProgress.setVisibility(View.INVISIBLE);
        tvMaster.setVisibility(View.INVISIBLE);

        if (mIsSender) {
            switch (message.getStateCode()) {
                case IChatMessage.STATE_CREATED:
                case IChatMessage.STATE_SUCCEED:
                    break;
                case IChatMessage.STATE_FAILED:

                    break;
                case IChatMessage.STATE_GOING:
                    tvProgress.setVisibility(View.INVISIBLE);
                    tvMaster.setVisibility(View.INVISIBLE);
                    break;
            }
        }


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

    /**
     * @param path      photo file path
     * @param imageView Image view to display the picture.
     */
    private void setPictureScale(String path, ImageView imageView) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        if (imageWidth < 100 * mDensity) {
            imageHeight = imageHeight * (100 * mDensity / imageWidth);
            imageWidth = 100 * mDensity;
        }
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);
    }

}