package com.lytech.xvjialing.latte.app;

import android.content.Context;

import java.util.WeakHashMap;

/**
 * Created by xvjialing on 2017/12/27.
 */

public final class Latte {  // 加上final杜绝继承与修改

    public static Configurator init(Context context){
        getConfigurations().put(ConfigType.APPLICATION_CONTEXT.name(),context.getApplicationContext());
        return Configurator.getInstance();
    }

    private static WeakHashMap<String,Object> getConfigurations(){
        return Configurator.getInstance().getLatteConfigs();
    }




}
