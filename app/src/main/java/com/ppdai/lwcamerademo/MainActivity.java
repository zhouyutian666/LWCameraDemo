package com.ppdai.lwcamerademo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by liwen on 2017/6/8.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView mIVData;
    private TextView tv_home;
    private boolean isRun = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        tv_home = (TextView) findViewById(R.id.tv_home);
        mIVData = (ImageView) findViewById(R.id.iv_data);

        startActivityForResult(new Intent(this, PhotoActivity.class), 0);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isRun) {
                    startActivityForResult(new Intent(MainActivity.this, PhotoActivity.class), 0);
                    isRun = true;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIVData.setImageBitmap(Config.Image);
        isRun = false;
    }
}
