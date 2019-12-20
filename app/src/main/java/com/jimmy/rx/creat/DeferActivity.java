package com.jimmy.rx.creat;

import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hjg.toast.D;
import com.jakewharton.rxbinding2.view.RxView;
import com.jimmy.rx.R;
import com.jimmy.tool.LogUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.security.auth.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class DeferActivity extends AppCompatActivity {

    private static final String TAG = "DeferActivity";
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    Button button2;
    private int a;

    /**
     * Defer操作符会一直等待直到有观察者订阅它，然后它使用Observable工厂方法生成一个Observable。
     * 它对每个观察者都这样做，因此尽管每个订阅者都以为自己订阅的是同一个Observable，事实上每个订阅者获取的是它们自己的单独的数据序列。
     * <p>
     * 当接口请求结束之后，调用订阅，这个时候之前创建的defer被观察者才会创建实例。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defer);
        ButterKnife.bind(this);
        a = 10;
        //创建一个观察者
        final Observable<String> observable = Observable.just("just result: " + a);

        /*当被观察者被创建，值已经被保存*/
//        RxView.clicks(button).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) throws Exception {
//                observable.subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) {
//                        D.showShort(s);
//                    }
//                });
//            }
//        });
        a = 10;
        //创建一个被订阅之后才会被创建的观察者
        final Observable<String> observableDefer = Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                return Observable.just("just result: " + a);
            }
        });
        a = 12;

        /*当被订阅的时候才会创建被观察者的实例，这时候的a数值是12了。 */
        RxView.clicks(button2).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                observableDefer.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d("DeferActivity", s);
                    }
                });
            }
        });


        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "integer:" + integer);
            }
        });

        String[] strings = new String[]{"1", "2", "3", "4"};

        Observable.fromIterable(Arrays.asList(1, 2, 3, 4, 5)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "fromIterable-integer:" + integer);
            }
        });
        Observable.fromArray(1, 2, 3, 4).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "fromArray-integer:" + integer);
            }
        });
        Observable.fromArray(strings).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("DeferActivity", "fromArray-数组-integer:" + s);
            }
        });

        Observable.just(1, 2, 3, 4, 5).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "just-integer:" + integer);
            }
        });
        Observable.range(1, 10).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "range-integer.longValue():" + integer.longValue());
            }
        });

        Observable.range(1, 3).delay(1, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "repeat-integer:" + integer);
            }
        });
        Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d("DeferActivity", "aLong:" + aLong);
            }
        });

        Observable.range(1, 3).doOnEach(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d("DeferActivity", "doOnEach-integer:" + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("DeferActivity", "doOnEach-Consumer-integer:" + integer);
            }
        });

        Observable.range(1, 3).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer == 1) {
                    throw new RuntimeException("Item exceeds maximum value");
                }
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d("DeferActivity", "doOnNext-integer:" + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("DeferActivity", "doOnNext-integer:" + e.getMessage());

            }

            @Override
            public void onComplete() {

            }
        });


        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                LogUtils.d("顺序", "被观察者emitter：线程选择就近原则，链式之后就近原则。io线程");
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        LogUtils.d("顺序", "doOnSubscribe：订阅之后立即执行。线程选择在链式之后就近原则，如果后面没有写，那么跟被观察者同线程执行。主线程");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());

        RxView.clicks(button).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                observable1.observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.d("顺序", "观察者onSubscribe方法，被订阅之后立即执行");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        LogUtils.d("顺序", "观察者中onNext方法");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("顺序", "观察者中onComplete方法");
                    }
                });
            }
        });

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                emitter.requested();
                Log.d("Flowable", "current requested: " + emitter.requested());

            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1000);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d("Flowable", "integer:" + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.d("Flowable", "integer:onError");

            }

            @Override
            public void onComplete() {
                Log.d("Flowable", "integer:onComplete");

            }
        });


        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "First requested = " + emitter.requested());
                emitter.onNext(1);
//                boolean flag;
//                for (int i = 0; ; i++) {
//                    flag = false;
//                    while (emitter.requested() == 0) {
//                        if (!flag) {
//                            Log.d(TAG, "Oh no! I can't emit value!");
//                            flag = true;
//                        }
//                    }
//                    emitter.onNext(i);
//                    Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
//                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });


        Flowable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).groupBy(new Function<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer integer) throws Exception {
                return integer % 3 == 0;
            }
        }).subscribe(new Consumer<GroupedFlowable<Boolean, Integer>>() {
            @Override
            public void accept(GroupedFlowable<Boolean, Integer> booleanIntegerGroupedFlowable) throws Exception {
                booleanIntegerGroupedFlowable.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, booleanIntegerGroupedFlowable.getKey() + " : " + integer);
                    }
                });
            }
        });

        Flowable<Integer> q = Flowable.just(1, 2, 3);
        Flowable<Integer> w = Flowable.just(4, 5, 6, 7);
        Flowable<Integer> e = Flowable.just(7, 8, 9);

        Flowable.combineLatest(q, w, e, new Function3<Integer, Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2, Integer integer3) throws Exception {
                return integer.toString() + integer2.toString() + integer3.toString();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "Flowable + combineLatest : " + s);
            }
        });
        Flowable.mergeArray(q, w).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "merge-integer:" + integer);
            }
        });

        Flowable.concat(q, w).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "concat-integer:" + integer);
            }
        });


        q.startWith(w).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "startWith-integer:" + integer);
            }
        });

        Flowable.empty().switchIfEmpty(w).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "switchIfEmpty:" + o.toString());
            }
        });


        Flowable.zip(q, w, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return integer.toString() + integer2.toString();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "zip:" + s);
            }
        });


//        Observable.catch
    }
}