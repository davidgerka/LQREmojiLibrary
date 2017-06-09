package com.win.emoji.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;


import com.lqr.emoji.R;
import com.win.emoji.adapter.EmotionGridViewAdapter;
import com.win.emoji.adapter.EmotionPagerAdapter;
import com.win.emoji.emotionkeyboardview.EmojiIndicatorView;
import com.win.emoji.util.DisplayUtils;
import com.win.emoji.util.EmotionUtils;
import com.win.emoji.util.GlobalOnItemClickManagerUtils;
import com.win.emoji.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:可替换的模板表情，gridview实现
 */
public class EmotiomComplateFragment extends BaseFragment {
    private EmotionPagerAdapter emotionPagerGvAdapter;
    private ViewPager vp_complate_emotion_layout;
    private EmojiIndicatorView ll_point_group;//表情面板对应的点列表
    private int emotion_map_type;

    private int linesPerPage;      //每一页有多少行
    private int countsPerLine;     //一行多少个表情
    private boolean hasDeleteBtn;     //每一页是否有删除按钮
    private static final int EMOTION_MARGIN_PADDING = 1;     //表情两边的padding，单位为dp，计算时要乘以屏幕密度
    private static final int EMOTION_MARGIN_LANDSCAPE = 6;   //表情左右间隔，单位为dp，计算时要乘以屏幕密度
    private static final int EMOTION_MARGIN_VERTICAL = 16;   //表情上下间隔，单位为dp，计算时要乘以屏幕密度

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
        View rootView = inflater.inflate(R.layout.fragment_complate_emotion, container, false);
        initView(rootView);
        initListener();
        return rootView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {
        vp_complate_emotion_layout = (ViewPager) rootView.findViewById(R.id.vp_complate_emotion_layout);
        ll_point_group = (EmojiIndicatorView) rootView.findViewById(R.id.ll_point_group);
        //获取map的类型
        emotion_map_type = args.getInt(FragmentFactory.EMOTION_MAP_TYPE);
        initParams();
        initEmotion();
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

        vp_complate_emotion_layout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ll_point_group.playByStartPointToNext(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void initParams() {
        countsPerLine = 7;      //一行7个表情
        linesPerPage = 3;       //4行
        hasDeleteBtn = false;   //没有删除按钮
    }

    /**
     * 初始化表情面板
     * 思路：获取表情的总数，按每行存放7个表情，动态计算出每个表情所占的宽度大小（包含间距），
     * 而每个表情的高与宽应该是相等的，这里我们约定只存放3行
     * 每个面板最多存放7*3=21个表情，再减去一个删除键，即每个面板包含20个表情
     * 根据表情总数，循环创建多个容量为20的List，存放表情，对于大小不满20进行特殊
     * 处理即可。
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int screenWidth = ScreenUtils.getScreenWidth(getActivity());
        // item的间距
        int spacingPadding = DisplayUtils.dp2px(getActivity(), EMOTION_MARGIN_PADDING);  //padding
        int spacingLandscape = DisplayUtils.dp2px(getActivity(), EMOTION_MARGIN_LANDSCAPE);  //横向距离
        int spacingVertical = DisplayUtils.dp2px(getActivity(), EMOTION_MARGIN_VERTICAL);  //竖向距离
        // 动态计算item的宽度和高度
//        int itemWidth = (screenWidth - spacing * (countsPerLine + 2)) / countsPerLine;
        int itemWidth = (screenWidth - 2 * spacingPadding - spacingLandscape * (countsPerLine - 1)) / countsPerLine;
        //动态计算gridview的总高度
        int gvHeight = itemWidth * linesPerPage + spacingVertical * (linesPerPage + 2);

        int countsPerPage = (hasDeleteBtn ? countsPerLine * linesPerPage - 1 : countsPerLine * linesPerPage);
        Log.i("", "----------------------countsPerPage = " + countsPerPage + "---height = "
                + gvHeight + "----screenWidth = " + screenWidth + "---itemWidth = " + itemWidth);

        List<GridView> emotionViews = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        // 遍历所有的表情的key
        for (String emojiName : EmotionUtils.getEmojiMap(emotion_map_type).keySet()) {
            emotionNames.add(emojiName);
            // 每countsPerPage个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == countsPerPage) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, gvHeight, itemWidth, spacingLandscape, spacingVertical, spacingPadding);
                emotionViews.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }

        // 判断最后是否有不足一页个数表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, gvHeight, itemWidth, spacingLandscape, spacingVertical, spacingPadding);
            emotionViews.add(gv);
        }

        //初始化指示器
        ll_point_group.initIndicator(emotionViews.size());
        // 将多个GridView添加显示到ViewPager中
        emotionPagerGvAdapter = new EmotionPagerAdapter(emotionViews);
        vp_complate_emotion_layout.setAdapter(emotionPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        vp_complate_emotion_layout.setLayoutParams(params);
        vp_complate_emotion_layout.setOverScrollMode(View.OVER_SCROLL_NEVER);

    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int gvHeight, int itemWidth,
                                           int spacingLandscape, int spacingVertical, int spacingPadding) {
        // 创建GridView
        GridView gv = new GridView(getActivity());
        //设置点击背景透明
//        gv.setSelector(android.R.drawable.list_selector_background);
        gv.setSelector(R.drawable.btn_r_rectangle_transparent_black_sel);
//        gv.setBackgroundResource(android.R.color.holo_green_light);
        //设置7列
        gv.setNumColumns(countsPerLine);
        gv.setPadding(spacingPadding, spacingVertical, spacingPadding, spacingVertical);
        gv.setHorizontalSpacing(spacingLandscape);
        gv.setVerticalSpacing(spacingVertical);
        gv.setGravity(Gravity.CENTER);

        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(getActivity(), emotionNames, itemWidth, emotion_map_type, hasDeleteBtn);
        gv.setAdapter(adapter);
        //设置全局点击事件
        gv.setOnItemClickListener(GlobalOnItemClickManagerUtils.getInstance(getActivity()).getOnItemClickListener(emotion_map_type));
        return gv;
    }


}
