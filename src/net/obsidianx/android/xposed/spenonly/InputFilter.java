package net.obsidianx.android.xposed.spenonly;

import android.view.MotionEvent;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class InputFilter implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static XSharedPreferences sPrefs;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        sPrefs = new XSharedPreferences(InputFilter.class.getPackage().getName());
        sPrefs.makeWorldReadable();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final XC_MethodHook checkSource = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (sPrefs.getBoolean(Prefs.PREF_ENABLED, true)) {
                    final MotionEvent event = (MotionEvent) param.args[0];
                    if (event.getDevice().getName().equals("sec_touchscreen")) {
                        param.setResult(false);
                    }
                }
            }
        };

        XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "dispatchTouchEvent", MotionEvent.class, checkSource);
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "dispatchTouchEvent", MotionEvent.class, checkSource);
    }
}