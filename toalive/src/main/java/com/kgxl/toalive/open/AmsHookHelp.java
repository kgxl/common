package com.kgxl.toalive.open;

import static android.os.Process.myUid;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by kgxl on 2023/8/16
 */
public class AmsHookHelp {
    private AmsHookHelp() {
    }

    public static AmsHookHelp getInstance() {
        return AmsHookHelpHolder.mAmsHookHelp;
    }

    static class AmsHookHelpHolder {
        static AmsHookHelp mAmsHookHelp = new AmsHookHelp();
    }

    private AmsHooker getHooker() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return new AmsQHooker();
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            return new AmsPHooker();
        } else {
            return new AmsNHooker();
        }
    }

    private int getTransactCode() {
        int transactCode = IBinder.FIRST_CALL_TRANSACTION + 2;
        switch (Build.VERSION.SDK_INT) {
            case 26:
            case 27:
            case 28:
                transactCode = IBinder.FIRST_CALL_TRANSACTION + 5;
                break;
            case 29:
            case 30:
            case 31:
            case 32:
                transactCode = IBinder.FIRST_CALL_TRANSACTION;
                break;
            default:
                transactCode = IBinder.FIRST_CALL_TRANSACTION + 2;
                break;
        }
        return transactCode;
    }

    public void realStartAct(Context context, Intent intent, String startActivityStr) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            moveToFront(context);
//            isAllowed(context);
            fullScreen(context, intent);
        } else {
            startAct(context, startActivityStr);
        }
    }

    private boolean isAllowed(Context ctx) {
        AppOpsManager ops = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021;
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
            Integer result = (Integer) method.invoke(ops, op, myUid(), ctx.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;

        } catch (Exception e) {
            Log.e("kgxl", "not support");
        }
        return false;
    }

    public void moveToFront(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> list = activityManager.getAppTasks();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTaskInfo().baseIntent.getComponent().getPackageName().equals(context.getPackageName())) {
                System.out.println("moveToFront " + list.get(i).getTaskInfo());
                list.get(i).moveToFront();
                return;
            }
        }
    }

    private void startAct(Context context, String startActivityStr) {
        AmsHooker amsHooker = getHooker();
        IBinder iBinder = amsHooker.getIBinder();
        int transactCode = getTransactCode();
        Intent intent = new Intent();
        ComponentName component = new ComponentName(context.getPackageName(), startActivityStr);
        intent.setComponent(component);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Parcel data = getRealParcel(context, intent);
        //BinderProxy
        //mRemote指向BinderProxy，而BinderProxy持有C++端的BpBinder，进而借助Binder驱动和AMS通信
        System.out.println("mRemote " + iBinder + "  data :" + data + " code :" + transactCode);
        boolean transact = false;
        try {
            transact = iBinder.transact(transactCode, data, null, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        data.readException();
        System.out.println("transact " + transact);
    }

    private Parcel getRealParcel(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return getQParcel(context, intent);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            return getPParcel(context, intent);
        } else {
            return getNParcel(context, intent);
        }
    }

    private Parcel getPParcel(Context context, Intent intent) {
        Parcel data = Parcel.obtain();
        //写入AMS Binder服务描述信息即android.app.IActivityManager
        data.writeInterfaceToken("android.app.IActivityManager");
        //写入IApplicationThread 匿名Binder服务实体(这个在attachApplication时写入过)
        data.writeStrongBinder(null);
        data.writeString(context.getPackageName());
        data.writeString(null);
        data.writeTypedObject(intent, 0);
        data.writeString(null);
        data.writeStrongBinder(null);
        data.writeString(null);
        data.writeInt(0);
        data.writeInt(0);
        data.writeInt(0);
        return data;
    }

    private Parcel getQParcel(Context context, Intent intent) {
        Parcel data = Parcel.obtain();
        //  _data.writeInterfaceToken(DESCRIPTOR);
        //           _data.writeStrongInterface(caller);
        //           _data.writeString(callingPackage);
        //           _data.writeString(callingFeatureId);
        //           _data.writeTypedObject(intent, 0);
        //           _data.writeString(resolvedType);
        //           _data.writeStrongBinder(resultTo);
        //           _data.writeString(resultWho);
        //           _data.writeInt(requestCode);
        //           _data.writeInt(flags);
        //           _data.writeTypedObject(profilerInfo, 0);
        //           _data.writeTypedObject(options, 0);

        //写入AMS Binder服务描述信息即android.app.IActivityManager
        data.writeInterfaceToken("android.app.IActivityTaskManager");
        //写入IApplicationThread 匿名Binder服务实体(这个在attachApplication时写入过)
        data.writeStrongBinder(null);
        data.writeString(context.getPackageName());
        data.writeString(null);
        data.writeTypedObject(intent, 0);
        data.writeString(null);
        data.writeStrongBinder(null);
        data.writeString(null);
        data.writeInt(0);
        data.writeInt(0);
        data.writeInt(0);
        data.writeTypedObject(null, 0);
        data.writeTypedObject(null, 0);
        return data;
    }

    private Parcel getNParcel(Context context, Intent intent) {
        Parcel data = Parcel.obtain();
        //写入AMS Binder服务描述信息即android.app.IActivityManager
        data.writeInterfaceToken("android.app.IActivityManager");
        //写入IApplicationThread 匿名Binder服务实体(这个在attachApplication时写入过)
        data.writeStrongBinder(null);
        data.writeString(context.getPackageName());
        intent.writeToParcel(data, 0);
        data.writeString(null);
        data.writeStrongBinder(null);
        data.writeString(null);
        data.writeInt(0);
        data.writeInt(0);
        data.writeInt(0);
        data.writeInt(0);
        return data;
    }

    private void fullScreen(Context ctx, Intent intent) {
        NotificationUtils.getInstance().sendNotificationFullScreen(ctx, intent, "", "");
    }
}
