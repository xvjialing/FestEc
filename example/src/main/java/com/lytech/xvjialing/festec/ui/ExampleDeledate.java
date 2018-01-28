package com.lytech.xvjialing.festec.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lytech.xvjialing.festec.R;
import com.lytech.xvjialing.latte.delegates.LatteDelegate;
import com.lytech.xvjialing.latte.net.RestClient;
import com.lytech.xvjialing.latte.net.callback.IError;
import com.lytech.xvjialing.latte.net.callback.IFailure;
import com.lytech.xvjialing.latte.net.callback.IRequest;
import com.lytech.xvjialing.latte.net.callback.ISuccess;

/**
 * Created by xvjialing on 2018/1/22.
 */

public class ExampleDeledate extends LatteDelegate {

    private static final String TAG = ExampleDeledate.class.getSimpleName();
    
    @Override
    public Object setLayout() {
        return R.layout.delegate_example;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        testRestClient();
    }

    private void testRestClient() {
        RestClient.builder()
                .url("http://news.baidu.com/")
//                .params("", "")
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .onRequest(new IRequest() {
                    @Override
                    public void onRestartStart() {

                    }

                    @Override
                    public void onRequestEnd() {

                    }
                })
                .build()
                .get();
    }
}
