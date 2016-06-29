package com.cwf.demo.rxjavademo;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.cwf.libs.okhttplibrary.OkHttpClientManager;
import com.cwf.libs.okhttplibrary.callback.ResultCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private GridView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        image = (ImageView) findViewById(R.id.image);
        listView = (GridView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((MyAdapter) listView.getAdapter()).getItem(position).getUrl();
                OkHttpClientManager.getInstance().downloadFile(url, getApplicationContext().getExternalCacheDir().getAbsolutePath(), new ResultCallBack() {
                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onSuccess(String s) {

                    }
                });
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        String[] string = new String[]{"A", "bA", "CC"};
        /*from*/
        Observable.from(string)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("test", s);
                    }
                });

        /*rxoermissions*/
        RxPermissions.getInstance(getApplicationContext()).request(Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /*base*/
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("onNext");
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e("Test", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Test", e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e("Test", s);
            }
        });

        /*Thread*/
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e("just", integer + "");
                    }
                });

        /*map*/
        Observable.just("ABCDEFG")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s.substring(0, s.length() - 2);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("Func1", s);
                    }
                });

        RxBus.get().register(this);
        RxBus.get().post("你好！");
        RxBus.get().post(new User("ME", "Here"));

        OkHttpClientManager.getInstance().get("http://gank.io/api/data/福利/100/1", new ResultCallBack() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);

                    Gson gson = new Gson();
                    final List<Item> itemList = gson.fromJson(jsonObject.getString("results"), new TypeToken<List<Item>>() {
                    }.getType());

                    Subscriber<DrawableTypeRequest<String>> subscriber = new Subscriber<DrawableTypeRequest<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(DrawableTypeRequest<String> item) {
//                            Log.d("onNext", item);
                            item.into(image);
                        }
                    };
                    listView.setAdapter(new MyAdapter(itemList));
                    Observable.from(itemList)
//                            .subscribeOn(Schedulers.io())
                            .flatMap(new Func1<Item, Observable<DrawableTypeRequest<String>>>() {
                                @Override
                                public Observable<DrawableTypeRequest<String>> call(Item item) {
                                    return Observable.just(Glide.with(MainActivity.this)
                                            .load(item.getUrl()));
                                }
                            })
                            .subscribe(subscriber);
//                    Observable.create(new Observable.OnSubscribe<String>() {
//                        @Override
//                        public void call(Subscriber<? super String> subscriber) {
//                            subscriber.onNext(itemList.get(0).getUrl());
//                            subscriber.onCompleted();
//                        }
//                    }).subscribe(new Observer<String>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(String s) {
//                            Glide.with(MainActivity.this)
//                                    .load(s)
//                                    .into(image);
//                        }
//                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe
    public void eat(String food) {
        // purpose
        Log.e("eat", food);
        RxBus.get().post("tag", "我很好！");
    }

    @Subscribe
    public void getUser(User user) {
        Log.e("getUser", user.getName() + " : " + user.getAddress());
        RxBus.get().post(user.getName());
        RxBus.get().post("tag", user.getAddress());
    }


    @Subscribe(
            thread = EventThread.NEW_THREAD,
            tags = {
                    @Tag("tag")
            }
    )
    public void eatMore1(String food) {
        // purpose
        Log.e("eatMore1", food);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

}
