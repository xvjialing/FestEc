

# Latte



![项目架构](http://p1i22rxiz.bkt.clouddn.com/Latte_structure.jpg)

| module      | 作用                                       | library类型       |
| ----------- | ---------------------------------------- | --------------- |
| 注解module    | 提供代码生成器所需注解                              | Java Library    |
| 代码生成器module | 从注解获取信息，通过annotationProcessor或apt生成代码    | Java Library    |
| 核心module    | 路由架构、网络请求、照相和二维码及图片剪裁、共性的通用UI、通用的工具、web view的处理、微信登陆和支付、支付宝支付封装、诸多重复性的处理 | Android Library |
| 业务module    | 相应一类业务的特殊UI、相应一类业务需要的通用逻辑、相应一类业务的特殊处理    | Android Library |
| 具体module    | 项目特有的个别功能、只有该项目需要的第三方库、只有该项目会更改的UI及逻辑、需要在application model中使用的的一些签名和验证 | Android Library |

根据以上结构构建项目：

![](http://p1i22rxiz.bkt.clouddn.com/app-structure.jpg)

* 设置各module之间的依赖关系：latte-core依赖latte-annotions，latte-ec依赖latte-core，example依赖latte-ce和latte-compiler。
* 将Moudule：example的build.gradle中的`implementation project(':latte-compiler')`改为`annotationProcessor project(':latte-compiler')`
* 精简build.gradle中的依赖，因为latte-core的build.gradle已经包含了大多数基础的依赖，可以将latte-ec与example的build.gradle中的依赖进行精简。

latte-core的build.gradle的依赖：

```groovy
dependencies {
    api fileTree(include: ['*.jar'], dir: 'libs')
    api 'com.android.support:appcompat-v7:26.1.0'
    api 'junit:junit:4.12'
    api 'com.android.support.test:runner:1.0.1'
    api 'com.android.support.test.espresso:espresso-core:3.0.1'
    api 'com.android.support.constraint:constraint-layout:1.0.2'
    api project(':latte-annotations')
}
```

Latte-ec的build.gradle的依赖：

```groovy
dependencies {
    api fileTree(include: ['*.jar'], dir: 'libs')
    api project(':latte-core')
}
```

example的build.gradle的依赖：

```groovy
dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation project(':latte-ec')
    annotationProcessor project(':latte-compiler')
}
```

> 由于Android Studio 3.0将依赖关系进行了更新，默认是*`implementation`，但这回导致被依赖的包无法访问到当前module中的类，所以应该改成`api`

这样项目的依赖关系就处理好了

***

## 初始化项目：

在latee-core中进行项目配置初始化：

/FestEC/latte-core/src/main/java/com/lytech/xvjialing/latte/app/ConfigType.java

```Java
public enum ConfigType {
    API_HOST,
    APPLICATION_CONTEXT,
    CONFIG_READY,
    ICON,

}
```

/FestEC/latte-core/src/main/java/com/lytech/xvjialing/latte/app/Configurator.java

```java
import java.util.WeakHashMap;

public class Configurator {

    private static final WeakHashMap<String,Object> LATTE_CONFIGS=new WeakHashMap<>();

    private Configurator(){
        LATTE_CONFIGS.put(ConfigType.CONFIG_READY.name(),false);

    }

    private static class Holder{
        private static final Configurator INSTANCE = new Configurator();
    }

    public static Configurator getInstance(){
        return Holder.INSTANCE;
    }

    final WeakHashMap<String,Object> getLatteConfigs(){
        return LATTE_CONFIGS;
    }

    public final void configure(){
        LATTE_CONFIGS.put(ConfigType.CONFIG_READY.name(),true);
    }

    public final Configurator withApiHost(String host){
        LATTE_CONFIGS.put(ConfigType.API_HOST.name(),host);
        return this;
    }

    private void checkConfiguration(){
        final boolean isReady= (boolean) LATTE_CONFIGS.get(ConfigType.CONFIG_READY.name());
        if (!isReady){
            throw new RuntimeException("Configuration is not ready,please call configure");
        }
    }

    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Enum<ConfigType> key){
        checkConfiguration();
        return (T) LATTE_CONFIGS.get(key);
    }
}
```

/FestEC/latte-core/src/main/java/com/lytech/xvjialing/latte/app/Latte.java

```java

import android.content.Context;

import java.util.WeakHashMap;

public final class Latte {  // 加上final杜绝继承与修改

    public static Configurator init(Context context){
        getConfigurations().put(ConfigType.APPLICATION_CONTEXT.name(),context.getApplicationContext());
        return Configurator.getInstance();
    }

    private static WeakHashMap<String,Object> getConfigurations(){
        return Configurator.getInstance().getLatteConfigs();
    }




}
```

在example module中引入配置：

/FestEC/example/src/main/java/com/lytech/xvjialing/festec/ExampleApp.java

```java
import android.app.Application;
import com.lytech.xvjialing.latte.app.Latte;

public class ExampleApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Latte.init(this)
                .withApiHost("http://127.0.0.1")
                .configure();
    }
}
```

最后别忘了在AndroidManifest.xml中加入`android:name=".ExampleApp"`

这样项目配置信息初始化就完成了。

## 界面初始化

在latte-core模块中添加delegates文件夹，在其中新建BaseDelagate

```java
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * Created by xvjialing on 2018/1/11.
 */

public abstract class BaseDelegate extends SwipeBackFragment {

    @SuppressWarnings("SpellCheckingInspection")
    private Unbinder mUnbinder=null;

    public abstract Object setLayout();

    public abstract void onBindView(@Nullable Bundle savedInstanceState,View rootView);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=null;

        if (setLayout() instanceof Integer){
            rootView=inflater.inflate((Integer) setLayout(),container,false);
        }else if (setLayout() instanceof View){
            rootView= (View) setLayout();
        }
        if (rootView!=null){
            mUnbinder= ButterKnife.bind(this,rootView);
            onBindView(savedInstanceState,rootView);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder!=null){
            mUnbinder.unbind();
        }
    }
}
```

再新建PermissionCheckerDelegate继承自BaseDelegate

```java
/**
 * Created by xvjialing on 2018/1/11.
 */

public abstract class PermissionCheckerDelegate extends BaseDelegate {

}
```

再新建LatteDelegate继承自PermissionCheckerDelegate

```java
/**
 * Created by xvjialing on 2018/1/11.
 */

public abstract class LatteDelegate extends PermissionCheckerDelegate{
}
```

再新建activities文件夹，并在其中新建ExampleActivity继承自ProxyActivity

```java
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ContentFrameLayout;

import com.lytech.xvjialing.latte.R;
import com.lytech.xvjialing.latte.delegates.LatteDelegate;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by xvjialing on 2018/1/11.
 */

public abstract class ProxyActivity extends SupportActivity{

    public abstract LatteDelegate setRootDelegate();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initContainer(savedInstanceState);
    }

    private void initContainer(@Nullable Bundle savedInstanceState){
        final ContentFrameLayout container=new ContentFrameLayout(this);
        container.setId(R.id.delegate_container);
        setContentView(container);
        if (savedInstanceState==null){
            loadRootFragment(R.id.delegate_container,setRootDelegate());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        System.runFinalization();
    }
}
```

然后在example模块中新建布局文件delegate_example，再新建ExampleDeledate继承自LatteDelegate

```java
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lytech.xvjialing.festec.R;
import com.lytech.xvjialing.latte.delegates.LatteDelegate;

/**
 * Created by xvjialing on 2018/1/22.
 */

public class ExampleDeledate extends LatteDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_example;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
```

最后新建ExampleActivity继承自ProxyActivity

```java

import com.lytech.xvjialing.latte.activities.ProxyActivity;
import com.lytech.xvjialing.latte.delegates.LatteDelegate;

public class ExampleActivity extends ProxyActivity {

    @Override
    public LatteDelegate setRootDelegate() {
        return new ExampleDeledate();
    }
}
```

自此界面的初始化就完成了。

## 初始化网络



