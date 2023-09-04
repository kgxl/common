package com.kgxl.toalive.open;

import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zjy on 2023/8/14
 */
public class AmsQHooker extends AmsHooker {

    @Override
    public Object getHookObj() {
        Class amNativeClass = null;
        try {
            amNativeClass = ReflectUtils.getClass("android.app.ActivityTaskManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 获取gDefault实例
        try {
            return ReflectUtils.readStaticField(amNativeClass, "IActivityTaskManagerSingleton");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Field getHookField() {
        try {
            return ReflectUtils.getField(ReflectUtils.getClass("android.util.Singleton"), "mInstance");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getTarget() {
        try {
            return ReflectUtils.getClass("android.util.Singleton").getDeclaredMethod("get").invoke(getHookObj());
        } catch (IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class[] getInterfaces() {
        try {
            return new Class[]{ReflectUtils.getClass("android.app.IActivityTaskManager")};
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IBinder getIBinder() {
        Object proxy = null;
        try {
            proxy = ReflectUtils.getClass("android.util.Singleton").getDeclaredMethod("get").invoke(getHookObj());
            Class proxyClass = proxy.getClass();
            System.out.println("proxy = " + proxy);
            System.out.println("pc = " + proxyClass);
            Method asBinder = ReflectUtils.getMethod(proxyClass, "asBinder");
            IBinder mRemote = (IBinder) asBinder.invoke(proxy);
            return mRemote;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
