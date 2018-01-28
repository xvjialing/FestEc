package com.lytech.xvjialing.latte.net;

import com.lytech.xvjialing.latte.net.callback.IError;
import com.lytech.xvjialing.latte.net.callback.IFailure;
import com.lytech.xvjialing.latte.net.callback.IRequest;
import com.lytech.xvjialing.latte.net.callback.ISuccess;
import com.lytech.xvjialing.latte.net.callback.RequestCallBacks;

import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by xvjialing on 2018/1/22.
 */

public class RestClient {

    private final String URL;
    private final WeakHashMap<String,Object> PARAMS=RestCreator.getParams();
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final RequestBody BODY;


    public RestClient(String url,
                      Map<String, Object> params,
                      IRequest request,
                      ISuccess success,
                      IFailure failure,
                      IError error,
                      RequestBody body) {
        this.URL = url;
        this.PARAMS.putAll(params);
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.BODY = body;
    }

    public static RestClientBuilder builder(){
        return new RestClientBuilder();
    }

    private void request(HttpMethod httpMethod){
        final RestService restService=RestCreator.getRestService();
        Call<String> call=null;

        if (REQUEST!=null){
            REQUEST.onRestartStart();
        }

        switch (httpMethod){
            case GET:
                call=restService.get(URL,PARAMS);
                break;
            case POST:
                call=restService.post(URL,PARAMS);
                break;
            case PUT:
                call=restService.put(URL,PARAMS);
                break;
            case DELETE:
                call=restService.delete(URL,PARAMS);
                break;
            case UPLOAD:
                break;
            case PUT_RAW:
                break;
            case POST_RAW:
                break;
            default:
                break;
        }

        if (call!=null){
            call.enqueue(getRequestCallbak());
        }
    }

    private Callback<String> getRequestCallbak(){
        return new RequestCallBacks(REQUEST,SUCCESS,FAILURE,ERROR);
    }

    public final void get(){
        request(HttpMethod.GET);
    }

    public final void post(){
        request(HttpMethod.POST);
    }

    public final void put(){
        request(HttpMethod.PUT);
    }

    public final void delete(){
        request(HttpMethod.DELETE);
    }
}
