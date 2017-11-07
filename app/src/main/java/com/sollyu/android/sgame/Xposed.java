package com.sollyu.android.sgame;

import android.os.Build;

import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 作者：sollyu
 * 时间：2017/11/7
 * 说明：
 */
public class Xposed implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        /*
         * 仅拦截王者荣耀一个
         */
        if (loadPackageParam.packageName.equals("com.tencent.tmgp.sgame")) {
            HashMap<String, Object> buildRouter = new HashMap<>();

            {
                /*
                 * 修改手机厂商
                 */
                String hookValue = "Xiaomi";
                XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", hookValue);
                XposedHelpers.setStaticObjectField(Build.class, "PRODUCT", hookValue);
                XposedHelpers.setStaticObjectField(Build.class, "BRAND", hookValue);
                buildRouter.put("ro.product.manufacturer", hookValue);
                buildRouter.put("ro.product.brand", hookValue);
                buildRouter.put("ro.product.name", hookValue);
            }

            {
                /*
                 * 修改手机型号
                 */
                String hookValue = "MIX";
                XposedHelpers.setStaticObjectField(Build.class, "MODEL", hookValue);
                XposedHelpers.setStaticObjectField(Build.class, "DEVICE", hookValue);
                buildRouter.put("ro.product.device", hookValue);
                buildRouter.put("ro.product.model", hookValue);
            }

            /*
             * SystemProperties
             */
            XposedBridge.hookAllMethods(XposedHelpers.findClass("android.os.SystemProperties", loadPackageParam.classLoader), "native_get", new SystemPropertiesCallback(buildRouter));
        }
    }

    /**
     * SystemProperties Callback
     */
    private class SystemPropertiesCallback extends XC_MethodHook {
        final HashMap<String, Object> buildRouter;

        private SystemPropertiesCallback(HashMap<String, Object> buildRouter) {
            this.buildRouter = buildRouter;
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (buildRouter.containsKey(param.args[0].toString())) {
                param.setResult(buildRouter.get(param.args[0].toString()));
            }
        }
    }

}
