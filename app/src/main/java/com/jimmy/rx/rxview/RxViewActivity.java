package com.jimmy.rx.rxview;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.hjg.toast.D;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.view.ViewScrollChangeEvent;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jimmy.rx.R;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class RxViewActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.touch)
    Button touch;
    @BindView(R.id.edittext)
    EditText edittext;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_view);
        ButterKnife.bind(this);

        /*单击*/
        RxView.clicks(button).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        D.showShort("单击");
                    }
                });

        /*长安*/
        RxView.longClicks(button, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;//false，不会触发长按， true，会触发
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                D.showShort("长按");
            }
        });


        /*触摸*/
        RxView.touches(touch).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                D.showShort("触摸");
            }
        });

        /*2秒内连续输入的会被忽略*/
        RxTextView.textChanges(edittext).debounce(2, TimeUnit.SECONDS)
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                D.showShort(s);
            }
        });

        RxTextView.editorActions(edittext)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("RxViewActivity", edittext.getText().toString());
                        Log.d("RxViewActivity", "integer:" + integer);
                    }
                });


        try {
            RxTextView.color(button).accept(Color.parseColor("#00ff00"));
            RxTextView.text(button).accept("动态改变");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //滑动时触发
        RxView.scrollChangeEvents(scrollView)
                .subscribe(new Consumer<ViewScrollChangeEvent>() {
                    @Override
                    public void accept(ViewScrollChangeEvent viewScrollChangeEvent) throws Exception {
                        Log.d("RxViewActivity", "viewScrollChangeEvent:" + viewScrollChangeEvent);
                    }
                });

    }
}
