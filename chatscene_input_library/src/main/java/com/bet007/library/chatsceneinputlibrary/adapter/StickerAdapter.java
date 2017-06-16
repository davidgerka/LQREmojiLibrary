package com.bet007.library.chatsceneinputlibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bet007.library.chatsceneinputlibrary.R;
import com.bet007.library.chatsceneinputlibrary.fragment.EmotiomFragment;
import com.bet007.library.chatsceneinputlibrary.model.StickerCategory;
import com.bet007.library.chatsceneinputlibrary.model.StickerItem;
import com.bet007.library.chatsceneinputlibrary.utils.LQREmotionKit;
import com.bet007.library.chatsceneinputlibrary.utils.StickerManager;


/**
 * 贴图表情适配器
 */

public class StickerAdapter extends BaseAdapter {
    private static final float FACTOR = 0.75f;
    private Context mContext;
    private StickerCategory mCategory;
    private int startIndex;

    private float mPerWidth;
    private float mPerHeight;
    private float mIvSize;

    public StickerAdapter(Context context, StickerCategory category, int emotionWidth, int emotionHeight, int startIndex) {
        mContext = context;
        mCategory = category;
        this.startIndex = startIndex;

        mPerHeight = emotionWidth;
        mPerWidth = emotionHeight;
        float ivWidth = emotionWidth * FACTOR;
        float ivHeight = emotionHeight * FACTOR;
        mIvSize = Math.min(ivWidth, ivHeight);
    }


    @Override
    public int getCount() {
        int count = mCategory.getStickers().size() - startIndex;
        count = Math.min(count, EmotiomFragment.STICKER_PER_PAGE);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return mCategory.getStickers().get(startIndex + position);
    }

    @Override
    public long getItemId(int position) {
        return startIndex + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StickerViewHolder viewHolder;
        if (convertView == null) {
            RelativeLayout rl = new RelativeLayout(mContext);
            rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mPerHeight));

            ImageView imageView = new ImageView(mContext);
//            imageView.setImageResource(R.drawable.ic_tab_emoji);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.width = (int) mIvSize;
            params.height = (int) mIvSize;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(params);

            rl.addView(imageView);

            viewHolder = new StickerViewHolder();
            viewHolder.mImageView = imageView;

            convertView = rl;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StickerViewHolder) convertView.getTag();
        }

        int index = startIndex + position;
        if (index >= mCategory.getStickers().size()) {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }

        StickerItem sticker = mCategory.getStickers().get(index);
        if (sticker == null) {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }

        if(TextUtils.isEmpty(sticker.getCategory()) || TextUtils.isEmpty(sticker.getName())){
            convertView.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }
        String stickerBitmapUri = StickerManager.getInstance().getStickerBitmapUri(sticker.getCategory(), sticker.getName());
        LQREmotionKit.getImageLoader().displayImage(mContext, stickerBitmapUri, viewHolder.mImageView);
        if(!TextUtils.isEmpty(stickerBitmapUri)){
            convertView.setBackgroundResource(R.drawable.chatscene_emotion_bg_sel);
        }else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }


        return convertView;
    }

    class StickerViewHolder {
        public ImageView mImageView;
    }
}
