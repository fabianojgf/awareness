package great.ufc.br.awarenessclass.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

public class WalkingHeadphoneFenceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        if (TextUtils.equals(fenceState.getFenceKey(), "walkingHeadphoneFence")) {
            switch(fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(android.content.ContentValues.TAG, "Walking with Headphones plugged in.");
                    Toast.makeText(context, "Walking with Headphones plugged in.", Toast.LENGTH_SHORT).show();
                    light(context);
                    break;
                case FenceState.FALSE:
                    Log.i(android.content.ContentValues.TAG, "Walking without Headphones plugged in.");
                    Toast.makeText(context, "Walking without Headphones plugged in.", Toast.LENGTH_SHORT).show();
                    break;
                case FenceState.UNKNOWN:
                    Log.i(android.content.ContentValues.TAG, "The walking/headphone fence is in an unknown state.");
                    Toast.makeText(context, "The walking/headphone fence is in an unknown state.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void light(Context context) {

    }
}
