package com.jimmy.rx.combine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimmy.rx.R;
import com.jimmy.tool.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 使用 Merge 操作符你可以将多个Observables的输出合并，就好像它们是一个单个的 Observable一样。
 * Merge 可能会让合并的Observables发射的数据交错(有一个类似的操作符 Concat 不会让数据交错，
 * 它会按顺序一个接着一个发射多个Observables的发射物)。
 * 任何一个原始Observable的 onError 通知会被立即传递给观察者，而且 会终止合并后的Observable。
 * <p>
 * 在很多ReactiveX实现中还有一个叫 MergeDelayError 的操作符，它的行为有一点不同，它会 保留 onError 通知直到合并后的Observable所有的数据发射完成，在那时它才会
 * 把 onError 传递给观察者。
 * RxJava将它实现为 merge , mergeWith 和 mergeDelayError 。
 */
public class MergeActivity extends AppCompatActivity {


    @BindView(R.id.execute)
    Button execute;
    @BindView(R.id.tv_result_show)
    TextView tvResultShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        ButterKnife.bind(this);

        //这里次序不一定。可能是1，3，5，2，4，6 .  可能是 2，4，6，1，3，5
        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Integer> odds = Observable.just(1, 3, 5).subscribeOn(Schedulers.io());
                Observable<Integer> evens = Observable.just(2, 4, 6).subscribeOn(Schedulers.io());
                Observable.merge(odds, evens).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        tvResultShow.setText("");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        tvResultShow.setText(tvResultShow.getText().toString() + integer + ",");
                        //这里次序不一定。可能是1，3，5，2，4，6 .  可能是 2，4，6，1，3，5
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }

}
