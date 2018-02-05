package com.moskovko.meditation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
    }

    public void resetTimerPoints(View view) {
        ((TimerView)findViewById(R.id.timer)).clearTimerPoints();
    }

    public void startTimer(View view) {
        TimerView timer = ((TimerView)findViewById(R.id.timer));
        if (timer.isRunning()) {
            timer.stop();
            ((Button)findViewById(R.id.start_stop_btn)).setText("start");
        } else {
            timer.start();
            ((Button)findViewById(R.id.start_stop_btn)).setText("stop");
        }
    }
}
