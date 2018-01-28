package com.lytech.xvjialing.festec.ui;

import com.lytech.xvjialing.latte.activities.ProxyActivity;
import com.lytech.xvjialing.latte.delegates.LatteDelegate;

public class ExampleActivity extends ProxyActivity {

    @Override
    public LatteDelegate setRootDelegate() {
        return new ExampleDeledate();
    }
}
