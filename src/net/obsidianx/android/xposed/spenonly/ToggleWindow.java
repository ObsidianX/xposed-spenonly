package net.obsidianx.android.xposed.spenonly;

import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

import java.io.File;
import java.io.IOException;

public class ToggleWindow extends StandOutWindow implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = ToggleWindow.class.getSimpleName();

    private File mToggleFile;

    @Override
    public void onCreate() {
        super.onCreate();

        mToggleFile = new File(Environment.getExternalStorageDirectory(), InputFilter.TOGGLE_FILE);
    }

    @Override
    public String getAppName() {
        return getString(R.string.app_name);
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        final View view = LayoutInflater.from(this).inflate(R.layout.window_toggle, frame, true);
        final Switch toggleSwitch = (Switch)view.findViewById(R.id.toggle_switch);
        toggleSwitch.setOnCheckedChangeListener(this);
        toggleSwitch.setChecked(mToggleFile.exists());
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int width = (int)(metrics.density * 150);
        final int height = (int)(metrics.density * 50);
        return new StandOutLayoutParams(id, width, height, StandOutLayoutParams.RIGHT, StandOutLayoutParams.TOP);
    }

    @Override
    public int getFlags(int id) {
        return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return getString(R.string.notification_close);
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return StandOutWindow.getCloseIntent(this, ToggleWindow.class, id);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        try {
            if(isChecked) {
                mToggleFile.createNewFile();
            } else {
                mToggleFile.delete();
            }
        } catch(IOException e) {
            Log.e(TAG, "Could not create toggle file");
        }
    }
}
