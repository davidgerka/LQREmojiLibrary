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
public class ChatSceneActivity extends BaseActivity implements IEmotionSelectedListener, IEmotionKeyboardListener, ViewClick {
    private static final String TAG = ChatSceneActivity.class.getSimpleName();
    private static final int ITEM_SMOOTH_COUNT = 10;
    private static final int ITEM_GAP_COUNT = 5;
    private static final int ADJUST_VIEW_GAP = 150; // 输入法弹出来后，间隔多久去调整/操作ui，单位：毫秒
    public final static int RECORD_VOICE_TIPS = 20018; // 聊天界面，录音提示消息

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

    private ChatSceneActivity _this;
    private Application mApplication;
    private String userId = "kaka";
    private String targetId = "gerrard";
    private SoundRecorder mSensor = new SoundRecorder();
    // 录音时的倒数计时view
    private View recodrPopupLayout;
    private View recordStatusLayout;
    private View recordLoadLayout;
    private ImageView recordVolumeView;
    private ImageView recordimageTipView;
    private TextView recordTextView;
    private TextView recordRemainTextView; // 还能说多少秒

    private boolean adjustFlag = false; // 记录adjustView是否有改变高度
    private int flag = 1; // 1：初始状态；2：录音状态；3：出错状态
    private boolean isShosrt = false; // 录音时间太短标志，少于1秒时，置为true
    private long startVoiceT, endVoiceT; // 录音开始、结束时间
    private String voiceName; // 录音文件名字，如： xxx.amr
    private Rect mRect = new Rect(); // 按住说话区域
    private int recordTime = 0; // 录音时间，用来显示倒计时
    private boolean isOnTime = false; // 开始录音时，会开启一个线程记录时间和刷新倒计时，这个是线程标志位，当它置为false时会结束线程
    private static final int HEIGHT_GAP = 200; // 录音时，Y轴滑动增量
    private static final int UPDATE_MIC_LEVEL_GAP = 200; // 更新录音音量等状态的时间间隔

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        mApplication = getApplication();
        setContentView(R.layout.activity_chat_scene);
        ButterKnife.bind(this);
        mSensor = new SoundRecorder();
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
        recordStatusLayout = (View) this.findViewById(R.id.voice_rcd_hint_rcding);
        recordLoadLayout = (View) this.findViewById(R.id.voice_rcd_hint_loading);
        recordVolumeView = (ImageView) this.findViewById(R.id.record_time);
        recordimageTipView = (ImageView) this.findViewById(R.id.record_imagetip);
        recordTextView = (TextView) this.findViewById(R.id.record_tip);
        recordRemainTextView = (TextView) findViewById(R.id.tv_remain_time);
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
            if (msg.what == RECORD_VOICE_TIPS) {
                if (msg.arg1 == 0) {
                    chatSceneActivity.recordTextView.setText(R.string.chatscene_record_slideup_cancel);
                } else {
                    String string = String.format(chatSceneActivity.getString(R.string.record_remain_time), String.valueOf(msg.arg2));
                    chatSceneActivity.recordRemainTextView.setText(string);
                    chatSceneActivity.recordRemainTextView.setVisibility(View.VISIBLE);
                    chatSceneActivity.recordTextView.setVisibility(View.INVISIBLE);
                }
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
        onTouchTalkEvent(v, event, recordLayout);
    }

    @Override
    public void ItemClick(Object item, String clickTag, String paras, View view) {

    }

    // 按下语音录制按钮时
    private void onTouchTalkEvent(View v, MotionEvent event, View recordLayout) {

        if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
            v.getGlobalVisibleRect(mRect);
            Rect rect = new Rect();
            recordLayout.getGlobalVisibleRect(rect);
            mRect.bottom = rect.bottom + 1500;
            mRect.top = mRect.top - HEIGHT_GAP;

            long k = SoundRecorder.getFreeDiskSpace();
            if (k == -1) {
                ToastUtil.showMessage(_this, getString(R.string.record_audio_fail_no_sdcard));
                flag = 3;
                return;
            } else if (k < 4 * 1024) {
                flag = 3;
                ToastUtil.showMessage(_this, getString(R.string.record_audio_fail_nomore_volume));
                return;
            }

            SoundRecorder.makeFolderWithName(SoundRecorder.VOICE_FOLDER); // 创建录音文件夹
            talkViewFreeFinish();
            recordimageTipView.setBackgroundResource(R.drawable.chatscene_record_free_cancel);
            recordTextView.setText(R.string.chatscene_record_slideup_cancel);
            recordTextView.setBackgroundColor(Color.TRANSPARENT);
            recordVolumeView.setBackgroundResource(R.drawable.chatscene_mic_0);
            recordStatusLayout.setVisibility(View.INVISIBLE);
            recordLoadLayout.setVisibility(View.VISIBLE);
            recordimageTipView.setVisibility(View.INVISIBLE);
            recordVolumeView.setVisibility(View.INVISIBLE);
            recordTextView.setVisibility(View.INVISIBLE);
            recordRemainTextView.setVisibility(View.INVISIBLE);
            recodrPopupLayout.setVisibility(View.VISIBLE);

            mWeakHandler.postDelayed(new Runnable() {
                public void run() {
                    stopPlayVoice();
                    if (!isShosrt) {
                        recordLoadLayout.setVisibility(View.INVISIBLE);
                        recordStatusLayout.setVisibility(View.VISIBLE);
                        recordimageTipView.setVisibility(View.INVISIBLE);
                        recordVolumeView.setVisibility(View.VISIBLE);
                        recordTextView.setVisibility(View.VISIBLE);
                    }
                }
            }, 0);

            voiceName = SoundRecorder.createVoiceName(userId, targetId);
            Log.i(TAG, "----------------voiceName = " + voiceName);
            try {
                start(voiceName);
                SoundRecorder.muteAudioFocus(mApplication, true);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showMessage(_this, getString(R.string.record_audio_fail_check_perssion));
                talkViewInit();
                flag = 1;
                mWeakHandler.postDelayed(new Runnable() {
                    public void run() {
                        recodrPopupLayout.setVisibility(View.INVISIBLE);
                    }
                }, 200);
                SoundRecorder.muteAudioFocus(mApplication, false);
                return;
            }
            startVoiceT = System.currentTimeMillis();
            recordTime = 0;
            isOnTime = true;
            ClassCut localClassCut = new ClassCut();
            new Thread(localClassCut).start();
            flag = 2;
        } else if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {// 松开手势时执行录制完成

            isOnTime = false;
            if (flag == 3) {
                flag = 1;
                return;
            }

            talkViewInit();
            if (flag == 2) {
                flag = 1;

                int x = (int) event.getRawX();    //屏幕位置
                int y = (int) event.getRawY();
                if (mRect.contains(x, y)) {
                    stop();
                    endVoiceT = System.currentTimeMillis();

                    int time = (int) ((endVoiceT - startVoiceT) / 1000);
                    if (time > SoundRecorder.RECORD_TIME_MAX)
                        time = SoundRecorder.RECORD_TIME_MAX;
                    if (time < SoundRecorder.RECORD_TIME_MIN) { // 时间太短
                        isShosrt = true;
                        recordVolumeView.setVisibility(View.INVISIBLE);
                        recordimageTipView.setBackgroundResource(R.drawable.chatscene_record_tooshort);
                        recordimageTipView.setVisibility(View.VISIBLE);
                        recordTextView.setText(R.string.record_time_short);
                        recordTextView.setBackgroundColor(Color.TRANSPARENT);
                        mWeakHandler.postDelayed(new Runnable() {

                            public void run() {
                                recodrPopupLayout.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);
                        return;
                    }

                    File file = new File(SoundRecorder.getVoicePathWithName(voiceName));
                    Log.i(TAG, "--------------------file.size = " + file.length());
                    if (file == null || file.length() < SoundRecorder.RECORD_AUDIO_FILE_MIN_SIZE) {
                        file.delete();
                        file = null;
                        mWeakHandler.post(new Runnable() {
                            public void run() {
                                recodrPopupLayout.setVisibility(View.GONE);
                                ToastUtil.showMessage(_this, getString(R.string.record_audio_fail_check_perssion));
                            }
                        });
                        return;
                    }

                    sendVoiceMsg(time);
                    recodrPopupLayout.setVisibility(View.GONE);
                } else { // 手势按下的位置不在语音录制按钮的范围内
                    recodrPopupLayout.setVisibility(View.GONE);
                    stop();
                    File file = new File(SoundRecorder.getVoicePathWithName(voiceName));
                    if (file != null && file.exists()) {
                        file.delete();
                        file = null;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (flag == 2) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (mRect.contains(x, y)) { //
                    recordVolumeView.setVisibility(View.VISIBLE);
                    recordimageTipView.setVisibility(View.INVISIBLE);
                    int k = SoundRecorder.RECORD_TIME_MAX - recordTime;
                    if (k <= SoundRecorder.RECORD_TIME_TIPS) {
                        String string = String.format(getString(R.string.record_remain_time), String.valueOf(k));
                        recordRemainTextView.setText(string);
                    } else {
                        recordTextView.setText(R.string.chatscene_record_slideup_cancel);
                    }
                    recordTextView.setBackgroundColor(Color.TRANSPARENT);
                    talkViewFreeFinish();
                } else { // 手指的位置不在语音录制按钮的范围内
                    recordVolumeView.setVisibility(View.INVISIBLE);
                    recordimageTipView.setVisibility(View.VISIBLE);
                    recordTextView.setText(R.string.record_free_cancel);
                    recordTextView.setBackgroundColor(getResources().getColor(R.color.record_tip_bgcolor));
                    talkViewFreeCancel();
                }

            } else {
                talkViewInit();
            }
        }
    }

    class ClassCut implements Runnable {
        ClassCut() {
        }

        public void run() {
            while (isOnTime) {

                if (recordTime >= SoundRecorder.RECORD_TIME_MAX) { // 超过时长，自动发送
                    isOnTime = false;
                    recordTime = 0;
                    if (flag == 2) { // 录音状态，强制完成录音并发送
                        flag = 1;
                        stop();

                        File file = new File(SoundRecorder.getVoicePathWithName(voiceName));
                        if (file == null || file.length() < SoundRecorder.RECORD_AUDIO_FILE_MIN_SIZE) {
                            file.delete();
                            file = null;
                            mWeakHandler.post(new Runnable() {
                                public void run() {
                                    recodrPopupLayout.setVisibility(View.GONE);
                                    talkViewInit();
                                    ToastUtil.showMessage(_this, getString(R.string.record_audio_fail_check_perssion));
                                }
                            });
                        } else {
                            endVoiceT = System.currentTimeMillis();
                            int voiceTime = (int) ((endVoiceT - startVoiceT) / 1000L);
                            if (voiceTime > SoundRecorder.RECORD_TIME_MAX)
                                voiceTime = SoundRecorder.RECORD_TIME_MAX;
                            sendVoiceMsg(voiceTime);

                            mWeakHandler.post(new Runnable() {
                                public void run() {
                                    recodrPopupLayout.setVisibility(View.GONE);
                                    String string = String.format(getString(R.string.record_audio_maxtime),
                                            String.valueOf(SoundRecorder.RECORD_TIME_MAX));
                                    ToastUtil.showMessage(_this, string);
                                    talkViewInit();
                                }
                            });
                        }

                    }
                } else {
                    try {
                        Thread.sleep(100L);
                        int time = (int) ((System.currentTimeMillis() - startVoiceT) / 1000L);
                        if (time > SoundRecorder.RECORD_TIME_MAX || time < 0) {
                            time = 0;
                        }
                        if (recordTime != time) {
                            recordTime = time;
                            int k = SoundRecorder.RECORD_TIME_MAX - recordTime; // 剩余多少秒录音时间
                            if (k <= SoundRecorder.RECORD_TIME_TIPS) {
                                mWeakHandler.obtainMessage(RECORD_VOICE_TIPS, 1, k).sendToTarget();
                            } else {
                                mWeakHandler.obtainMessage(RECORD_VOICE_TIPS, 0, 0).sendToTarget();
                            }
                        }
                    } catch (InterruptedException localInterruptedException) {
                    }
                }
            }

            isOnTime = false;
            recordTime = 0;
        }
    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getLevel();
            updateRecordVolume(amp);
            if (flag == 2) {
                mWeakHandler.postDelayed(mPollTask, UPDATE_MIC_LEVEL_GAP);
            }
        }
    };

    /**
     * 开始录音
     */
    private void start(String name) throws Exception {
        if (mSensor == null) {
            mSensor = new SoundRecorder();
        }
        mSensor.start(name);
        Log.i(TAG, "----------------postDelayed");
        mWeakHandler.postDelayed(mPollTask, UPDATE_MIC_LEVEL_GAP);
    }

    /**
     * 停止录音
     */
    private void stop() {
        mWeakHandler.removeCallbacks(mSleepTask);
        mWeakHandler.removeCallbacks(mPollTask);
        if (mSensor != null) {
            mSensor.stop();
        }
        SoundRecorder.muteAudioFocus(mApplication, false);
    }

    /**
     * 按住说话，初始状态
     */
    private void talkViewInit() {
        mEmotionMainFragment.talkViewInit();
    }

    /**
     * 按住说话，录音状态，松开结束
     */
    private void talkViewFreeFinish() {
        mEmotionMainFragment.talkViewFreeFinish();
    }

    /**
     * 按住说话，录音状态，松开取消
     */
    private void talkViewFreeCancel() {
        mEmotionMainFragment.talkViewFreeCancel();
    }

    /**
     * 更新录音音量图片
     *
     * @param signalEMA 音量值
     * @return
     */
    private boolean updateRecordVolume(double signalEMA) {
        // Log.w("-------signalEMA = " + signalEMA);

        switch ((int) signalEMA) {
            case 1:
                recordVolumeView.setImageResource(R.drawable.chatscene_mic_0);
                break;
            case 2:
                recordVolumeView.setImageResource(R.drawable.chatscene_mic_1);
                break;
            case 3:
                recordVolumeView.setImageResource(R.drawable.chatscene_mic_2);
                break;
            case 4:
                recordVolumeView.setImageResource(R.drawable.chatscene_mic_3);
                break;
            case 5:
                recordVolumeView.setImageResource(R.drawable.chatscene_mic_4);
                break;
            default:
                recordVolumeView.setImageResource(R.drawable.chatscene_mic_5);
                break;
        }
        return true;
    }

    /**
     * 停止播放声音，刷新列表
     */
    private void stopPlayVoice() {
//        VoiceUtil.stopVoice();
//        if (mAdapter != null && mAdapter.getMsgId() != SoundRecorder.VOICE_MSG_DEFAULT_ID) {
//            mAdapter.setMsgVoiceId(SoundRecorder.VOICE_MSG_DEFAULT_ID);
//            mAdapter.notifyDataSetChanged();
//            mAdapter.abandonAudioFoucs();
//        }
    }

    /**
     * 发送语音消息
     */
    private void sendVoiceMsg(int voiceTime) {
        String voicePath = SoundRecorder.getVoicePathWithName(voiceName);
        mWebChatManager.addRecordToList(voicePath, voiceTime);
        mDataAdapter.notifyDataSetChanged();
        adjustKeyboardForSmooth();
    }

    
    @Override
    public void onPause() {
        super.onPause();
//        InputMethodUtil.hideInputMethod(_this);
        isOnTime = false;
        recordTime = 0;
        flag = 1;
        stop();
        recodrPopupLayout.setVisibility(View.INVISIBLE);
//        stopPlayVoice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSensor != null) {
            mSensor.destroy();
        }
        RecordHolderController.getInstance(getApplicationContext()).destroy();
    }
}
