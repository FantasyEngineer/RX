package com.jimmy.rx.combine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jimmy.rx.R;
import com.jimmy.tool.StrUtils;
import com.jimmy.tool.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * CombineLatest一般用于联合判断。
 * 多个observable结果交叉，不想是zip，是两两相合。不像是merge，是直接加到序列里。
 */

public class CombineLatestActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.age)
    EditText age;
    @BindView(R.id.sex)
    EditText sex;
    @BindView(R.id.click)
    Button click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_latest);
        ButterKnife.bind(this);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("点击");
            }
        });

        //联合判断
        Observable<CharSequence> observable1 = RxTextView.textChanges(name);
        Observable<CharSequence> observable2 = RxTextView.textChanges(age);
        Observable<CharSequence> observable3 = RxTextView.textChanges(sex);
        Observable.combineLatest(observable1, observable2, observable3, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                return StrUtils.isNotEmpty(charSequence.toString()) && StrUtils.isNotEmpty(charSequence2.toString()) && StrUtils.isNotEmpty(charSequence3.toString());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isEnable) throws Exception {
                        if (isEnable) {
                            click.setEnabled(true);
                            click.setText("可以点击");
                        } else {
                            click.setEnabled(false);
                            click.setText("不可以点击");
                        }
                    }
                });

    }
}
