package day3.study.jiyun.com.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //TODO 增加新功能
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        runThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        print("输");
    }
    public void print(String str){
        System.out.print(str);
    }
    public void runThread(Runnable runnable){
        new Handler().post(runnable);
    }
    private void initView() {
        mBtn = (Button) findViewById(R.id.mBtn);

        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtn:
                integerArrayToString(new Integer[]{1,2,3,45,6,7,7,8,9});
                break;
        }
    }
    private void integerArrayToString(Integer[] arr){
        final StringBuffer sb = new StringBuffer();
        Observable.fromArray(arr).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer+"";
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                sb.append(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e("TAG",sb.toString());
            }
        });
    }
    private void stringArrayToInteger(String[] arr){
        Observable.fromArray(arr).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return Integer.valueOf(s);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e("TAG",integer+10+"");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    private void stringToInteger(final String number){
        Observable.fromCallable(new Callable<String>() {//设置数据来源
            @Override
            public String call() throws Exception {
                return number;
            }
        }).flatMap(new Function<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(final String s) throws Exception {
                return Observable.fromCallable(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return Integer.valueOf(s);
                    }
                });
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e("TAG",integer+"");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    private List<Bitmap> mList = new ArrayList<>();
    private void loadImage(){
        //创建被观察者
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return Environment.getExternalStorageDirectory().getAbsolutePath()+"/mCache/";
            }
        }).flatMap(new Function<String, Observable<File>>() {//转换

            @Override
            public Observable<File> apply(String s) throws Exception {
                File file = new File(s);
                return Observable.fromArray(file.listFiles());
            }
        }).map(new Function<File, Bitmap>() {//循环+转换
            @Override
            public Bitmap apply(File file) throws Exception {
                InputStream stream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            }
            //订阅
        }).subscribeOn(Schedulers.io())//切换io线程
                .observeOn(Schedulers.newThread())//切换到新的子线程
//                .observeOn(AndroidSchedulers.mainThread())//切换到主线程
                .subscribe(new Observer<Bitmap>() {//观察者
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap bitmap) {
                Log.e("TAG",bitmap.toString());
                mList.add(bitmap);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.e("TAG","转换中出现错误");

            }

            @Override
            public void onComplete() {
                //显示到ListView中
                Log.e("TAG","我们转换了"+mList.size()+"张图片");
            }
        });
    }
    private void show() {
        //1、创建观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.e("TAG",s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        //2、创建被观察者
        //方式一
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("张三");
                emitter.onNext("李四");
                emitter.onNext("王五");
                emitter.onNext("赵六");
            }
        });
        //方式二
        Observable<String> observable12 = Observable.just("张三","李四","王五","赵六");
        //方式三
        String[] arr = {"张三","李四","王五","赵六"};
        Observable<String> observable1 = Observable.fromArray(arr);
        //方式四
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "张三";
            }
        });
        //3、订阅
        observable.subscribe(observer);
    }
}
