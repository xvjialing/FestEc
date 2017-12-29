package com.lytech.xvjialing.festec;

import android.app.Application;

import com.lytech.xvjialing.latte.app.Latte;

/**
 * Created by xvjialing on 2017/12/29.
 */

public class ExampleApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Latte.init(this)
                .withApiHost("http://127.0.0.1")
                .configure();
    }
}
