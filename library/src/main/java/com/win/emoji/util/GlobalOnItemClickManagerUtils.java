package com.win.emoji.util;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.win.emoji.adapter.EmotionGridViewAdapter;
import com.win.emoji.emotionkeyboardview.OnEmotionClickListener;


/**
 * Description:点击表情的全局监听管理类
 */
public class GlobalOnItemClickManagerUtils {
    public static final int CHAT_TEXT_MAX = 63;//之前是64; // 文字消息字数上限
    private static GlobalOnItemClickManagerUtils instance;
    private EditText mEditText;//输入框
    private static Context mContext;
    private OnEmotionClickListener mEmotionClickListener;

    public static GlobalOnItemClickManagerUtils getInstance(Context context) {
        mContext=context;
        if (instance == null) {
            synchronized (GlobalOnItemClickManagerUtils.class) {
                if(instance == null) {
                    instance = new GlobalOnItemClickManagerUtils();
                }
            }
        }
        return instance;
    }

    public void attachToEditText(EditText editText) {
        mEditText = editText;
    }

    public void setOnEmotionClickListener(OnEmotionClickListener listener){
        mEmotionClickListener = listener;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(final int emotion_map_type) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAdapter = parent.getAdapter();

                if (itemAdapter instanceof EmotionGridViewAdapter) {
                    // 点击的是表情
                    EmotionGridViewAdapter emotionGvAdapter = (EmotionGridViewAdapter) itemAdapter;

                    if (emotionGvAdapter.hasDeleteBtn() && position == emotionGvAdapter.getCount() - 1) {
                        // 如果点击了最后一个回退按钮,则调用删除键事件
                        if(mEditText != null){
                            mEditText.dispatchKeyEvent(new KeyEvent(
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        }
                    } else {
                        // 如果点击了表情,则添加到输入框中
                        String emotionName = emotionGvAdapter.getItem(position);
                        Log.i("", "--------------哎呀，你点击了表情了啊  index = "+position+"---emotionName = "+emotionName);

                        if(mEmotionClickListener != null){
                            mEmotionClickListener.clickEmotion(emotionName);
                            return;
                        }else if(mEditText != null){
                            // 获取当前光标位置,在指定位置上添加表情图片文本
                            int curPosition = mEditText.getSelectionStart();
                            StringBuilder sb = new StringBuilder(mEditText.getText().toString());
                            sb.insert(curPosition, emotionName);

                            // 特殊文字处理,将表情等转换一下
                            mEditText.setText(SpanStringUtils.getEmotionContent(emotion_map_type,
                                    mContext, mEditText, sb.toString()));

                            // 将光标设置到新增完表情的右侧
                            if(curPosition + emotionName.length() < CHAT_TEXT_MAX){
                                mEditText.setSelection(curPosition + emotionName.length());
                            }
                        }

                    }

                }
            }
        };
    }




}
