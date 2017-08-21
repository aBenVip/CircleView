package com.dgcredit.circleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.wangyuwei.loadingview.LoadingView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleView viewById = (CircleView) findViewById(R.id.circle);
        viewById.setMiddleText(0.0750);
        viewById.setSweepAngle(0.5F);
        viewById.setRateText("年化利率");
    }
}
