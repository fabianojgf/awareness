package great.ufc.br.awarenessclass;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.snapshot.PlacesResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import great.ufc.br.awarenessclass.actions.HeadphoneFenceReceiver;
import great.ufc.br.awarenessclass.actions.WalkingFenceReceiver;
import great.ufc.br.awarenessclass.actions.WalkingHeadphoneFenceReceiver;

public class MainActivity extends AppCompatActivity {
    // Declare variables for pending intent and fence receiver.
    private String HEADPHONE_FENCE_RECEIVER_ACTION = "HEADPHONE_FENCE_RECEIVER_ACTION";
    private String WALKING_FENCE_RECEIVER_ACTION = "WALKING_FENCE_RECEIVER_ACTION";
    private String WALKING_HEADPHONE_FENCE_RECEIVER_ACTION = "WALKING_HEADPHONE_FENCE_RECEIVER_ACTION";

    private GoogleApiClient mGoogleApiClient;
    private HeadphoneFenceReceiver headphoneReceiver;
    private WalkingFenceReceiver walkingReceiver;
    private WalkingHeadphoneFenceReceiver walkingHeadphoneReceiver;

    Button btnHeadphoneSnapshot;
    Button btnLocationSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();


		Intent headphoneIntent = new Intent(HEADPHONE_FENCE_RECEIVER_ACTION);
		PendingIntent headphonePendingIntent = PendingIntent.getBroadcast(this, 0, headphoneIntent, 0);
		// Registering the receiver
		headphoneReceiver = new HeadphoneFenceReceiver();
		registerReceiver(headphoneReceiver, new IntentFilter(HEADPHONE_FENCE_RECEIVER_ACTION));
		// Create a fence for Headphone.
		AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
		// Register a fence for Headphone.
		registerFence(headphonePendingIntent,"headphoneFence", headphoneFence);


        Intent walkingIntent = new Intent(WALKING_FENCE_RECEIVER_ACTION);
        PendingIntent walkingPendingIntent = PendingIntent.getBroadcast(this, 2, walkingIntent, 0);
        // Registering the receiver
        walkingReceiver = new WalkingFenceReceiver();
        registerReceiver(walkingReceiver, new IntentFilter(WALKING_FENCE_RECEIVER_ACTION));
        // Create a fence for User Walking.
        AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
        // Register a fence for Headphone.
        registerFence(walkingPendingIntent,"walkingFence", walkingFence);


		Intent walkingHeadPhoneIntent = new Intent(WALKING_HEADPHONE_FENCE_RECEIVER_ACTION);
		PendingIntent walkingHeadPhonePendingIntent = PendingIntent.getBroadcast(this, 2, walkingHeadPhoneIntent, 0);
		// Registering the receiver
		walkingHeadphoneReceiver = new WalkingHeadphoneFenceReceiver();
		registerReceiver(walkingHeadphoneReceiver, new IntentFilter(WALKING_HEADPHONE_FENCE_RECEIVER_ACTION));
		// Create a fence for User Walking with Headphone.
		AwarenessFence walkingHeadPhoneFence = AwarenessFence.and(walkingFence, headphoneFence);
		// Register a fence for Headphone.
		registerFence(walkingHeadPhonePendingIntent,"walkingHeadPhoneFence", walkingHeadPhoneFence);

        btnHeadphoneSnapshot = findViewById(R.id.btnHeadphoneSnapshot);
        btnLocationSnapshot = findViewById(R.id.btnLocationSnapshot);

        btnHeadphoneSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHeadphoneSnapshot();
            }
        });

        btnLocationSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlacesSnapshot();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startHeadphoneSnapshot(){
        //pegar o cliente de snapshot da api
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(this);
        snapshotClient.getHeadphoneState().addOnSuccessListener(new OnSuccessListener<HeadphoneStateResponse>() {
            //promise da requisicao getWeather
            @Override
            public void onSuccess(HeadphoneStateResponse headphoneStateResponse) {
                //mostrar a previsao do tempo no celular
                int resp = headphoneStateResponse.getHeadphoneState().getState();
                String response = (resp == HeadphoneState.PLUGGED_IN) ? "Fone Plugado" : "Fone Desplugado";
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationSnapshot(){
        //pegar o cliente de snapshot da api
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(this);
        snapshotClient.getLocation().addOnSuccessListener(new OnSuccessListener<LocationResponse>() {
            //promise da requisicao getWeather
            @Override
            public void onSuccess(LocationResponse locationResponse) {
                //mostrar a previsao do tempo no celular
                String resp = locationResponse.getLocation().getLatitude()
                        + ","
                        + locationResponse.getLocation().getLongitude();
                Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startPlacesSnapshot(){
        //pegar o cliente de snapshot da api
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(this);
        snapshotClient.getPlaces().addOnSuccessListener(new OnSuccessListener<PlacesResponse>() {
            //promise da requisicao getWeather
            @Override
            public void onSuccess(PlacesResponse placesResponse) {
                //mostrar a previsao do tempo no celular
                List<PlaceLikelihood> places = placesResponse.getPlaceLikelihoods();
                String resp = "";

                for(int i = 0; i < 5; i++) {
                    PlaceLikelihood p = places.get(i);
                    resp += ("(" + p.getPlace().getName().toString() + " - " + p.getLikelihood() + ")" + " \n");
                }

                Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
            }
        });
        snapshotClient.getPlaces().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String resp = "Não foi possível obter informações de lugar.";
                Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startWeatherSnapshot(){
        //pegar o cliente de snapshot da api
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(this);
        snapshotClient.getWeather().addOnSuccessListener(new OnSuccessListener<WeatherResponse>() {
            //promise da requisicao getWeather
            @Override
            public void onSuccess(WeatherResponse weatherResponse) {
                //mostrar a previsao do tempo no celular
                String resp = weatherResponse.getWeather().toString();
                Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
            }
        });
        snapshotClient.getWeather().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void registerFence(final PendingIntent pendingIntent, final String fenceKey, final AwarenessFence fence) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(fenceKey, fence, pendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            Log.i(android.content.ContentValues.TAG, "Fence was successfully registered.");
                            queryFence(fenceKey);
                        } else {
                            Log.e(android.content.ContentValues.TAG, "Fence could not be registered: " + status);
                        }
                    }
                });
    }

    protected void unregisterFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(
                                new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(android.content.ContentValues.TAG, "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(android.content.ContentValues.TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

    protected void queryFence(final String fenceKey) {
        Awareness.FenceApi.queryFences(mGoogleApiClient,
                FenceQueryRequest.forFences(Arrays.asList(fenceKey)))
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            Log.e(android.content.ContentValues.TAG, "Could not query fence: " + fenceKey);
                            return;
                        }
                        FenceStateMap map = fenceQueryResult.getFenceStateMap();
                        for (String fenceKey : map.getFenceKeys()) {
                            FenceState fenceState = map.getFenceState(fenceKey);
                            Log.i(android.content.ContentValues.TAG, "Fence " + fenceKey + ": "
                                    + fenceState.getCurrentState()
                                    + ", was="
                                    + fenceState.getPreviousState()
                                    + ", lastUpdateTime="
                                    + new Date(fenceState.getLastFenceUpdateTimeMillis()));
                        }
                    }
                });
    }
}
