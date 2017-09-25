package com.moskovko.meditation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
    }

    public void resetTimerPoints(View view) {
        ((TimerView)findViewById(R.id.timer)).clearTimerPoints();
    }
}
