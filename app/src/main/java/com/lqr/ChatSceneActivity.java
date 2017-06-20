package com.lqr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bet007.library.chatsceneinputlibrary.fragment.EmotionMainFragment;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionKeyboardListener;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionSelectedListener;
import com.bet007.library.chatsceneinputlibrary.listener.IImageLoader;
import com.bet007.library.chatsceneinputlibrary.utils.LQREmotionKit;
import com.bet007.mobile.score.adapter.qiuyou.ChatMessageAdapter;
import com.bet007.mobile.score.adapter.qiuyou.RecordHolderController;
import com.bet007.mobile.score.common.ChatSceneRecordUtil;
import com.bet007.mobile.score.common.SoundRecorder;
import com.bet007.mobile.score.common.ToastUtil;
import com.bet007.mobile.score.interfaces.ImageLoader;
import com.bet007.mobile.score.interfaces.ViewClick;
import com.bet007.mobile.score.manager.qiuyou.WebChatManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 会话界面
 */
public class ChatSceneActivity extends BaseActivity implements IEmotionSelectedListener, IEmotionKeyboardListener,
        ViewClick, ChatSceneRecordUtil.RecordCallback {
    private static final String TAG = ChatSceneActivity.class.getSimpleName();
    private static final int ITEM_SMOOTH_COUNT = 10;
    private static final int ITEM_GAP_COUNT = 5;
    private static final int ADJUST_VIEW_GAP = 150; // 输入法弹出来后，间隔多久去调整/操作ui，单位：毫秒

    @Bind(R.id.flEmotionView)
    FrameLayout mFlEmotionView;
    @Bind(R.id.recyclerview)
    LRecyclerView mLRecyclerView;
    private EmotionMainFragment mEmotionMainFragment;
    private WebChatManager mWebChatManager = new WebChatManager();
    private ChatMessageAdapter mDataAdapter = null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;
    private WeakHandler mWeakHandler = new WeakHandler(this);
    private ChatSceneRecordUtil mChatSceneRecordUtil;

    private ChatSceneActivity _this;
    private Application mApplication;
    private View recodrPopupLayout;//录音时提示layout

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        mApplication = getApplication();
        setContentView(R.layout.activity_chat_scene);
        ButterKnife.bind(this);
        RecordHolderController.getInstance(this);
        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            }
        });

        mWebChatManager.generateDataList();
        findViews();
        initEmotionMainFragment();
        initList();
        mChatSceneRecordUtil = new ChatSceneRecordUtil(this);
        mChatSceneRecordUtil.setViews(mEmotionMainFragment, recodrPopupLayout);
        mChatSceneRecordUtil.setRecordCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mEtContent.clearFocus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        setIntent(intent);
//        processExtraData();
//        processData();
//        initView();
//        initList();
    }

    private void findViews(){
        recodrPopupLayout = findViewById(R.id.layout_record_popup);
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
                if(state == RecyclerView.SCROLL_STATE_DRAGGING){
                    if(mEmotionMainFragment != null){
                        mEmotionMainFragment.hideAllKeyboard();
                    }
                }
            }

        });

        mLRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if (mEmotionMainFragment != null) {
                        mEmotionMainFragment.hideAllKeyboard();
                    }
                }
                return false;
            }
        });

        mDataAdapter.setDataListPointer(mWebChatManager.getList());
        adjustKeyboardForSmooth();
    }

    private void startPostAdjustListview(){
        mWeakHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                adjustKeyboardForSmooth();
            }
        }, ADJUST_VIEW_GAP);
    }

    /**
     * 判断条件，滚动列表到底部
     */
    private void adjustKeyboardForSmooth() {
        int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        int visibleItemCount = mLinearLayoutManager.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        if(totalItemCount > visibleItemCount){  //布满整个RecyclerView
            // 离底部隔ITEM_SMOOTH_COUNT条信息才能有滚动动作
            if(totalItemCount - lastVisibleItemPosition < ITEM_SMOOTH_COUNT){
                scrollToBottom(true);
            }else {
                scrollToBottom(false);
            }
        }else {
            scrollToTop(false);
        }
    }

    /**
     * 滚动到底部
     */
    private void scrollToBottom(boolean isSmooth){
        if (isSmooth) {
            mLRecyclerView.smoothScrollToPosition(mWebChatManager.getList().size());
        } else {
            mLRecyclerView.scrollToPosition(mWebChatManager.getList().size());

        }
    }

    /**
     * 滚动到顶部
     */
    private void scrollToTop(boolean isSmooth){
        if (isSmooth) {
            mLRecyclerView.smoothScrollToPosition(0);
        } else {
            mLRecyclerView.scrollToPosition(0);

        }
    }

    @Override
    public void completeRecord(int duration, String filePath, String fileName) {
//        String voicePath = SoundRecorder.getVoicePathWithName(fileName);
        mWebChatManager.addRecordToList(filePath, duration);
        mDataAdapter.notifyDataSetChanged();
        adjustKeyboardForSmooth();
    }

    public static class WeakHandler extends Handler {
        WeakReference<ChatSceneActivity> mWeakReference;

        public WeakHandler(ChatSceneActivity chatActivity){
            mWeakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatSceneActivity chatSceneActivity = mWeakReference.get();
            if(chatSceneActivity == null){
                return;
            }
        }
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
        startPostAdjustListview();
    }

    @Override
    public void onHideInputMethod() {

    }

    @Override
    public void onShowEmotionKeyboard() {
        mEmotionMainFragment.showEmotionKeyboard();
        startPostAdjustListview();
    }

    @Override
    public void onShowRecordLayout() {
        mEmotionMainFragment.showRecordLayout();
        startPostAdjustListview();
    }

    @Override
    public void onSendTextMsg(String content) {
        mWebChatManager.addTextToList(content);
        mDataAdapter.notifyDataSetChanged();
        adjustKeyboardForSmooth();
    }

    @Override
    public void onListViewSmooth() {
        startPostAdjustListview();
    }

    @Override
    public void onTouchRecordEvent(View v, MotionEvent event, View recordLayout) {
        if(mChatSceneRecordUtil != null){
            mChatSceneRecordUtil.onTouchTalkEvent(v, event, recordLayout);
        }
    }

    @Override
    public void ItemClick(Object item, String clickTag, String paras, View view) {

    }

    @Override
    public void onPause() {
        super.onPause();
//        InputMethodUtil.hideInputMethod(_this);
        if(mChatSceneRecordUtil != null){
            mChatSceneRecordUtil.pause();
        }
        RecordHolderController.getInstance(getApplicationContext()).stopAndResetHolder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatSceneRecordUtil != null){
            mChatSceneRecordUtil.destroy();
        }
        RecordHolderController.getInstance(getApplicationContext()).destroy();
    }
}
