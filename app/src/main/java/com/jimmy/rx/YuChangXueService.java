package com.jimmy.rx;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.XToast;
import com.jimmy.tool.ToastUtils;
import com.jimmy.tool.Utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.jimmy.rx.AbstractTF.isEmptyArray;

public class YuChangXueService extends AccessibilityService {

    private final String TAG = getClass().getName();
    public static int videoNum = 0;
    public static int bookNum = 0;
    public static int collectNum = 0;
    private String Dianshitai = "cn.xuexi.android:id/home_bottom_tab_button_contact";
    private String Bailing = "cn.xuexi.android:id/home_bottom_tab_button_ding";
    private String Wenzhang = "cn.xuexi.android:id/home_bottom_tab_button_work";


    private long videoTime = 100 * 1000;
    private int videoNumMax = 10;
    private long bookTime = 60 * 1000;
    private int bookNumMax = 10;


    public static YuChangXueService mService;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://模拟点击视屏
                    if (isHome()) {
                        videoNum++;
                        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条视频新闻
                    }
                    mHandler.sendEmptyMessageDelayed(1, videoTime);
                    show("第" + videoNum + "条视频\n正在观看" + videoTime / 1000 + "秒\n请勿关闭", videoTime);
                    break;
                case 1://播放视频界面停留之后，点击返回
                    //在播放视频界面，10s之后，触发返回
                    if (!isHome()) {
                        YuChangXueService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    //返回之后,要求在首页才会去触发滑动
                    mHandler.sendEmptyMessageDelayed(2, 500);
                    break;
                case 2://回到首页，滑动
                    Log.d("YuChangXueService", "videoNum:" + videoNum);
                    if (videoNum >= videoNumMax) {
                        ToastUtils.showLong("视频播放完成");
                        mHandler.sendEmptyMessageDelayed(3, 1000);//去跳转到文章阅读
                        return;
                    } else {
                        if (isHome()) {
                            Path path = new Path();
                            path.moveTo(400, MainActivity.height / 2);
                            path.lineTo(400, 0);
                            dispatchGestureMove(path, 40);
                        }
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                    break;
                case 3://学习文章
                    ToastUtils.showLong("开始学习文章");
                    if (isHome()) {
                        ServiceUtils.performClickWithID(YuChangXueService.this, Wenzhang);
                        mHandler.sendEmptyMessageDelayed(4, 500);
                    }
                    break;

                case 4://点击book
                    if (isHome()) {
                        bookNum++;
                        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条文章
                    }
                    if (collectNum < 4) {//去收藏
                        collectNum++;
                        mHandler.sendEmptyMessageDelayed(7, 1500);//去收藏
                    }
                    mHandler.sendEmptyMessageDelayed(11, 500);//视听
                    //去执行阅读
                    mHandler.sendEmptyMessageDelayed(5, bookTime);
                    show("第" + bookNum + "篇\n" + "阅读文章" + bookTime / 1000 + "秒\n请勿关闭", bookTime);
                    break;
                case 7://点击收藏
                    clickCollect();
                    mHandler.sendEmptyMessageDelayed(8, 1000);
//                    if (isCanClickCollect()) {
//                        dispatchGestureClick(900, 1820);//模拟点击最右边的更多
//                    }
////                    dispatchGestureClick(50, 1900);//模拟点击最右边的更多
//                    mHandler.sendEmptyMessageDelayed(8, 1000);
                    break;
                case 8://弹出分享框
                    clickShare();
                    break;
                case 9://点击分享按钮
                    clickWXshare();
                    break;
                case 10://从强国的分享返回
                    if (!isHome()) {
                        YuChangXueService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        ToastUtils.showShort("分享成功，不需要分享给具体的人");
                    }
                    break;
                case 11://开启播放语音
                    clickSound();
                    break;
                case 5://book页面需要停留的时间
                    if (!isHome()) {
                        YuChangXueService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    //返回之后,要求在首页才会去触发滑动
                    mHandler.sendEmptyMessageDelayed(6, 1000);
                    break;
                case 6://首页去滑动文章页面
                    if (bookNum >= bookNumMax) {
                        ToastUtils.showLong("任务执行完毕");
                        YuChangXueService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                        return;
                    }

                    if (bookNum == 3 || bookNum == 7) {//观看到这几个的时候，需要横向滑动
                        Log.d("YuChangXueService", "开始横向滑动");
                        if (isHome()) {
                            Path path = new Path();
                            path.moveTo(800, MainActivity.height / 5 * 4);
                            path.lineTo(0, MainActivity.height / 5 * 4);
                            dispatchGestureMove(path, 40);
                        }
                    } else {
                        if (isHome()) {
                            Path path = new Path();
                            path.moveTo(400, MainActivity.height / 2);
                            path.lineTo(400, 0);
                            dispatchGestureMove(path, 40);
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(4, 2000);
                    break;


            }
        }
    };


    public static boolean canHome = true;
    private Disposable bailing;
    private Disposable bailing1;


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        ToastUtils.showShort("服务已经连接");
        mService = this;
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = TYPE_WINDOW_STATE_CHANGED;
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        serviceInfo.packageNames = new String[]{"cn.xuexi.android"};// 监控的app
        serviceInfo.notificationTimeout = 100;
        //设置可以监控webview
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);

//        this.getServiceInfo().flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
    }


    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
//        Log.d("YuChangXueService", accessibilityEvent.getPackageName() + "\n" + accessibilityEvent.getClassName());
//        Log.d("YuChangXueService", "accessibilityEvent.getEventType():" + accessibilityEvent.getEventType());
//        Log.d("YuChangXueService", "accessibilityEvent.getPackageName():" + accessibilityEvent.getPackageName());
//        Log.d("YuChangXueService", "accessibilityEvent.getClassName():" + accessibilityEvent.getClassName());
        if (!canHome) {
            return;
        }
        if (isHome()) {
            ToastUtils.showShort("当前处在首页");//只有在首页才能进行下面的操作
            //点击电视台
            canHome = false;
            ServiceUtils.performClickWithID(YuChangXueService.this, Dianshitai);
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }

//        clickPlaySound();

//
//        AccessibilityNodeInfo uiContent = ServiceUtils.findNodeInfoByViewId(getRootInActiveWindow(), "cn.xuexi.android:id/ui_common_base_ui_activity_content");
//        if (uiContent != null) {
//            Log.d("YuChangXueService", "-------");
//            Log.d("YuChangXueService", uiContent.getChild(1).toString());


//            AccessibilityNodeInfo listView = viewpager.getChild(1);
//            Log.d("YuChangXueService", "listView.getClassName():" + listView.getClassName());
//            Log.d("YuChangXueService", "listView.getChildCount():" + listView.getChildCount());
//            Log.d("YuChangXueService", "listView.getChild(0).toString():" + listView.getChild(0).toString());
//            Log.d("YuChangXueService", "listView.getChild(1).toString():" + listView.getChild(1).toString());
//            Log.d("YuChangXueService", "listView.getChild(2).toString():" + listView.getChild(2).toString());
//            Log.d("YuChangXueService", "listView.getChild(3).toString():" + listView.getChild(3).toString());
//            Log.d("YuChangXueService", "listView.getChild(4).toString():" + listView.getChild(4).toString());
//            Log.d("YuChangXueService", "listView.getChild(5).toString():" + listView.getChild(5).toString());
//
//            if (listView.getChild(0) == null) {
//                return;
//            }
//            if (listView.getChild(0).getChild(0) == null) {
//                return;
//            }

//            listView.getChild(0).getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

//            for (int i = 0; i < listView.getChildCount(); i++) {
//                Log.d("YuChangXueService", "i:" + i);
//                Log.d("YuChangXueService", "listView.getChild(i):" + listView.getChild(i).getClassName());
////                for (int j = 0; j < listView.getChild(0).getChildCount(); j++) {
////                    Log.d("YuChangXueService", "listView.getChild(0).getChild(j).getClassName():" + listView.getChild(0).getChild(j).getClassName());
////                }
//
////                if ("android.widget.ListView".equals(viewpager.getChild(i).getClassName())) {
////                    Log.d("YuChangXueService", "i:" + i);
////                    Log.d("YuChangXueService", "viewpager.getChild(i).getClassName():" + viewpager.getChild(i).getClassName());
////                }
//            }
//        }

//        List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByText("欢迎发表你的观点");
//        if (list == null) {
//            Log.d("YuChangXueService", "这个是没找到");
//            return;
//        } else {
//            Log.d("YuChangXueService", "找到啦");
//        }


//        for (AccessibilityNodeInfo item : list) {
//            /**
//             *  蚂蚁森林本身不可点击，但是他的父控件可以点击
//             */
//            AccessibilityNodeInfo parent = item.getParent();
//            if (null != parent) {
//                if (parent.getChild(2).equals("android.widget.ImageView")) {
//                    if (parent.getChild(2).isClickable()) {
//                       accessibilityNodeInfo.getParent().getChild(2)) parent.getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        Log.d(TAG, "item = " + item.toString() + ", parent click = " + parent.toString());
//                    }
//                }
//                break;
//            }
//        }


//        dispatchGestureClick(980, 1820);//模拟点击最右边的更多


//        AccessibilityNodeInfo mNodeInfo = getRootInActiveWindow();


//        AccessibilityNodeInfo mNodeInfo = accessibilityEvent.getSource();

    }

    @Override
    public void onInterrupt() {
        ToastUtils.showShort("学习功能被迫中断");
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        ToastUtils.showShort("学习功能已关闭");
        mService = null;
    }


    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }

    //点击收藏
    public void clickCollect() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByText(this.getRootInActiveWindow(), "欢迎发表你的观点");
        if (accessibilityNodeInfo != null && accessibilityNodeInfo.getClassName().equals("android.widget.TextView")) {
            Log.d("YuChangXueService", "找到了这个控件");
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
            if (parent != null && parent.getChildCount() > 0 && parent.getChildCount() == 9) {
                Log.d("YuChangXueService", "parent.getChildCount():" + parent.getChildCount());

                for (int i = 0; i < parent.getChildCount(); i++) {
                    AccessibilityNodeInfo child = parent.getChild(i);
                    if (child == null) {
                        continue;
                    }
                    if (child.isClickable() && child.isEnabled() && "android.widget.ImageView".equals(child.getClassName())) {
                        Log.d("YuChangXueService", "i:" + i);
                        Log.d("YuChangXueService", "child.getClassName():" + child.getClassName());
                        switch (i) {
                            case 7://8为分享。7为收藏.(文章学习界面)，2为返回，4为分享
                                parent.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);//
                                break;
                        }
                    }
                }
            }
        }
    }


    //点击分享按钮
    public void clickShare() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByText(this.getRootInActiveWindow(), "欢迎发表你的观点");
        if (accessibilityNodeInfo != null && accessibilityNodeInfo.getClassName().equals("android.widget.TextView")) {
            Log.d("YuChangXueService", "找到了这个控件");
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
            if (parent != null && parent.getChildCount() > 0 && parent.getChildCount() == 9) {
                parent.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);//
                mHandler.sendEmptyMessageDelayed(9, 1000);
//                for (int i = 0; i < parent.getChildCount(); i++) {
//                    AccessibilityNodeInfo child = parent.getChild(i);
//                    if (child == null) {
//                        continue;
//                    }
//                    if (child.isClickable() && child.isEnabled() && "android.widget.ImageView".equals(child.getClassName())) {
//                        Log.d("YuChangXueService", "i:" + i);
//                        Log.d("YuChangXueService", "child.getClassName():" + child.getClassName());
//                        switch (i) {
//                            case 4://8为分享。7为收藏.(文章学习界面)，2为返回，4为分享
//                                parent.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);//
//                                mHandler.sendEmptyMessageDelayed(9, 1000);
//                                break;
//                        }
//                    }
//                }
            }
        }
    }

    //点击分享强国
    public void clickWXshare() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByViewId(this.getRootInActiveWindow(), "cn.xuexi.android:id/img_gv_item");
        accessibilityNodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        mHandler.sendEmptyMessageDelayed(10, 500);//500毫秒后，自动返回
//        Log.d("YuChangXueService", "accessibilityNodeInfo:" + accessibilityNodeInfo);
//        Log.d("YuChangXueService", "accessibilityNodeInfo.getChildCount():" + accessibilityNodeInfo.getChildCount());
    }


    //点击视听
    private void clickSound() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByText(this.getRootInActiveWindow(), "欢迎发表你的观点");
        if (accessibilityNodeInfo != null && accessibilityNodeInfo.getClassName().equals("android.widget.TextView")) {
            Log.d("YuChangXueService", "找到了这个控件");
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
            if (parent != null && parent.getChildCount() > 0 && parent.getChildCount() == 9) {
//                parent.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);//
//                mHandler.sendEmptyMessageDelayed(9, 1000);
                Rect rect = new Rect();
                parent.getChild(4).getBoundsInScreen(rect);
                ToastUtils.showShort("开启播放视听");
                dispatchGestureClick(rect.left - 10, rect.top + 10);//模拟点击最右边的更多
                Log.d("YuChangXueService", "rect.left:" + rect.left);
                Log.d("YuChangXueService", "rect.top:" + rect.top);
            }
        }
    }

    /**
     * 立即发送移动的手势
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     *
     * @param path  移动路径
     * @param mills 持续总时间
     */
    @RequiresApi(24)
    public void dispatchGestureMove(Path path, long mills) {
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, mills)).build(), null, null);
    }

    /**
     * 点击指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    @RequiresApi(24)
    public void dispatchGestureClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }

    /**
     * 由于太多,最好回收这些AccessibilityNodeInfo
     */
    public static void recycleAccessibilityNodeInfo(List<AccessibilityNodeInfo> listInfo) {
        if (isEmptyArray(listInfo)) return;

        for (AccessibilityNodeInfo info : listInfo) {
            info.recycle();
        }
    }


    /**
     * 判断当前页面view在不在
     *
     * @param packageAndId
     * @return
     */
    public boolean isViewExist(String packageAndId) {
        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        if (nodeInfo == null) {
            return false;
        }
        AccessibilityNodeInfo targetNode = ServiceUtils.findNodeInfoByViewId(nodeInfo, packageAndId);
        nodeInfo.recycle();
        if (targetNode != null) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否在首页
     *
     * @return
     */
    public boolean isHome() {
        return isViewExist("cn.xuexi.android:id/home_bottom_tab_button_ding") && isViewExist("cn.xuexi.android:id/home_bottom_tab_button_message");
    }

    public boolean isCanClickCollect() {
        return isViewExist("cn.xuexi.android:id/common_webview");
    }

    /**
     * 是否在应用内
     *
     * @param str
     * @return
     */
    public boolean isInApp(String str) {
        return str.equals("cn.xuexi.android");
    }

    public void show(String message, Long lo) {
        new XToast(Utils.getApp())
                .setView(R.layout.toast)
                // 设置成可拖拽的
                //.setDraggable()
                // 设置显示时长
                .setGravity(Gravity.CENTER)
                .setDuration(lo.intValue())
                // 设置动画样式
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setText(R.id.tv_toast, message)
                .setOnClickListener(R.id.tv_toast, new OnClickListener<TextView>() {

                    @Override
                    public void onClick(XToast toast, TextView view) {
                        // 点击这个 View 后消失
                        toast.cancel();
                        // 跳转到某个Activity
                        // toast.startActivity(intent);
                    }
                })
                .show();
    }

    public static void setZERO() {
        videoNum = 0;
        bookNum = 0;
        collectNum = 0;
        YuChangXueService.canHome = true;
    }
}