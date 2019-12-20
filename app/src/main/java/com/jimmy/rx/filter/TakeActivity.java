package com.jimmy.rx.filter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.jimmy.rx.BaseActivity;
import com.jimmy.rx.R;
import com.jimmy.tool.LogUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.fuseable.ScalarCallable;
import io.reactivex.internal.util.SorterFunction;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;


public class TakeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        /*这里使用just操作符发射数据*/
        /*使用take进行限制，只发送前几个数据*/
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).take(3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Log.d("TakeActivity", "integer.intValue():" + integer.intValue());
            }
        });


        Observable.just(1, 2, 3).take(4).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Log.d("TakeActivity", "integer" + integer.intValue());
            }
        });

        //数据源为空的时候，不会触发回调
        Observable.empty().take(4).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.d("数据源为空" + o.toString());

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.d("数据源为空" + throwable.getMessage());
            }
        });

        //可以接收时长，规定时间内的数据被发送，其他忽略
        Observable.interval(1, TimeUnit.SECONDS).take(5, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d("TakeActivity", "时间内接收到的数据:" + aLong);
            }
        });



        /*takelast*/
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).takeLast(3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Log.d("TakeActivity", "返回最后几个数值:" + integer.intValue());
            }
        });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (int i = 0; i <= 5; i++) {
                    emitter.onNext(i + "");
                    Thread.sleep(1000);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).takeLast(2, TimeUnit.SECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity", "返回最后时间段发送的数据" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("TakeActivity", "返回最后时间段发送的数据" + throwable.getMessage());
            }
        });


//      startwith  首部增加一个item
        Observable.just(1, 2).startWith(0).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Log.d("TakeActivity", "首部增加:" + integer.intValue());
            }
        });

        List arrayDeque = Arrays.asList(3, 4);
        Observable.just(1, 2).startWith(arrayDeque).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("TakeActivity", "首部增加list:" + o.toString());
            }
        });


        /*在首部插入一个observable的数据*/
        Observable<Integer> ob = Observable.just(1, 2);
        Observable.just(3, 4).startWith(ob).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "首部增加Observable:" + integer.toString());
            }
        });


        /*首部插入一系列数据*/
        List arrayDeque1 = Arrays.asList(3, 4);
        Observable.fromIterable(arrayDeque1).startWithArray(1, 2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "首部插入Array:" + integer.toString());
            }
        });

        /*filter操作符，添加一个拦截器*/
        Observable.just(1, 2, 3, 4, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                if (integer % 2 == 0) {
                    return true;
                } else
                    return false;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Log.d("TakeActivity", "filter过滤能对2取余的数据:" + integer);
            }
        });

        /*ofType只返回对应类型的数据*/
        Observable.just("字符串类型", 1, 2, 3, 10.0f).ofType(String.class).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity", "ofType筛选的规定的类型数据-->" + s);
            }
        });


        //first操作符，取通知中的第一条数据，括号内的为默认值。如果序列中为空，那么发送默认值。
        //last操作符与之相反，取最后一个条数据
        Observable.just(0, 1, 2, 3, 4).first(-1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("TakeActivity", "first操作符---" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("TakeActivity", "first操作符---" + throwable.getMessage());
                    }
                });
        Observable.just(0, 1, 2, 3, 4).firstElement().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "firstElement:" + integer);
            }
        });

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                emitter.onComplete();
            }
        }).firstElement().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("TakeActivity", "firstElement+empty:" + o);
            }
        });

        Observable.empty().firstElement().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                Log.d("TakeActivity", "firstElement+empty:" + o);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.d("TakeActivity", "firstElement+empty：" + throwable.getMessage());
            }
        });


        Observable.empty().first("1").subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object s) throws Exception {
                Log.d("TakeActivity", "first操作符-用了默认值--" + s.toString());
            }
        });


        /*single(defalut)操作符 只有序列中有一个数据的时候才能取，
        只取第一项,如果有多项目，抛异常IllegalArgumentException: Sequence contains more than one element!*/
        Observable.just(0, 2, 3).single(1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                LogUtils.d("single操作符---" + integer.toString());

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.d("single操作符---" + throwable.toString());

            }
        });

        Observable.just(0).single(1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "single操作符---" + integer.toString());
            }
        });

        Observable.empty().single(1).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.d("single操作符--数据为空-" + o.toString());
                /*    │ single操作符--数据为空-1*/
            }
        });

        //ignoreElements  忽略所有，直接跳到完成，使用Action回调函数
        Observable.just(1, 2, 3)
                .ignoreElements()
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.d("ignoreElements结束");
                    }
                });


        /*last*/
        Observable.just(1, 2, 3).last(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "last--" + integer);
            }
        });

        /*lastElement*/
        Observable.just(1, 2, 3).lastElement().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "lastElement--" + integer);
            }
        });


        Observable.just(1, 2, 3).skip(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "skip:" + integer);
            }
        });


        Observable.just(1, 2, 3).skipLast(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "skip:" + integer);
            }
        });


        List arrayDeque2 = Arrays.asList(3, 4, 2, 3);
        Observable.fromIterable(arrayDeque2).distinct().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("TakeActivity", o.toString());
            }
        });

        Observable.just(1, 2, 2, 3, 4, 4, 5).distinct(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer integer) throws Exception {
                return 3 > integer ? "tag1" : "tag2";//这里返回key值，小于3的key值是第一组，也就是说1和2的key值都是第一组，只会将1提交给订阅者，2的key值与1相同就直接被过滤掉了，这个变体是根据key值进行过滤的
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "distinct+integer:" + integer);
            }
        });

        Observable.just(1, 2, 2, 3, 4, 3, 5).distinctUntilChanged().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "integer:" + integer);
            }
        });


        Observable.just(1, 2, 2, 3, 4, 3, 5).elementAt(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "elementAt:" + integer);
            }
        });


        Observable.just(1, 2, 2, 3, 4, 3, 5).elementAt(10, 999).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "elementAt:" + integer);
            }
        });
        Observable.just(1, 2, 2, 3, 4, 3, 5).elementAtOrError(10).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "elementAt:" + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.d("TakeActivity", "elementAtOrError" + throwable.getMessage());
            }
        });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (int i = 0; i <= 30; i++) {
                    emitter.onNext(i + "");
                    Thread.sleep(1000);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).sample(5, TimeUnit.SECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity", "sample+" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("TakeActivity", "sample+" + throwable.getMessage());
            }
        });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (int i = 0; i <= 31; i++) {
                    emitter.onNext(i + "");
                    Thread.sleep(1000);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).throttleFirst(5, TimeUnit.SECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity", "throttleFirst+" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("TakeActivity", "throttleFirst+" + throwable.getMessage());
            }
        });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (int i = 0; i <= 31; i++) {
                    emitter.onNext(i + "");
                    Thread.sleep(1000);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).throttleLast(5, TimeUnit.SECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity", "throttlelast+" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("TakeActivity", "throttlelast+" + throwable.getMessage());
            }
        });

        Observable.just(1, 2, 2, 3, 4, 3, 5).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                if (integer > 0) {
                    return true;
                }
                return false;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d("TakeActivity", "aBoolean:" + aBoolean);
            }
        });


        Observable<String> a = Observable.timer(1, TimeUnit.SECONDS).just("这里是延时一秒的1", "这里是延时一秒的2", "这里是延时一秒的3");
        Observable<String> b = Observable.timer(2, TimeUnit.SECONDS).just("这里是延时2秒的", "这里是延时2秒的");

        List<Observable<String>> list = Arrays.asList(a, b);
        Observable.amb(list).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("TakeActivity---amb", o.toString());
            }
        });

        //等同于list
        Observable.ambArray(a, b).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity---amb", s.toString());
            }
        });


        Observable.just(1, 2, 2, 3, 4, 3, 5).contains(0).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d("TakeActivity-contains", "aBoolean:" + aBoolean);
            }
        });

        Observable.just(1, 2, 2, 3, 4, 3, 5).isEmpty().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d("TakeActivity-isEmpty", "aBoolean:" + aBoolean);
            }
        });

        //window这里将just发送的每一项，变成了一个个单独的Observable，然后进行延时操作。
        Observable.just(1, 2, 3, 4, 5).window(3).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> integerObservable) throws Exception {
                return integerObservable.delay(2, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "window-integer:" + integer);
            }
        });

        Observable.interval(1, TimeUnit.SECONDS).take(10).window(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Observable<Long>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Observable<Long> longObservable) {
                        Log.d("TakeActivity", "window-time - onNext");
                        longObservable.subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.d("TakeActivity", "window-time - aLong:" + aLong.longValue());
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TakeActivity", "window-time - onComplete");

                    }
                });


        Observable.just(1, 2, 3, 4, 5).buffer(3).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d("TakeActivity", "buffer-integers:" + integers);
            }
        });

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).buffer(4, 6).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d("TakeActivity", "buffer-skip-integers:" + integers);
            }
        });


        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just("变成了字符串" + integer);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity-flatMap", s);
            }
        });

        Observable.interval(1, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(Long aLong) throws Exception {
                return Observable.just(aLong).delay(3, TimeUnit.SECONDS);
            }
        }, 2).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long o) throws Exception {
                Log.d("TakeActivity", "flatMap - maxConcurrency:" + o);
            }
        });


        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).concatMap(new Function<Integer, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Integer integer) throws Exception {
                return Observable.just("转换成字符串且有循序：" + integer);
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("TakeActivity", "concatMap-o:" + o);
            }
        });

        Observable.interval(0, 5, TimeUnit.SECONDS).switchMap(new Function<Long, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Long aLong) throws Exception {
                Log.d("TakeActivity", "switchMap-o:主流发送的数据发送" + aLong);
                return Observable.interval(1, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("TakeActivity", "switchMap-o:从流开始发送数据" + o);
            }
        });


        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer + "-->转化字符串";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("TakeActivity", "普通map" + s);
            }
        });

        Observable.range(1, 5).cast(Integer.class).subscribe(new Observer<Integer>() {

            @Override
            public void onError(Throwable e) {
                LogUtils.d("------>onError()" + e);
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer i) {
                Log.d("TakeActivity", "------>onNext()" + i);
            }
        });

        Observable.range(1, 5).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TakeActivity", "scan-integer:" + integer);
            }
        });

    }
}
