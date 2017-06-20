package com.bet007.mobile.score.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bet007.library.chatsceneinputlibrary.fragment.EmotionMainFragment;
import com.lqr.R;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 聊天界面的录音工具类
 */

public class ChatSceneRecordUtil {
    public final static int RECORD_VOICE_TIPS = 20018; // 聊天界面，录音提示消息
    private static final String TAG = ChatSceneRecordUtil.class.getSimpleName();
    private static final int HEIGHT_GAP = 200; // 录音时，Y轴滑动增量
    private static final int UPDATE_MIC_LEVEL_GAP = 200; // 更新录音音量等状态的时间间隔
    private Context mContext;
    private EmotionMainFragment mEmotionMainFragment;
    // 录音时的倒数计时view
    private View recodrPopupLayout;
    private View recordStatusLayout;
    private View recordLoadLayout;
    private ImageView recordVolumeView;
    private ImageView recordimageTipView;
    private TextView recordTextView;
    private TextView recordRemainTextView; // 还能说多少秒
    private WeakHandler mWeakHandler;
    private SoundRecorder mSensor = new SoundRecorder();
    private int flag = 1; // 1：初始状态；2：录音状态；3：出错状态
    private boolean isInRecordRect = true;  //手指位置是否在录音有效区域
    private boolean isShosrt = false; // 录音时间太短标志，少于1秒时，置为true
    private long startVoiceT, endVoiceT; // 录音开始、结束时间
    private String voiceName; // 录音文件名字，如： xxx.amr
    private Rect mRect = new Rect(); // 按住说话区域
    private int recordTime = 0; // 录音时间，用来显示倒计时
    private boolean isOnTime = false; // 开始录音时，会开启一个线程记录时间和刷新倒计时，这个是线程标志位，当它置为false时会结束线程
    private String userId = "kaka";
    private String targetId = "gerrard";
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getLevel();
            updateRecordVolume(amp);
            if (flag == 2) {
                mWeakHandler.postDelayed(mPollTask, UPDATE_MIC_LEVEL_GAP);
            }
        }
    };
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stopRecord();
        }
    };

    public ChatSceneRecordUtil(Context context) {
        mContext = context;
        mWeakHandler = new WeakHandler(this);
    }

    public void setViews(EmotionMainFragment emotionMainFragment, View popupLayout) {
        if(mEmotionMainFragment == null || popupLayout == null){
            throw new NullPointerException("mEmotionMainFragment and popupLayout cannot be null");
        }
        mEmotionMainFragment = emotionMainFragment;
        recodrPopupLayout = popupLayout;
        recordStatusLayout = (View) popupLayout.findViewById(R.id.voice_rcd_hint_rcding);
        recordLoadLayout = (View) popupLayout.findViewById(R.id.voice_rcd_hint_loading);
        recordVolumeView = (ImageView) popupLayout.findViewById(R.id.record_time);
        recordimageTipView = (ImageView) popupLayout.findViewById(R.id.record_imagetip);
        recordTextView = (TextView) popupLayout.findViewById(R.id.record_tip);
        recordRemainTextView = (TextView) popupLayout.findViewById(R.id.tv_remain_time);
    }

    // 按下语音录制按钮时
    public void onTouchTalkEvent(View v, MotionEvent event, View recordLayout) {

        if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
            v.getGlobalVisibleRect(mRect);
            Rect rect = new Rect();
            recordLayout.getGlobalVisibleRect(rect);
            mRect.bottom = rect.bottom + 1500;
            mRect.top = mRect.top - HEIGHT_GAP;

            long k = SoundRecorder.getFreeDiskSpace();
            if (k == -1) {
                ToastUtil.showMessage(mContext, mContext.getString(R.string.record_audio_fail_no_sdcard));
                flag = 3;
                return;
            } else if (k < 4 * 1024) {
                flag = 3;
                ToastUtil.showMessage(mContext, mContext.getString(R.string.record_audio_fail_nomore_volume));
                return;
            }

            SoundRecorder.makeFolderWithName(SoundRecorder.VOICE_FOLDER); // 创建录音文件夹
            recordViewFreeFinish();
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
                startRecord(voiceName);
                SoundRecorder.muteAudioFocus(mContext.getApplicationContext(), true);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showMessage(mContext, mContext.getString(R.string.record_audio_fail_check_perssion));
                recordViewInit();
                flag = 1;
                mWeakHandler.postDelayed(new Runnable() {
                    public void run() {
                        recodrPopupLayout.setVisibility(View.INVISIBLE);
                    }
                }, 200);
                SoundRecorder.muteAudioFocus(mContext.getApplicationContext(), false);
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

            recordViewInit();
            if (flag == 2) {
                flag = 1;

                int x = (int) event.getRawX();    //屏幕位置
                int y = (int) event.getRawY();
                if (mRect.contains(x, y)) {
                    stopRecord();
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
                    if (file.length() < SoundRecorder.RECORD_AUDIO_FILE_MIN_SIZE) {
                        file.delete();
                        file = null;
                        mWeakHandler.post(new Runnable() {
                            public void run() {
                                recodrPopupLayout.setVisibility(View.GONE);
                                ToastUtil.showMessage(mContext, mContext.getString(R.string.record_audio_fail_check_perssion));
                            }
                        });
                        return;
                    }

                    sendVoiceMsg(time);
                    recodrPopupLayout.setVisibility(View.GONE);
                } else { // 手势按下的位置不在语音录制按钮的范围内
                    recodrPopupLayout.setVisibility(View.GONE);
                    stopRecord();
                    File file = new File(SoundRecorder.getVoicePathWithName(voiceName));
                    if (file.exists()) {
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
                    isInRecordRect = true;
                    recordVolumeView.setVisibility(View.VISIBLE);
                    recordimageTipView.setVisibility(View.INVISIBLE);
                    int k = SoundRecorder.RECORD_TIME_MAX - recordTime;
                    if (k <= SoundRecorder.RECORD_TIME_TIPS) {
                        String string = String.format(mContext.getString(R.string.record_remain_time), String.valueOf(k));
                        recordRemainTextView.setText(string);
                    } else {
                        recordTextView.setText(R.string.chatscene_record_slideup_cancel);
                    }
                    recordTextView.setBackgroundColor(Color.TRANSPARENT);
                    recordViewFreeFinish();
                } else { // 手指的位置不在语音录制按钮的范围内
                    isInRecordRect = false;
                    recordVolumeView.setVisibility(View.INVISIBLE);
                    recordimageTipView.setVisibility(View.VISIBLE);
                    recordTextView.setText(R.string.record_free_cancel);
                    recordTextView.setBackgroundColor(mContext.getResources().getColor(R.color.record_tip_bgcolor));
                    recordViewFreeCancel();
                }

            } else {
                recordViewInit();
            }
        }
    }

    /**
     * 停止播放声音，刷新列表
     */
    private void stopPlayVoice() {
        //这里不用处理了，有audio焦点监听来处理
    }

    /**
     * 开始录音
     */
    private void startRecord(String name) throws Exception {
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
    private void stopRecord() {
        mWeakHandler.removeCallbacks(mSleepTask);
        mWeakHandler.removeCallbacks(mPollTask);
        if (mSensor != null) {
            mSensor.stop();
        }
        SoundRecorder.muteAudioFocus(mContext.getApplicationContext(), false);
    }

    /**
     * 按住说话，初始状态
     */
    private void recordViewInit() {
        mEmotionMainFragment.recordViewInit();
    }

    /**
     * 按住说话，录音状态，松开结束
     */
    private void recordViewFreeFinish() {
        mEmotionMainFragment.recordViewFreeFinish();
    }

    /**
     * 按住说话，录音状态，松开取消
     */
    private void recordViewFreeCancel() {
        mEmotionMainFragment.recordViewFreeCancel();
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

    public static class WeakHandler extends Handler {
        WeakReference<ChatSceneRecordUtil> mWeakReference;

        public WeakHandler(ChatSceneRecordUtil chatActivity) {
            mWeakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatSceneRecordUtil chatSceneActivity = mWeakReference.get();
            if (chatSceneActivity == null) {
                return;
            }
            if (msg.what == RECORD_VOICE_TIPS) {
                if (msg.arg1 == 0) {
                    if (chatSceneActivity.isInRecordRect) {
                        chatSceneActivity.recordTextView.setText(R.string.chatscene_record_slideup_cancel);
                    } else {
                        chatSceneActivity.recordTextView.setText(R.string.record_free_cancel);
                    }
                } else {
                    String string = String.format(chatSceneActivity.mContext.getString(R.string.record_remain_time), String.valueOf(msg.arg2));
                    chatSceneActivity.recordRemainTextView.setText(string);
                    chatSceneActivity.recordRemainTextView.setVisibility(View.VISIBLE);
                    chatSceneActivity.recordTextView.setVisibility(View.INVISIBLE);
                }
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
                        stopRecord();

                        File file = new File(SoundRecorder.getVoicePathWithName(voiceName));
                        if (file == null || file.length() < SoundRecorder.RECORD_AUDIO_FILE_MIN_SIZE) {
                            file.delete();
                            file = null;
                            mWeakHandler.post(new Runnable() {
                                public void run() {
                                    recodrPopupLayout.setVisibility(View.GONE);
                                    recordViewInit();
                                    ToastUtil.showMessage(mContext, mContext.getString(R.string.record_audio_fail_check_perssion));
                                }
                            });
                        } else {
                            endVoiceT = System.currentTimeMillis();
                            int voiceTime = (int) ((endVoiceT - startVoiceT) / 1000L);
                            if (voiceTime > SoundRecorder.RECORD_TIME_MAX) {
                                voiceTime = SoundRecorder.RECORD_TIME_MAX;
                            }
                            final int time = voiceTime;
                            mWeakHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendVoiceMsg(time);
                                }
                            });


                            mWeakHandler.post(new Runnable() {
                                public void run() {
                                    recodrPopupLayout.setVisibility(View.GONE);
                                    String string = String.format(mContext.getString(R.string.record_audio_maxtime),
                                            String.valueOf(SoundRecorder.RECORD_TIME_MAX));
                                    ToastUtil.showMessage(mContext, string);
                                    recordViewInit();
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

    private void sendVoiceMsg(int voiceTime){
        if(mRecordCallback != null){
            String voicePath = SoundRecorder.getVoicePathWithName(voiceName);
            mRecordCallback.completeRecord(voiceTime, voicePath, voiceName);
        }
    }

    public void pause(){
        isOnTime = false;
        recordTime = 0;
        flag = 1;
        stopRecord();
        recodrPopupLayout.setVisibility(View.INVISIBLE);
    }

    public void destroy(){
        if (mSensor != null) {
            mSensor.destroy();
        }
    }

    private RecordCallback mRecordCallback;
    public void setRecordCallback(RecordCallback recordCallback){
        mRecordCallback = recordCallback;
    }
    public interface RecordCallback {
        void completeRecord(int duration, String filePath, String fileName);
    }

}
