package com.jimmy.rx;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jakewharton.rxbinding2.view.RxView;
import com.jimmy.rx.combine.CombineLatestActivity;
import com.jimmy.rx.creat.DeferActivity;
import com.jimmy.rx.filter.TakeActivity;
import com.jimmy.rx.rxview.RxViewActivity;
import com.jimmy.rx.延时和轮询.TimeIntervalActivity;
import com.jimmy.tool.ToastUtils;

import java.util.LinkedList;
import java.util.List;
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
        height = ScreenUtils.getScreenHeight();
        width = ScreenUtils.getScreenWidth();
        Log.d("YuChangXueService", "height:" + height + ";width:" + width);


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.conbine:
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                    Toast.makeText(MainActivity.this, "7.0及以上才能使用手势", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Path path = new Path();
//                path.moveTo(400, 800);
//                path.lineTo(10, 800);
//                final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 0, 500);
//                YuChangXueService.mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), new AccessibilityService.GestureResultCallback() {
//                    @Override
//                    public void onCompleted(GestureDescription gestureDescription) {
//                        super.onCompleted(gestureDescription);
//                        Toast.makeText(MainActivity.this, "手势成功", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(GestureDescription gestureDescription) {
//                        super.onCancelled(gestureDescription);
//                        Toast.makeText(MainActivity.this, "手势失败，请重启手机再试", Toast.LENGTH_SHORT).show();
//                    }
//                }, null);

                if (!YuChangXueService.isStart()) {
                    try {
                        this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    } catch (Exception e) {
                        this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort("开始执行");
                    YuChangXueService.setZERO();
                    this.moveTaskToBack(true);
                }

                break;

            case R.id.btnCheckHome:
                YuChangXueService.setZERO();

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (Settings.canDrawOverlays(this)) {//有权限
//
//        } else {//无权限
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            intent.setData(Uri.parse("package:" + this.getApplicationContext().getPackageName()));
//            this.startActivity(intent);
//        }
        if (!Settings.canDrawOverlays(this)) {//有权限
            XXPermissions.with(this)
                    // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                    .constantRequest()
                    // 支持请求6.0悬浮窗权限8.0请求安装权限
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .request(new OnPermission() {

                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {

                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {

                        }
                    });
        }
    }


    /**
     * 判断一个服务是否正在运行
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> lists = am.getRunningServices(200);

        for (ActivityManager.RunningServiceInfo info : lists) {//判断服务
            if (info.service.getClassName().equals(serviceName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }
}
