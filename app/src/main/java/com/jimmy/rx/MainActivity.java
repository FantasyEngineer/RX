package com.jimmy.rx;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjg.toast.D;
import com.jakewharton.rxbinding2.view.RxView;
import com.jimmy.rx.combine.CombineLatestActivity;
import com.jimmy.rx.creat.DeferActivity;
import com.jimmy.rx.filter.TakeActivity;
import com.jimmy.rx.rxview.RxViewActivity;
import com.jimmy.rx.延时和轮询.TimeIntervalActivity;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;

public class MainActivity extends BaseActivity {

    @BindView(R.id.conbine)
    Button conbine;
    @BindView(R.id.btnCheckHome)
    Button checkHome;
    public static int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        D.init(this);
        height = ScreenUtils.getScreenHeight();
        width = ScreenUtils.getScreenWidth();
        Log.d("YuChangXueService", "height:" + height + ";width:" + width);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.conbine:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Toast.makeText(MainActivity.this, "7.0及以上才能使用手势", Toast.LENGTH_SHORT).show();
                    return;
                }
                Path path = new Path();
                path.moveTo(400, 800);
                path.lineTo(10, 800);
                final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 0, 500);
                YuChangXueService.mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), new AccessibilityService.GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                        Toast.makeText(MainActivity.this, "手势成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                        Toast.makeText(MainActivity.this, "手势失败，请重启手机再试", Toast.LENGTH_SHORT).show();
                    }
                }, null);

                break;

            case R.id.btnCheckHome:
                YuChangXueService.canHome = true;
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!YuChangXueService.isStart()) {
            try {
                this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        }

    }
}
