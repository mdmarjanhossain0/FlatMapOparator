package com.example.test4;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MyTag";
    Disposable disposable;
    Disposable compositeDisposable=new CompositeDisposable();
    androidx.appcompat.widget.SearchView searchView;
    long currentTimeMillis;
    private AdView mAdView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView=findViewById(R.id.searchView);
        currentTimeMillis = System.currentTimeMillis();
        /*    BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);*/
//        obserberTesting();
        serchQueryTesting();
        /*MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus){
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
/*
        mAdView.setAdSize(AdSize.BANNER);

        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");*/
    }

    private void serchQueryTesting() {
        Observable<String> observable=Observable
                .create(new ObservableOnSubscribe<String>(){
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception{
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText){
                        if(!emitter.isDisposed()){
                            emitter.onNext(newText);
                        }
                        return false;
                    }
                  });
                }
            })
                .debounce(1000, TimeUnit.MILLISECONDS);
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable=d;
            }

            @Override
            public void onNext(String s){
                Log.d(TAG, String.valueOf(currentTimeMillis-System.currentTimeMillis()));
                Log.d(TAG,s.toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void obserberTesting() {
        List<Integer> source=new ArrayList();
        source.add(1);
        source.add(2);
        source.add(3);
        List<Integer> iterable=new ArrayList();
        iterable.add(10);
        iterable.add(11);
        iterable.add(12);
        Observable.fromIterable(source)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Integer>(){
                    @Override
                    public Integer apply(Integer integer) throws Exception{
                        Log.d(TAG,"mapping"+Thread.currentThread().getName().toString());
                        return integer*2;
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        Log.d(TAG, "flatmap" + integer + Thread.currentThread().getName().toString());
                        return Observable.fromIterable(iterable)
                                .subscribeOn(Schedulers.io());
                }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"disposable"+Thread.currentThread().getName().toString());
                disposable=d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG,"onnext"+Thread.currentThread().getName().toString());
                Log.d(TAG, String.valueOf(integer));
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage().toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"oncomplete "+Thread.currentThread().getName().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        compositeDisposable.dispose();
  /*      List<Integer> source=new ArrayList();
        source.add(1);
        source.add(2);
        source.add(3);
        List<Integer> iterable=new ArrayList();
        source.add(10);
        source.add(11);
        source.add(12);
        Observable.fromIterable(source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<Integer, Integer>(){
//                    @Override
//                    public Integer apply(Integer integer) throws Exception{
//                        Log.d(TAG,"mapping"+Thread.currentThread().getName().toString());
//                        return integer*2;
//                    }
//                })
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception{
                        return Observable.fromIterable(iterable)
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG,"disposable"+Thread.currentThread().getName().toString());
                        disposable=d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG,"onnext"+Thread.currentThread().getName().toString());
                        Log.d(TAG, String.valueOf(integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"oncomplete "+Thread.currentThread().getName().toString());
                    }
                });*/
    }
}