package com.bet007.library.chatsceneinputlibrary.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bet007.library.chatsceneinputlibrary.R;
import com.bet007.library.chatsceneinputlibrary.adapter.EmotionViewPagerAdapter;
import com.bet007.library.chatsceneinputlibrary.emotionKeyboardView.EmojiIndicatorView;
import com.bet007.library.chatsceneinputlibrary.emotionKeyboardView.EmotionViewPager;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionExtClickListener;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionSelectedListener;
import com.bet007.library.chatsceneinputlibrary.model.StickerCategory;
import com.bet007.library.chatsceneinputlibrary.utils.EmojiManager;
import com.bet007.library.chatsceneinputlibrary.utils.StickerManager;


/**
 * 表情（emoji、贴图）Fragment
 */
public class EmotiomFragment extends BaseFragment {
    public static final int EMOJI_COLUMNS = 7;
    public static final int EMOJI_ROWS = 3;
    public static final int EMOJI_PER_PAGE = EMOJI_COLUMNS * EMOJI_ROWS - 1;//最后一个是删除键
    public static final int STICKER_COLUMNS = 4;
    public static final int STICKER_ROWS = 2;
    public static final int STICKER_PER_PAGE = STICKER_COLUMNS * STICKER_ROWS;
    private static final String TAG = EmotiomFragment.class.getSimpleName();
    private EmotionViewPager mEmotionViewPager;
    private EmojiIndicatorView mIndicatorView;//表情面板对应的点列表
    private int mEmotionType = 0;   //哪一类表情，目前只有一类
    private int mPageCount;     //该类表情总共有多少页

    private IEmotionSelectedListener mEmotionSelectedListener;
    private IEmotionExtClickListener mEmotionExtClickListener;
    private EditText mMessageEditText;


    /**
     * 创建与Fragment对象关联的View视图时调用
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatscene_emotion_layout, container, false);
        mEmotionType = args.getInt(FragmentFactory.EMOTION_TYPE);//获取类型
        initParams(mEmotionType);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initParams(int emotionType) {
        if (emotionType == 0) {
            mPageCount = (int) Math.ceil(EmojiManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
        } else {
            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mEmotionType - 1);
            mPageCount = (int) Math.ceil(category.getStickers().size() / (float) STICKER_PER_PAGE);
        }
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {
        mEmotionViewPager = (EmotionViewPager) rootView.findViewById(R.id.emotion_viewpager);
        mIndicatorView = (EmojiIndicatorView) rootView.findViewById(R.id.emoji_indicator_view);
        initEmotion(mEmotionType);
    }

    /**
     * 初始化表情面板
     */
    private void initEmotion(int emotionType) {
        //初始化指示器
        mIndicatorView.initIndicator(mPageCount);

        final EmotionViewPagerAdapter adapter = new EmotionViewPagerAdapter(getActivity(), emotionType, mEmotionSelectedListener);
        mEmotionViewPager.setAdapter(adapter);

        if (emotionType == 0) {
            adapter.attachEditText(mMessageEditText);
        }
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

        mEmotionViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndicatorView.playByStartPointToNext(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void setEmotionSelectedListener(IEmotionSelectedListener emotionSelectedListener) {
        if (emotionSelectedListener != null) {
            this.mEmotionSelectedListener = emotionSelectedListener;
        } else {
            Log.i(TAG, "IEmotionSelectedListener is null");
        }
    }


    public void setEmotionExtClickListener(IEmotionExtClickListener emotionExtClickListener) {
        if (emotionExtClickListener != null) {
            this.mEmotionExtClickListener = emotionExtClickListener;
        } else {
            Log.i(TAG, "IEmotionSettingTabClickListener is null");
        }
    }

    public void attachEditText(EditText messageEditText) {
        mMessageEditText = messageEditText;
    }


}
