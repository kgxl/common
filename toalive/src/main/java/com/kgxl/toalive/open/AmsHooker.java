package com.kgxl.toalive.open;

import android.os.IBinder;

import java.lang.reflect.Field;

/**
 * Created by zjy on 2023/8/14
 */
public abstract class AmsHooker {
    // 通过反射，将am替换成proxy
    public void hookAms(Object proxy) {
        try {
            Object hookObj = getHookObj();
            Field hookField = getHookField();
            if (hookObj != null && hookField != null && proxy != null) {
                hookField.set(hookObj, proxy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 即IActivityManagerSingleton实例
    public abstract Object getHookObj();

    // 即mInstance
    public abstract Field getHookField();

    // 即am
    public abstract Object getTarget();

    // 接口，用来创建Proxy
    public abstract Class[] getInterfaces();

    //获取通新的binder
    public abstract IBinder getIBinder();
}