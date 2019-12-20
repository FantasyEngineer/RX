package com.jimmy.rx.延时和轮询;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.jimmy.rx.People;
import com.jimmy.rx.R;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;

public class TimeIntervalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_interval);
        Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                .flatMap(new Function<Long, Observable<List<Long>>>() {
                    @Override
                    public Observable<List<Long>> apply(Long aLong) throws Exception {
                        Log.d("TimeIntervalActivity", "timer结束");
                        return Observable.interval(1, TimeUnit.SECONDS).buffer(10, TimeUnit.SECONDS);
                    }
                }).observeOn(Schedulers.io()).subscribe(new Consumer<List<Long>>() {
            @Override
            public void accept(List<Long> o) throws Exception {
                Thread.sleep(10000);
                Log.d("TimeIntervalActivity", "这里是轮询" + o.toString());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });


        /*range在范围内发射,repeat为重复多少次*/
        Observable.range(1, 3).repeat(2).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d("TimeIntervalActivity", "integer:" + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d("TimeIntervalActivity", "onComplete");

            }
        });


        Observable.just(1, 2, 3, 4, 5, 6).flatMap(new Function<Integer, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Integer integer) throws Exception {
                if (integer == 5) {
                    return Observable.just(integer).timer(5, TimeUnit.SECONDS);
                }
                return Observable.just(integer);
            }
        }).timeout(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.d("TimeIntervalActivity", "o:" + o.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("TimeIntervalActivity", throwable.getMessage() + "121");
                    }
                });

        Observable.just(1, 2, 3, 4, 5, 6).timestamp().subscribe(new Consumer<Timed<Integer>>() {
            @Override
            public void accept(Timed<Integer> integerTimed) throws Exception {
                Log.d("TimeIntervalActivity", "integerTimed:" + integerTimed);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("TimeIntervalActivity", "integerTimed:" + throwable.getMessage());
            }
        });
        Observable.just(1, 2, 3, 4, 5, 6).count().cache();
        Observable.just(1, 2, 3, 4, 5, 6).toList().subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d("TimeIntervalActivity", "integers-toList:" + integers);
            }
        });
        Observable.just(1, 2, 3, 4, 5, 6).toMap(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "这是key" + integer;
            }
        }).subscribe(new Consumer<Map<String, Integer>>() {
            @Override
            public void accept(Map<String, Integer> objectIntegerMap) throws Exception {
                for (Map.Entry<String, Integer> a : objectIntegerMap.entrySet()) {
                    Log.d("TimeIntervalActivity", a.getKey() + " : " + a.getValue());
                }
            }
        });

        Observable.just(1, 2, 3, 4, 5, 6).toMultimap(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer integer) throws Exception {
                switch (integer) {
                    case 1:
                    case 2:
                    case 3:
                        return "key";
                    default:
                        return "key1";
                }
            }
        }, new Function<Integer, Object>() {
            @Override
            public Object apply(Integer integer) throws Exception {
                return integer + 10;
            }
        }).subscribe(new Consumer<Map<Object, Collection<Object>>>() {
            @Override
            public void accept(Map<Object, Collection<Object>> objectCollectionMap) throws Exception {
                Log.d("TimeIntervalActivity", objectCollectionMap.toString());
            }
        });


        Observable.just(1, 2, 3, 4, 5, 6).toSortedList().subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d("TimeIntervalActivity", "toSortedList:" + integers);
            }
        });

        Observable.just(3, 1, 2, 3, 4, 5, 6).toSortedList(2).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d("TimeIntervalActivity", "toSortedList:" + integers);
            }
        });
        Observable.just("nihao", "zz", "hello", "hi").toSortedList().subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> strings) throws Exception {
                Log.d("TimeIntervalActivity", "strings:" + strings);
            }
        });


        People p0 = new People(4, "zzz");
        People p1 = new People(20, "hhh");
        People p2 = new People(4, "yyy");
        Observable.just(p0, p1, p2).toSortedList(new Comparator<People>() {
            @Override
            public int compare(People o1, People o2) {
                if (o1.getAge() > o2.getAge()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }).subscribe(new Consumer<List<People>>() {
            @Override
            public void accept(List<People> people) throws Exception {
                Log.d("TimeIntervalActivity", "people:" + people.toString());
            }
        });


        Observable.just(3, 1, 2, 3, 4, 5, 6).takeUntil(new Observable<Object>() {
            @Override
            protected void subscribeActual(Observer<? super Object> observer) {
                observer.onNext("121213");
                observer.onComplete();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TimeIntervalActivity", "integer:" + integer);
            }
        });
    }
}
