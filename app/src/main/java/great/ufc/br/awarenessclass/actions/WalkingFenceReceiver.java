package great.ufc.br.awarenessclass.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

public class WalkingFenceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        if (TextUtils.equals(fenceState.getFenceKey(), "walkingFence")) {
            switch(fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(android.content.ContentValues.TAG, "The user is walking.");
                    Toast.makeText(context, "The user is walking.", Toast.LENGTH_SHORT).show();
                    vibrate(context);
                    break;
                case FenceState.FALSE:
                    Log.i(android.content.ContentValues.TAG, "The user is NOT walking.");
                    Toast.makeText(context, "The user is NOT walking.", Toast.LENGTH_SHORT).show();
                    break;
                case FenceState.UNKNOWN:
                    Log.i(android.content.ContentValues.TAG, "The user is in an unknown state.");
                    Toast.makeText(context, "The user is in an unknown state.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(500);
        }
    }
}
