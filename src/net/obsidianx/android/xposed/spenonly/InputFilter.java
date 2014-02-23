package net.obsidianx.android.xposed.spenonly;

import android.os.Environment;
import android.os.FileObserver;
import android.view.MotionEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class InputFilter implements IXposedHookLoadPackage {
    public static final String TOGGLE_FILE = "spenonly";

    private boolean mEnabled;
    private FileObserver mObserver;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final int mask = FileObserver.CREATE | FileObserver.DELETE;
        mObserver = new FileObserver(Environment.getExternalStorageDirectory().getAbsolutePath(), mask) {
            @Override
            public void onEvent(int event, String path) {
                if(path != null && path.endsWith(TOGGLE_FILE)) {
                    if(event == FileObserver.CREATE) {
                        mEnabled = true;
                    } else if(event == FileObserver.DELETE) {
                        mEnabled = false;
                    }
                }
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