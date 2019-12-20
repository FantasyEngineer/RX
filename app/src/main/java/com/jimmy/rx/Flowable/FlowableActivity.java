package com.jimmy.rx.Flowable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hjg.toast.D;
import com.jimmy.rx.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * 背压
 * 非Flowable的被观察者使用.buffer(10, TimeUnit.SECONDS);或者window达到背压的策略。
 * flowable被观察者有四种背压策略
 * 1. BackpressureStrategy.ERROR：缓存区默人大小128，流速不均衡时发射MissingBackpressureException信号。
 * 2. BackpressureStrategy.BUFFER：缓存区不限制大小，使用不当仍会OOM。
 * 3. BackpressureStrategy.DROP：缓存最近的nNext事件。
 * 4. BackpressureStrategy.LATEST：缓存区会保留最后的OnNext事件，覆盖之前缓存的OnNext事件。
 * 5. BackpressureStrategy.MISSING：OnNext事件没有任何缓存和丢弃，下游要处理任何溢出。
 * <p>
 * 或者使用sample或者throtlfirst进行丢弃。
 */

public class FlowableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowable);


        Flowable.interval(1, TimeUnit.SECONDS).onBackpressureBuffer().subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                D.showShort(aLong.toString());
            }
        });
    }
}
