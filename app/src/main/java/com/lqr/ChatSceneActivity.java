package com.lqr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bet007.library.chatsceneinputlibrary.fragment.EmotionMainFragment;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionKeyboardListener;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionSelectedListener;
import com.bet007.library.chatsceneinputlibrary.listener.IImageLoader;
import com.bet007.library.chatsceneinputlibrary.utils.LQREmotionKit;
import com.bet007.mobile.score.adapter.qiuyou.ChatMessageAdapter;
import com.bet007.mobile.score.interfaces.ImageLoader;
import com.bet007.mobile.score.interfaces.ViewClick;
import com.bet007.mobile.score.manager.qiuyou.WebChatManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 会话界面
 */
public class ChatSceneActivity extends BaseActivity implements IEmotionSelectedListener, IEmotionKeyboardListener, ViewClick {
    private static final String TAG = ChatSceneActivity.class.getSimpleName();

    @Bind(R.id.flEmotionView)
    FrameLayout mFlEmotionView;
    @Bind(R.id.recyclerview)
    LRecyclerView mLRecyclerView;
    private EmotionMainFragment mEmotionMainFragment;
    private WebChatManager mWebChatManager = new WebChatManager();
    private ChatMessageAdapter mDataAdapter = null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_scene);
        ButterKnife.bind(this);

        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            }
        });

        initEmotionMainFragment();

        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mEtContent.clearFocus();
    }

    /**
     * 初始化表情面板
     */
    private void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT, true);
        //隐藏控件
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN, false);
        //替换fragment
        //创建修改实例
        mEmotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class, bundle);
        mEmotionMainFragment.bindToContentView(mLRecyclerView);
        mEmotionMainFragment.setOnEmotionKeyboardListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flEmotionView, mEmotionMainFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }

    private void initList() {
        mDataAdapter = new ChatMessageAdapter(this, this, new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {

            }

            @Override
            public void loadImage(ImageView imageView, String string) {

            }
        });
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mLRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mLRecyclerView.setShowResultHeader(false);  //不显示头部结果view

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLRecyclerView.setLoadMoreEnabled(false);
        mLRecyclerView.setHasFixedSize(true);
        mLRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        mLRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        mLRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });


        mLRecyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }


            @Override
            public void onScrolled(int distanceX, int distanceY) {


            }

            @Override
            public void onScrollStateChanged(int state) {
            }

        });

        mDataAdapter.setDataListPointer(mWebChatManager.getList());
    }

    @Override
    public void onBackPressed() {
        /**
         * 判断是否拦截返回键操作
         */
        if (!mEmotionMainFragment.isInterceptBackPress()) {
            finish();
        }
    }

    @Override
    public void onEmojiSelected(String key) {
        Log.e(TAG, "onEmojiSelected : " + key);
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
        Log.e(TAG, "stickerBitmapPath : " + stickerBitmapPath);
    }

    @Override
    public void onShowInputMethod() {
        mEmotionMainFragment.showInputMethod();
    }

    @Override
    public void onHideInputMethod() {

    }

    @Override
    public void onShowEmotionKeyboard() {
        mEmotionMainFragment.showEmotionKeyboard();
    }

    @Override
    public void onShowRecordLayout() {
        mEmotionMainFragment.showRecordLayout();
    }

    @Override
    public void onSendTextMsg(String content) {

    }

    @Override
    public void onListViewSmooth() {

    }

    @Override
    public void onTouchRecordEvent(View v, MotionEvent event, View recordLayout) {

    }

    @Override
    public void ItemClick(Object item, String clickTag, String paras, View view) {

    }
}
