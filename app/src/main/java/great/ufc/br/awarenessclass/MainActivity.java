package great.ufc.br.awarenessclass;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.snapshot.PlacesResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import great.ufc.br.awarenessclass.actions.ToastAction;
import great.ufc.br.awarenessclass.actions.VibrateAction;

public class MainActivity extends AppCompatActivity {
    // Declare variables for pending intent and fence receiver.
    private String FENCE_RECEIVER_ACTION = "FENCE_RECEIVER_ACTION";
    private PendingIntent myPendingIntent;

    Button btnHeadphoneSnapshot;
    Button btnLocationSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //Fence

        //Criar as AwarenessFences
        AwarenessFence headphone = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        //Filtros de Intent
        IntentFilter hp = new IntentFilter("headphone");
        //Registrar Receivers (actions) na pilha do Android
        registerReceiver(new ToastAction(), hp);
        //Registrar PendingIntents getBroadcast com os filtros criados
        PendingIntent pi = PendingIntent.getBroadcast(this,123,new Intent("headphone"),PendingIntent.FLAG_CANCEL_CURRENT);
        //Registro de Fences no Google Awareness API
        FenceClient fc = Awareness.getFenceClient(this);
        fc.updateFences(new FenceUpdateRequest.Builder().addFence("Headphone",headphone,pi).build());

        //Criar as AwarenessFences
        AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);

        //Filtros de Intent
        IntentFilter hp2 = new IntentFilter("walking");
        //Registrar Receivers (actions) na pilha do Android
        registerReceiver(new VibrateAction(), hp2);
        //Registrar PendingIntents getBroadcast com os filtros criados
        PendingIntent pi2 = PendingIntent.getBroadcast(this,123, new Intent("walking"),PendingIntent.FLAG_CANCEL_CURRENT);
        //Registro de Fences no Google Awareness API
        FenceClient fc2 = Awareness.getFenceClient(this);
        fc.updateFences(new FenceUpdateRequest.Builder().addFence("walking", walkingFence, pi2).build());
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
}
