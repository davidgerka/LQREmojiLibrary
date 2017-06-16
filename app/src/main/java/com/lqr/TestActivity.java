package com.lqr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 会话界面
 */
public class TestActivity extends BaseActivity {
    private static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


//        findViewById(R.id.tv_time).setVisibility(View.GONE);
//        findViewById(R.id.tv_master).setVisibility(View.GONE);

        TextView textView = (TextView) findViewById(R.id.tv_content_text);
        textView.getLayoutParams().width = 510;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_layout_record);
        linearLayout.getLayoutParams().width = 410;

        findViewById(R.id.content_layout_text).setVisibility(View.GONE);
//        findViewById(R.id.content_layout_record).setVisibility(View.GONE);
//        findViewById(R.id.content_layout_pic).setVisibility(View.GONE);

        ImageView picView = (ImageView) findViewById(R.id.biv_content_pic);
        picView.setImageResource(R.drawable.tls);



        View chatToView = findViewById(R.id.cl_chatto);
//        chatToView.findViewById(R.id.content_layout_text).setVisibility(View.GONE);
        chatToView.findViewById(R.id.content_layout_record).setVisibility(View.GONE);
        chatToView.findViewById(R.id.content_layout_pic).setVisibility(View.GONE);
        picView = (ImageView) chatToView.findViewById(R.id.biv_content_pic);
        picView.setImageResource(R.drawable.tls);

        textView = (TextView) chatToView.findViewById(R.id.tv_content_text);
        textView.getLayoutParams().width = 510;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
