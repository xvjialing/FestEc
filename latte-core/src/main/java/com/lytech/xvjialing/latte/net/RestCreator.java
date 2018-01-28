package com.lytech.xvjialing.latte.net;

import com.lytech.xvjialing.latte.app.ConfigType;
import com.lytech.xvjialing.latte.app.Latte;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by xvjialing on 2018/1/22.
 */

public class RestCreator {

    private static final class ParamsHolder {
        public static final WeakHashMap<String,Object> PARAMS = new WeakHashMap<String,Object>();
    }

    public static WeakHashMap<String,Object> getParams(){
        return ParamsHolder.PARAMS;
    }


    public static RestService getRestService() {
        return RestServiceHolder.REST_SERVICE;
    }

    private static final class RetrofitHolder {
        private static final String BaseUrl = (String) Latte.getConfigurations().get(ConfigType.API_HOST.name());
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(OKHTTP_HOLDER.OK_HTTP_CLIENT)
                .build();
    }

    private static final class OKHTTP_HOLDER {
        private static final int TIME_OUT = 60;
        private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
    }


    private static final class RestServiceHolder {
        private static final RestService REST_SERVICE =
                RetrofitHolder.RETROFIT_CLIENT.create(RestService.class);
    }
}
