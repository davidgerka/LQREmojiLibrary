package com.bet007.library.chatsceneinputlibrary.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bet007.library.chatsceneinputlibrary.R;
import com.bet007.library.chatsceneinputlibrary.fragment.EmotiomFragment;
import com.bet007.library.chatsceneinputlibrary.utils.EmojiManager;


/**
 * emoji表情适配器
 */
public class EmojiAdapter extends BaseAdapter {
    private static final String TAG = EmojiAdapter.class.getSimpleName();
    private static final float FACTOR = 0.70f;
    private Context mContext;
    private int mStartIndex;
    private float mPerWidth;
    private float mPerHeight;
    private float mIvSize;

    public EmojiAdapter(Context context, int emotionWidth, int emotionHeight, int startIndex) {
        mContext = context;
        mStartIndex = startIndex;
        mPerHeight = emotionWidth;
        mPerWidth = emotionHeight;
        float ivWidth = emotionWidth * FACTOR;
        float ivHeight = emotionHeight * FACTOR;
        mIvSize = Math.min(ivWidth, ivHeight);
    }

    @Override
    public int getCount() {
        int count = EmojiManager.getDisplayCount() - mStartIndex + 1;
        count = Math.min(count, EmotiomFragment.EMOJI_PER_PAGE + 1);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mStartIndex + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rl = new RelativeLayout(mContext);
        rl.setLayoutParams(new AbsListView.LayoutParams((int)mPerWidth, (int) mPerHeight));

        ImageView emojiThumb = new ImageView(mContext);
        int count = EmojiManager.getDisplayCount();
        int index = mStartIndex + position;
        boolean flag = true;
        if (position == EmotiomFragment.EMOJI_PER_PAGE || index == count) {
            emojiThumb.setImageResource(R.drawable.ic_emoji_del);
        } else if (index < count) {
            Drawable drawable = EmojiManager.getDisplayDrawable(mContext, index);
            emojiThumb.setImageDrawable(drawable);
            if(drawable == null){
                flag = false;
            }
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.width = (int) mIvSize;
        layoutParams.height = (int) mIvSize;
        emojiThumb.setLayoutParams(layoutParams);

        if(flag){
            rl.setBackgroundResource(R.drawable.chatscene_emotion_bg_sel);
        }else {
            rl.setBackgroundColor(Color.TRANSPARENT);
        }
        rl.setGravity(Gravity.CENTER);
        rl.addView(emojiThumb);


        return rl;
    }
}
