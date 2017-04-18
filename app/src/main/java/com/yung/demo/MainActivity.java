package com.yung.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yung.widget.PendulumMenu;
import com.yung.widget.R;

public class MainActivity extends AppCompatActivity {
    PendulumMenu pendulummenuid;
    private int[] imgRes;
    private int[] linecos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ini();
    }

    public void ini() {
        pendulummenuid = (PendulumMenu) findViewById(R.id.pendulummenuid);
        imgRes = new int[]{R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e};
        linecos = new int[]{Color.parseColor("#ffbe00"), Color.parseColor("#ff9642"), Color.parseColor("#a8e968"), Color.parseColor("#63d4fe"), Color.parseColor("#ff8383")};
        pendulummenuid.setTextsAndImages(imgRes, linecos);
        pendulummenuid.setonMenuItemListener(new PendulumMenu.onMenuItemListener() {
            @Override
            public void onMenuClick(int index) {
                if (index > -1)//不再bitmap点击区域内时，返回-1
                    Toast.makeText(MainActivity.this, "第" + (index + 1) + "个子菜单被点击", Toast.LENGTH_SHORT).show();
            }
        });
        pendulummenuid.start();
    }
}
