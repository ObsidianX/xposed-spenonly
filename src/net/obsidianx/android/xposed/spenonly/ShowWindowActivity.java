package net.obsidianx.android.xposed.spenonly;

import android.app.Activity;
import android.os.Bundle;
import wei.mark.standout.StandOutWindow;

public class ShowWindowActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StandOutWindow.closeAll(this, ToggleWindow.class);
        StandOutWindow.show(this, ToggleWindow.class, StandOutWindow.DEFAULT_ID);

        finish();
    }
}
