package great.ufc.br.awarenessclass.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

public class HeadphoneFenceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFence")) {
            switch(fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(android.content.ContentValues.TAG, "Headphones are plugged in.");
                    Toast.makeText(context, "Headphones are plugged in.", Toast.LENGTH_SHORT).show();
                    break;
                case FenceState.FALSE:
                    Log.i(android.content.ContentValues.TAG, "Headphones are NOT plugged in.");
                    Toast.makeText(context, "Headphones are NOT plugged in.", Toast.LENGTH_SHORT).show();
                    break;
                case FenceState.UNKNOWN:
                    Log.i(android.content.ContentValues.TAG, "The headphone fence is in an unknown state.");
                    Toast.makeText(context, "The headphone fence is in an unknown state.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
