package com.bet007.library.chatsceneinputlibrary.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;


import com.bet007.library.chatsceneinputlibrary.R;
import com.bet007.library.chatsceneinputlibrary.fragment.EmotiomFragment;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionSelectedListener;
import com.bet007.library.chatsceneinputlibrary.model.StickerCategory;
import com.bet007.library.chatsceneinputlibrary.model.StickerItem;
import com.bet007.library.chatsceneinputlibrary.utils.DisplayUtils;
import com.bet007.library.chatsceneinputlibrary.utils.EmojiManager;
import com.bet007.library.chatsceneinputlibrary.utils.LQREmotionKit;
import com.bet007.library.chatsceneinputlibrary.utils.MoonUtils;
import com.bet007.library.chatsceneinputlibrary.utils.ScreenUtils;
import com.bet007.library.chatsceneinputlibrary.utils.StickerManager;

import java.util.List;


/**
 * 表情控件的ViewPager适配器(emoji + 贴图)
 */

public class EmotionViewPagerAdapter extends PagerAdapter {
    private static final String TAG = EmotionViewPagerAdapter.class.getSimpleName();
    public static final String DELETE_KEY = "/DEL"; //删除键

    private static final int EMOTION_MARGIN_PADDING = 1;     //表情两边的padding，单位为dp，计算时要乘以屏幕密度
    private static final int EMOTION_MARGIN_LANDSCAPE = 6;   //表情左右间隔，单位为dp，计算时要乘以屏幕密度
    private static final int EMOTION_MARGIN_VERTICAL = 16;   //表情上下间隔，单位为dp，计算时要乘以屏幕密度
    private static final int STICKER_MARGIN_VERTICAL = 6;   //sticker表情上下间隔，单位为dp，计算时要乘以屏幕密度
    private Context mContext;
    private int mPageCount = 0;
    private int mEmotionType = 0;   //表情类型
    private EditText mMessageEditText;
    // item的间距
    private int mSpacingPadding;  //padding
    private int mSpacingLandscape;  //横向距离
    private int mSpacingVertical;  //竖向距离
    private int mEmotionWidth;
    private int mEmotionHeight;
    private IEmotionSelectedListener listener;

    public AdapterView.OnItemClickListener emojiListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int index = position + (Integer) parent.getTag() * EmotiomFragment.EMOJI_PER_PAGE;
            int count = EmojiManager.getDisplayCount();
            if (position == EmotiomFragment.EMOJI_PER_PAGE || index >= count) {
                if (listener != null) {
                    listener.onEmojiSelected(DELETE_KEY);
                }
                onEmojiSelected(DELETE_KEY);
            } else {
                String text = EmojiManager.getDisplayText((int) id);
                if (!TextUtils.isEmpty(text)) {
                    if (listener != null) {
                        listener.onEmojiSelected(text);
                    }
                    onEmojiSelected(text);
                }
            }
        }
    };
    public AdapterView.OnItemClickListener stickerListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mEmotionType - 1);
            List<StickerItem> stickers = category.getStickers();
            int index = position + (Integer) parent.getTag() * EmotiomFragment.STICKER_PER_PAGE;

            if (index >= stickers.size()) {
                Log.i(TAG, "index " + index + " larger than size " + stickers.size());
                return;
            }

            if (listener != null) {
                StickerItem sticker = stickers.get(index);
                StickerCategory real = StickerManager.getInstance().getCategory(sticker.getCategory());

                if (real == null) {
                    return;
                }

                listener.onStickerSelected(sticker.getCategory(), sticker.getName(), StickerManager.getInstance().getStickerBitmapPath(sticker.getCategory(), sticker.getName()));
            }
        }
    };

    public EmotionViewPagerAdapter(Context context, int emotionType, IEmotionSelectedListener listener) {
        mContext = context;
        mEmotionType = emotionType;
        int screenWidth = ScreenUtils.getScreenWidth(context);

        if (mEmotionType == 0) {
            mPageCount = (int) Math.ceil(EmojiManager.getDisplayCount() / (float) EmotiomFragment.EMOJI_PER_PAGE);
            mSpacingPadding = DisplayUtils.dp2px(context, EMOTION_MARGIN_PADDING);
            mSpacingLandscape = DisplayUtils.dp2px(context, EMOTION_MARGIN_LANDSCAPE);
            mSpacingVertical = DisplayUtils.dp2px(context, EMOTION_MARGIN_VERTICAL);
            mEmotionWidth = (screenWidth - 2 * mSpacingPadding - mSpacingLandscape * (EmotiomFragment.EMOJI_COLUMNS - 1)) / EmotiomFragment.EMOJI_COLUMNS;
            mEmotionHeight = mEmotionWidth;
        } else {

            mPageCount = (int) Math.ceil(StickerManager.getInstance().getStickerCategories().get(mEmotionType - 1).getStickers().size() / (float) EmotiomFragment.STICKER_PER_PAGE);
            mSpacingPadding = DisplayUtils.dp2px(context, EMOTION_MARGIN_PADDING);
            mSpacingLandscape = DisplayUtils.dp2px(context, EMOTION_MARGIN_LANDSCAPE);
            mSpacingVertical = DisplayUtils.dp2px(context, STICKER_MARGIN_VERTICAL);
            mEmotionWidth = (screenWidth - 2 * mSpacingPadding - mSpacingLandscape * (EmotiomFragment.STICKER_COLUMNS - 1)) / EmotiomFragment.STICKER_COLUMNS;
            mEmotionHeight = mEmotionWidth;

        }

        this.listener = listener;
    }

    public void attachEditText(EditText messageEditText) {
        mMessageEditText = messageEditText;
    }

    @Override
    public int getCount() {
        return mPageCount == 0 ? 1 : mPageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Context context = container.getContext();
        RelativeLayout rl = new RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);

        GridView gridView = new GridView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        gridView.setLayoutParams(params);
        gridView.setGravity(Gravity.CENTER);

        gridView.setTag(position);//标记自己是第几页
        gridView.setPadding(mSpacingPadding, mSpacingPadding, mSpacingPadding, mSpacingPadding);
        gridView.setHorizontalSpacing(mSpacingLandscape);
        gridView.setVerticalSpacing(mSpacingVertical);
        gridView.setSelector(android.R.color.transparent);
        if (mEmotionType == 0) {
            gridView.setOnItemClickListener(emojiListener);
            gridView.setAdapter(new EmojiAdapter(context, mEmotionWidth, mEmotionHeight, position * EmotiomFragment.EMOJI_PER_PAGE));
            gridView.setNumColumns(EmotiomFragment.EMOJI_COLUMNS);
        } else {
            StickerCategory category = StickerManager.getInstance().getCategory(StickerManager.getInstance().getStickerCategories().get(mEmotionType - 1).getName());
            gridView.setOnItemClickListener(stickerListener);
            gridView.setAdapter(new StickerAdapter(context, category, mEmotionWidth, mEmotionHeight, position * EmotiomFragment.STICKER_PER_PAGE));
            gridView.setNumColumns(EmotiomFragment.STICKER_COLUMNS);
        }

        rl.addView(gridView);
        container.addView(rl);
        return rl;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void onEmojiSelected(String key) {
        if (mMessageEditText == null)
            return;
        Editable editable = mMessageEditText.getText();
        if (key.equals(DELETE_KEY)) {
            mMessageEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = mMessageEditText.getSelectionStart();
            int end = mMessageEditText.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            editable.replace(start, end, key);

            int editEnd = mMessageEditText.getSelectionEnd();
            MoonUtils.replaceEmoticons(LQREmotionKit.getContext(), editable, 0, editable.toString().length());
            mMessageEditText.setSelection(editEnd);
        }
    }
}
