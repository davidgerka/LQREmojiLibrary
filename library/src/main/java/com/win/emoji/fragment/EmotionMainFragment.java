package com.win.emoji.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lqr.emoji.R;
import com.win.emoji.adapter.HorizontalRecyclerviewAdapter;
import com.win.emoji.adapter.NoHorizontalScrollerVPAdapter;
import com.win.emoji.emotionkeyboardview.EmotionKeyboard;
import com.win.emoji.emotionkeyboardview.EmotionKeyboardListener;
import com.win.emoji.emotionkeyboardview.NoHorizontalScrollerViewPager;
import com.win.emoji.emotionkeyboardview.OnEmotionClickListener;
import com.win.emoji.model.ImageModel;
import com.win.emoji.util.EmojiFilter;
import com.win.emoji.util.EmotionUtils;
import com.win.emoji.util.GlobalOnItemClickManagerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:表情主界面
 */
public class EmotionMainFragment extends BaseFragment implements OnEmotionClickListener {

    //是否绑定当前Bar的编辑框的flag
    public static final String BIND_TO_EDITTEXT = "bind_to_edittext";
    //是否隐藏bar上的编辑框和发送按钮
    public static final String HIDE_BAR_EDITTEXT_AND_BTN = "hide bar's editText and btn";

    //当前被选中底部tab
    private static final String CURRENT_POSITION_FLAG = "CURRENT_POSITION_FLAG";
    private int CurrentPosition = 0;
    //底部水平tab
    private RecyclerView recyclerview_horizontal;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;

    public LinearLayout recordLayout; // 按住说话布局
    public RelativeLayout wordLayout; // 文字输入布局
    public EditText wordInputEt; // 文字输入
    public TextView recordTv; // 按住说话
    public Button sendWordBtn; // 文字发送按钮
    public Button wordBtn; // 文字输入按钮
    public Button recordBtn; // 录音按钮
    public Button emotionBtn; // 表情按钮
    private View dummyEmotionBtn;

    private int editLines = 1; // 文本输入框的行数

    //需要绑定的内容view
    private View contentView;

    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;

    //是否绑定当前Bar的编辑框,默认true,即绑定。
    //false,则表示绑定contentView,此时外部提供的contentView必定也是EditText
    private boolean isBindToBarEditText = true;

    //是否隐藏bar上的编辑框和发生按钮,默认不隐藏
    private boolean isHidenBarEditTextAndBtn = false;

    List<Fragment> fragments = new ArrayList<>();

    private EmotionKeyboardListener mEmotionKeyboardListener;

    private boolean hideEmotionKeyboard = false;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
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
        View rootView = inflater.inflate(R.layout.fragment_main_emotion, container, false);
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
                .bindToSoftInputButton(wordBtn)
                .bindToEmotionButton(emotionBtn)//绑定表情按钮
                .bindToRecordButton(recordBtn, recordLayout)//绑定录音按钮
                .bindToEmotionKeyboardListener(mEmotionKeyboardListener)
                .build();
        initListener();
        initDatas();
        //创建全局监听
        GlobalOnItemClickManagerUtils globalOnItemClickManager = GlobalOnItemClickManagerUtils.getInstance(getActivity());
        globalOnItemClickManager.setOnEmotionClickListener(this);


        if (isBindToBarEditText) {
            //绑定当前Bar的编辑框
            globalOnItemClickManager.attachToEditText(wordInputEt);

        } else {
            // false,则表示绑定contentView,此时外部提供的contentView必定也是EditText
            globalOnItemClickManager.attachToEditText((EditText) contentView);
            mEmotionKeyboard.bindToEditText((EditText) contentView);
        }
        return rootView;
    }

    public void showInputMethod(){
        wordLayout.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);
        setEmotionIconSel(false);
    }

    public void showEmotionKeyboard(){
        wordLayout.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);
        setEmotionIconSel(true);
    }

    public void showRecordLayout(){
        wordLayout.setVisibility(View.GONE);
        recordLayout.setVisibility(View.VISIBLE);
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

    public void setOnEmotionKeyboardListener(EmotionKeyboardListener emotionKeyboardListener){
        mEmotionKeyboardListener = emotionKeyboardListener;
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
        wordBtn = (Button) rootView.findViewById(R.id.btn_word);
        recordBtn = (Button) rootView.findViewById(R.id.btn_talk);
        emotionBtn = (Button) rootView.findViewById(R.id.btn_emotion);
        dummyEmotionBtn = rootView.findViewById(R.id.btn3);


        viewPager = (NoHorizontalScrollerViewPager) rootView.findViewById(R.id.vp_emotionview_layout);
        recyclerview_horizontal = (RecyclerView) rootView.findViewById(R.id.recyclerview_horizontal);
        if (isHidenBarEditTextAndBtn) {//隐藏
            wordLayout.setVisibility(View.GONE);
            recordLayout.setVisibility(View.VISIBLE);
        } else {
            wordLayout.setVisibility(View.VISIBLE);
            recordLayout.setVisibility(View.GONE);
        }

        wordInputEt.setFilters(
                new InputFilter[]{new InputFilter.LengthFilter(GlobalOnItemClickManagerUtils.CHAT_TEXT_MAX), new EmojiFilter()});
        wordInputEt.addTextChangedListener(textWatcher);

        recordTv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mEmotionKeyboardListener != null){
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
                if(mEmotionKeyboardListener != null){
                    mEmotionKeyboardListener.onSendTextMsg(text);
                }
            }
        });

        sendWordBtn.setEnabled(false);
        wordLayout.setVisibility(View.VISIBLE);
        recordLayout.setVisibility(View.GONE);

        if(hideEmotionKeyboard){
            hideEmotionFunction();
        }
    }

    /**
     * 隐藏表情键盘
     */
    public void setEmotionKeyboardHide(){
        hideEmotionKeyboard = true;
    }

    /**
     * 隐藏表情键盘功能
     */
    private void hideEmotionFunction(){
        dummyEmotionBtn.setVisibility(View.GONE);
        emotionBtn.setVisibility(View.GONE);
    }

    public void clickEmotion(String emotionKey){
        if(mEmotionKeyboardListener != null){
            mEmotionKeyboardListener.onSendTextMsg(emotionKey);
        }
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }

    /**
     * 数据操作，显示表情键盘
     */
    protected void initDatas() {
        replaceFragment();
        List<ImageModel> list = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == 0) {
                ImageModel model1 = new ImageModel();
                model1.icon = getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag = "经典笑脸";
                model1.isSelected = true;
                list.add(model1);
            } else {
                ImageModel model = new ImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }

        //记录底部默认选中第一个
        CurrentPosition = 0;
        mEditor.putInt(CURRENT_POSITION_FLAG, CurrentPosition);
        mEditor.commit();

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(getActivity(), list);
        recyclerview_horizontal.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerview_horizontal.setAdapter(horizontalRecyclerviewAdapter);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        recyclerview_horizontal.setVisibility(View.GONE);
        //初始化recyclerview_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerviewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                //获取先前被点击tab
                int oldPosition = mSharedPreferences.getInt(CURRENT_POSITION_FLAG, 0);

                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                datas.get(CurrentPosition).isSelected = true;
                mEditor.putInt(CURRENT_POSITION_FLAG, CurrentPosition);
                mEditor.commit();
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });


    }

    private void replaceFragment() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotiomComplateFragment f1 = (EmotiomComplateFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        Bundle b = null;
        for (int i = 0; i < 7; i++) {
            b = new Bundle();
            b.putString("Interge", "Fragment-" + i);
            EmotionInnerFragment fg = EmotionInnerFragment.newInstance(EmotionInnerFragment.class, b);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
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

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            int k = wordInputEt.getLineCount();
            if (k != editLines) {
                editLines = k;
                if(mEmotionKeyboardListener != null){
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

    public void hideAllKeyboard(){
        mEmotionKeyboard.hideAllKeyboard();
        setEmotionIconSel(false);
    }

    /**
     * 设置表情icon是否选中，当表情布局弹出来时，为选中状态，其他情况为正常状态
     * @param isSelected
     */
    public void setEmotionIconSel(boolean isSelected){
        if(isSelected){
            emotionBtn.setBackgroundResource(R.drawable.btn_chat_emotion_pre);
        }else {
            emotionBtn.setBackgroundResource(R.drawable.btn_chat_emotion_sel);
        }
    }



}


