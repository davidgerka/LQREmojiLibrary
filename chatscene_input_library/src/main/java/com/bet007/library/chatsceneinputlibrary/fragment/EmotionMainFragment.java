package com.bet007.library.chatsceneinputlibrary.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bet007.library.chatsceneinputlibrary.R;
import com.bet007.library.chatsceneinputlibrary.adapter.NoHorizontalScrollerVPAdapter;
import com.bet007.library.chatsceneinputlibrary.emotionKeyboardView.EmotionKeyboard;
import com.bet007.library.chatsceneinputlibrary.emotionKeyboardView.NoHorizontalScrollerViewPager;
import com.bet007.library.chatsceneinputlibrary.listener.IEmotionKeyboardListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情主界面Fragment
 */
public class EmotionMainFragment extends BaseFragment {

    //是否绑定当前Bar的编辑框的flag
    public static final String BIND_TO_EDITTEXT = "bind_to_edittext";
    //是否隐藏bar上的编辑框和发送按钮
    public static final String HIDE_BAR_EDITTEXT_AND_BTN = "hide bar's editText and btn";
    private static final int CHAT_TEXT_MAX = 1860;  //限制最大输入字数
    public LinearLayout recordLayout; // 按住说话布局
    public RelativeLayout wordLayout; // 文字输入布局
    public EditText wordInputEt; // 文字输入
    public TextView recordTv; // 按住说话
    public Button sendWordBtn; // 文字发送按钮
    public ImageView ivWord; // 文字输入按钮
    public ImageView ivRecord;  //录音
    public ImageView ivPicture; //图片
    public ImageView ivEmoji;   //表情
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;
    private View dummyEmotionBtn;
    private int editLines = 1; // 文本输入框的行数

    //需要绑定的内容view
    private View contentView;

    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;

    //是否绑定当前Bar的编辑框,默认true,即绑定。
    //false,则表示绑定contentView,此时外部提供的contentView必定也是EditText
    private boolean isBindToBarEditText = true;

    //是否隐藏bar上的编辑框和发送按钮,默认不隐藏
    private boolean isHidenBarEditTextAndBtn = false;


    private List<Fragment> fragments = new ArrayList<>();   //表情键盘fragments，如果有多种表情就添加多种

    private IEmotionKeyboardListener mEmotionKeyboardListener;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            int k = wordInputEt.getLineCount();
            if (k != editLines) {
                editLines = k;
                if (mEmotionKeyboardListener != null) {
                    mEmotionKeyboardListener.onListViewSmooth();
                }
            }
            String string = s.toString();
            if (TextUtils.isEmpty(string)) {
                sendWordBtn.setEnabled(false);
            } else {
                sendWordBtn.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

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
        View rootView = inflater.inflate(R.layout.fragment_chatscene_input_keyboard, container, false);
        isHidenBarEditTextAndBtn = args.getBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN);
        //获取判断绑定对象的参数
        isBindToBarEditText = args.getBoolean(EmotionMainFragment.BIND_TO_EDITTEXT);
        mSharedPreferences = getContext().getSharedPreferences(EmotionMainFragment.class.getSimpleName(), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        initView(rootView);
        mEmotionKeyboard = EmotionKeyboard.with(getActivity())
                .setEmotionView(rootView.findViewById(R.id.ll_emotion_layout))//绑定表情面板
                .bindToContent(contentView)//绑定内容view
                .bindToEditText(!isBindToBarEditText ? ((EditText) contentView) : wordInputEt)//判断绑定那种EditView
                .bindToSoftInputButton(ivWord)
                .bindToEmotionButton(ivEmoji)//绑定表情按钮
                .bindToRecordButton(ivRecord, recordLayout)//绑定录音按钮
                .bindToEmotionKeyboardListener(mEmotionKeyboardListener)
                .build();
        initFragments();


        return rootView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {

        recordLayout = (LinearLayout) rootView.findViewById(R.id.layout_record);
        wordLayout = (RelativeLayout) rootView.findViewById(R.id.layout_word);
        wordInputEt = (EditText) rootView.findViewById(R.id.et_input);
        recordTv = (TextView) rootView.findViewById(R.id.tv_record);
        sendWordBtn = (Button) rootView.findViewById(R.id.btn_send);
        dummyEmotionBtn = rootView.findViewById(R.id.btn2);

        View typeContainer = rootView.findViewById(R.id.llContainer);
        ivWord = (ImageView) typeContainer.findViewById(R.id.iv_word);
        ivRecord = (ImageView) typeContainer.findViewById(R.id.iv_record);
        ivPicture = (ImageView) typeContainer.findViewById(R.id.iv_picture);
        ivEmoji = (ImageView) typeContainer.findViewById(R.id.iv_emoji);

        viewPager = (NoHorizontalScrollerViewPager) rootView.findViewById(R.id.vp_emotionview_layout);
        if (isHidenBarEditTextAndBtn) {//隐藏
            wordLayout.setVisibility(View.GONE);
            recordLayout.setVisibility(View.VISIBLE);
        } else {
            wordLayout.setVisibility(View.VISIBLE);
            recordLayout.setVisibility(View.GONE);
        }

        wordInputEt.setFilters(
                new InputFilter[]{new InputFilter.LengthFilter(CHAT_TEXT_MAX)/*, new EmojiFilter()*/});
        wordInputEt.addTextChangedListener(textWatcher);

        recordTv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEmotionKeyboardListener != null) {
                    mEmotionKeyboardListener.onTouchRecordEvent(v, event, recordLayout);
                }
                return true;
            }
        });

        sendWordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = wordInputEt.getText().toString();
                if (text.length() <= 0)
                    return;
                wordInputEt.setText("");
                if (mEmotionKeyboardListener != null) {
                    mEmotionKeyboardListener.onSendTextMsg(text);
                }
            }
        });

        sendWordBtn.setEnabled(false);
        wordLayout.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);
    }

    private void initFragments() {

        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotiomFragment f1 = (EmotiomFragment) factory.getFragment(0);
        f1.attachEditText(wordInputEt);
        f1.setEmotionSelectedListener(mEmotionKeyboardListener);
        fragments.add(f1);
        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    public void showInputMethod() {
        wordLayout.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);
        ivWord.setVisibility(View.INVISIBLE);
        ivRecord.setVisibility(View.VISIBLE);
        setEmotionIconSel(false);
    }

    public void showEmotionKeyboard() {
        wordLayout.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);
        ivWord.setVisibility(View.INVISIBLE);
        ivRecord.setVisibility(View.VISIBLE);
        setEmotionIconSel(true);
    }

    public void showRecordLayout() {
        wordLayout.setVisibility(View.GONE);
        recordLayout.setVisibility(View.VISIBLE);
        ivWord.setVisibility(View.VISIBLE);
        ivRecord.setVisibility(View.INVISIBLE);
        setEmotionIconSel(false);
    }

    /**
     * 绑定内容view
     *
     * @param contentView
     * @return
     */
    public void bindToContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setOnEmotionKeyboardListener(IEmotionKeyboardListener emotionKeyboardListener) {
        mEmotionKeyboardListener = emotionKeyboardListener;
    }

    public void clickEmotion(String emotionKey) {
        if (mEmotionKeyboardListener != null) {
            mEmotionKeyboardListener.onSendTextMsg(emotionKey);
        }
    }

    /**
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     *
     * @return true则隐藏表情布局，拦截返回键操作
     * false 则不拦截返回键操作
     */
    public boolean isInterceptBackPress() {
        return mEmotionKeyboard.interceptBackPress();
    }

    /**
     * 按住说话，初始状态
     */
    public void talkViewInit() {
        recordTv.setText(R.string.record_press_start);
        recordTv.setBackgroundResource(R.drawable.chatscene_record_nor);
    }

    /**
     * 按住说话，录音状态，松开结束
     */
    public void talkViewFreeFinish() {
        recordTv.setText(R.string.record_free_finish);
        recordTv.setBackgroundResource(R.drawable.chatscene_record_pre);
    }

    /**
     * 按住说话，录音状态，松开取消
     */
    public void talkViewFreeCancel() {
        recordTv.setText(R.string.record_free_cancel);
        recordTv.setBackgroundResource(R.drawable.chatscene_record_pre);
    }

    public void hideAllKeyboard() {
        mEmotionKeyboard.hideAllKeyboard();
        setEmotionIconSel(false);
    }

    /**
     * 设置表情icon是否选中，当表情布局弹出来时，为选中状态，其他情况为正常状态
     *
     * @param isSelected
     */
    public void setEmotionIconSel(boolean isSelected) {
        if (isSelected) {
//            emotionBtn.setBackgroundResource(R.drawable.btn_chat_emotion_pre);
        } else {
//            emotionBtn.setBackgroundResource(R.drawable.btn_chat_emotion_sel);
        }
    }


}


