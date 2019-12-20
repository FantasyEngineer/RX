package com.jimmy.rx;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jimmy.tool.ToastUtils;
import com.jimmy.tool.Utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.jimmy.rx.AbstractTF.isEmptyArray;

public class YuChangXueService extends AccessibilityService {

    private final String TAG = getClass().getName();
    private int videoNum = 0;

    public static YuChangXueService mService;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://模拟点击视屏
                    if (isHome()) {
                        videoNum++;
                        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条视频新闻
                    }
                    mHandler.sendEmptyMessageDelayed(1, 5000);
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
                    if (videoNum > 3) {
                        mHandler.sendEmptyMessageDelayed(3, 1000);
                        return;
                    } else {
                        if (isHome()) {
                            Path path = new Path();
                            path.moveTo(400, MainActivity.height / 2);
                            path.lineTo(400, 0);
                            dispatchGestureMove(path, 1000);
                        }
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                    break;
                case 3:
                    ToastUtils.showShort("点击中间的");
                    ServiceUtils.performClickWithID(YuChangXueService.this, "cn.xuexi.android:id/home_bottom_tab_button_work");
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
    }


    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

//        AccessibilityNodeInfo biaoQingInfo = findFirst(AbstractTF.newContentDescription("表情", true));
//        if (biaoQingInfo != null) {
//            ToastUtils.showShort("找到我的文字");
//            Log.e(TAG, "onAccessibilityEvent: 找到wx的表情图标");//可以查看日志
//            biaoQingInfo.recycle();
//        }

//        Log.wtf(TAG, "onAccessibilityEvent : " + accessibilityEvent.toString());
        if (!canHome) {
            return;
        }
        if (isHome()) {
            ToastUtils.showShort("当前处在首页");//只有在首页才能进行下面的操作
            //点击百灵按钮
            canHome = false;
            ServiceUtils.performClickWithID(YuChangXueService.this, "cn.xuexi.android:id/home_bottom_tab_button_ding");
            mHandler.sendEmptyMessageDelayed(0, 500);
//            Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<Long>() {
//                        @Override
//                        public void accept(Long aLong) throws Exception {
////                            dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条视频新闻
//                            dispatchGestureClick(400, MainActivity.height / 3 + 10);//模拟点击一条视频新闻
//
//                            bailing = Observable.intervalRange(1, 3, 0, 10, TimeUnit.SECONDS)
//                                    .doOnSubscribe(new Consumer<Disposable>() {
//                                        @Override
//                                        public void accept(Disposable disposable) throws Exception {
//                                            Log.d("YuChangXueService", "这里执行");
//                                        }
//                                    })
//                                    .observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Long, ObservableSource<Long>>() {
//                                        @Override
//                                        public ObservableSource<Long> apply(Long o) throws Exception {
////                                    if (o.longValue() > 3) {
//////                                        bailing.dispose();
//////                                    }
//                                            //在播放视频界面，10s之后，触发返回
//                                            if (!isHome()) {
//                                                YuChangXueService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//                                            }
//                                            //返回之后,要求在首页才会去触发滑动
//                                            if (isHome()) {
//                                                Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
//                                                        .subscribe(new Consumer<Long>() {
//                                                            @Override
//                                                            public void accept(Long aLong) throws Exception {
//                                                                Path path = new Path();
//                                                                path.moveTo(400, MainActivity.height / 2);
//                                                                path.lineTo(400, 70);
//                                                                dispatchGestureMove(path, 1000);
//                                                            }
//                                                        });
//                                            }
//                                            return Observable.just(o).delay(2, TimeUnit.SECONDS);
//                                        }
//                                    }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Long, ObservableSource<?>>() {
//                                        @Override
//                                        public ObservableSource<?> apply(Long aLong) throws Exception {
//                                            if (isHome()) {
//                                                dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条视频新闻
//                                            }
//                                            return Observable.just(aLong);
//                                        }
//                                    }).toList().subscribe(new Consumer<List<Object>>() {
//                                        @Override
//                                        public void accept(List<Object> objects) throws Exception {
//                                            Log.d("YuChangXueService", "去读书:");
////                                    readBook();
//                                        }
//                                    });
//                        }
//                    });
        }
    }


    //阅读文章。
    private void readBook() {
        if (!isHome()) {
            ToastUtils.showShort("不是在首页，阅读中断");
        }
        //阅读文章
        ServiceUtils.performClickWithID(this, "cn.xuexi.android:id/home_bottom_tab_icon_group");
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条文章
                        dispatchGestureClick(400, MainActivity.height / 3 + 10);//模拟点击文章
                        //然后观看视频轮询
                        bailing1 = Observable.interval(10, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Long, ObservableSource<Long>>() {
                            @Override
                            public ObservableSource<Long> apply(Long o) throws Exception {
                                if (o.longValue() > 3) {
                                    bailing1.dispose();
                                }
                                //在播放视频界面，10s之后，触发返回
                                YuChangXueService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                //返回之后,要求在首页才会去触发滑动
                                if (isHome()) {
                                    Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Long>() {
                                                @Override
                                                public void accept(Long aLong) throws Exception {
                                                    Path path = new Path();
                                                    path.moveTo(400, MainActivity.height / 2);
                                                    path.lineTo(400, 70);
                                                    dispatchGestureMove(path, 1000);
                                                }
                                            });
                                }
                                return Observable.just(o).delay(2, TimeUnit.SECONDS);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.d("YuChangXueService", "aLong:" + aLong);
                                if (isHome()) {
                                    dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条文章
                                }
                            }
                        });
                    }
                });

    }

    @Override
    public void onInterrupt() {
        ToastUtils.showShort("学习功能被迫中断");
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastUtils.showShort("学习功能已关闭");
        mService = null;
    }


    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }


    /**
     * 点击该控件
     *
     * @return true表示点击成功
     */
    public static boolean clickView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            } else {
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                if (parent != null) {
                    boolean b = clickView(parent);
                    parent.recycle();
                    if (b) return true;
                }
            }
        }
        return false;
    }

    /**
     * 查找第一个匹配的控件
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    @Nullable
    public AccessibilityNodeInfo findFirst(@NonNull AbstractTF... tfs) {
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) return null;

        int idTextTFCount = 0, idTextIndex = 0;
        for (int i = 0; i < tfs.length; i++) {
            if (tfs[i] instanceof AbstractTF.IdTextTF) {
                idTextTFCount++;
                idTextIndex = i;
            }
        }
        switch (idTextTFCount) {
            case 0://id或text数量为0，直接循环查找
                AccessibilityNodeInfo returnInfo = findFirstRecursive(rootInfo, tfs);
                rootInfo.recycle();
                return returnInfo;
            case 1://id或text数量为1，先查出对应的id或text，然后再查其他条件
                if (tfs.length == 1) {
                    AccessibilityNodeInfo returnInfo2 = ((AbstractTF.IdTextTF) tfs[idTextIndex]).findFirst(rootInfo);
                    rootInfo.recycle();
                    return returnInfo2;
                } else {
                    List<AccessibilityNodeInfo> listIdText = ((AbstractTF.IdTextTF) tfs[idTextIndex]).findAll(rootInfo);
                    if (isEmptyArray(listIdText)) {
                        break;
                    }
                    AccessibilityNodeInfo returnInfo3 = null;
                    for (AccessibilityNodeInfo info : listIdText) {//遍历找到匹配的
                        if (returnInfo3 == null) {
                            boolean isOk = true;
                            for (AbstractTF tf : tfs) {
                                if (!tf.checkOk(info)) {
                                    isOk = false;
                                    break;
                                }
                            }
                            if (isOk) {
                                returnInfo3 = info;
                            } else {
                                info.recycle();
                            }
                        } else {
                            info.recycle();
                        }
                    }
                    rootInfo.recycle();
                    return returnInfo3;
                }
            default:
                throw new RuntimeException("由于时间有限，并且多了也没什么用，所以IdTF和TextTF只能有一个");
        }
        rootInfo.recycle();
        return null;
    }

    /**
     * @param tfs 由于是递归循环，会忽略IdTF和TextTF
     */
    public static AccessibilityNodeInfo findFirstRecursive(AccessibilityNodeInfo parent, @NonNull AbstractTF... tfs) {
        if (parent == null) return null;
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            boolean isOk = true;
            for (AbstractTF tf : tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                return child;
            } else {
                AccessibilityNodeInfo childChild = findFirstRecursive(child, tfs);
                child.recycle();
                if (childChild != null) {
                    return childChild;
                }
            }
        }
        return null;
    }

    /**
     * 查找全部匹配的控件
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    @NonNull
    public List<AccessibilityNodeInfo> findAll(@NonNull AbstractTF... tfs) {
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) return list;

        int idTextTFCount = 0, idTextIndex = 0;
        for (int i = 0; i < tfs.length; i++) {
            if (tfs[i] instanceof AbstractTF.IdTextTF) {
                idTextTFCount++;
                idTextIndex = i;
            }
        }
        switch (idTextTFCount) {
            case 0://id或text数量为0，直接循环查找
                findAllRecursive(list, rootInfo, tfs);
                break;
            case 1://id或text数量为1，先查出对应的id或text，然后再循环
                List<AccessibilityNodeInfo> listIdText = ((AbstractTF.IdTextTF) tfs[idTextIndex]).findAll(rootInfo);
                if (isEmptyArray(listIdText)) {
                    break;
                }
                if (tfs.length == 1) {
                    list.addAll(listIdText);
                } else {
                    for (AccessibilityNodeInfo info : listIdText) {
                        boolean isOk = true;
                        for (AbstractTF tf : tfs) {
                            if (!tf.checkOk(info)) {
                                isOk = false;
                                break;
                            }
                        }
                        if (isOk) {
                            list.add(info);
                        } else {
                            info.recycle();
                        }
                    }
                }
                break;
            default:
                throw new RuntimeException("由于时间有限，并且多了也没什么用，所以IdTF和TextTF只能有一个");
        }
        rootInfo.recycle();
        return list;
    }

    /**
     * @param tfs 由于是递归循环，会忽略IdTF和TextTF
     */
    public static void findAllRecursive(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, @NonNull AbstractTF... tfs) {
        if (parent == null || list == null) return;
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            boolean isOk = true;
            for (AbstractTF tf : tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                list.add(child);
            } else {
                findAllRecursive(list, child, tfs);
                child.recycle();
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
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = ServiceUtils.findNodeInfoByViewId(nodeInfo, packageAndId);
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
}