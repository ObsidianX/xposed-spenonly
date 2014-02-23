package net.obsidianx.android.xposed.spenonly;

import android.os.Environment;
import android.os.FileObserver;
import android.view.MotionEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.File;

public class InputFilter implements IXposedHookLoadPackage {
    private boolean mEnabled;
    private FileObserver mObserver;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final String packageName = InputFilter.class.getPackage().getName();
        final File file = new File(Environment.getDataDirectory(), "data/" + packageName + "/shared_prefs/" + Prefs.NAME + ".xml");
        if(!file.exists()) {
            file.mkdirs();
            file.createNewFile();
        }
        mObserver = new FileObserver(file.getAbsolutePath()) {
            @Override
            public void onEvent(int event, String path) {
                final XSharedPreferences prefs = new XSharedPreferences(packageName, Prefs.NAME);
                mEnabled = prefs.getBoolean(Prefs.PREF_ENABLED, false);
            }
        };
        mObserver.startWatching();

        final XC_MethodHook checkSource = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(mEnabled) {
                    final MotionEvent event = (MotionEvent)param.args[0];
                    if(event.getDevice().getName().equals("sec_touchscreen")) {
                        param.setResult(false);
                    }
                }
            }
        };

        XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "dispatchTouchEvent", MotionEvent.class, checkSource);
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "dispatchTouchEvent", MotionEvent.class, checkSource);
    }
}