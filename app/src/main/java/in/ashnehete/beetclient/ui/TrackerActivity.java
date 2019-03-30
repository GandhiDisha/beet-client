package in.ashnehete.beetclient.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import in.ashnehete.beetclient.GeofenceBroadcastReceiver;
import in.ashnehete.beetclient.R;
import in.ashnehete.beetclient.RouteRepository;
import in.ashnehete.beetclient.server.Route;

import static in.ashnehete.beetclient.AppConstants.GEOFENCE_EXPIRATION_IN_MILLIS;
import static in.ashnehete.beetclient.AppConstants.GEOFENCE_RADIUS_IN_METERS;
import static in.ashnehete.beetclient.AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE;

public class TrackerActivity extends AppCompatActivity {

    public static final String TAG = "TrackerActivity";

    Spinner spnRoutes;
    Button btnStart;
    TextView tvConsole;

    RouteRepository routeRepository;
    GeofencingClient geofencingClient;
    List<Geofence> geofences;
    PendingIntent pendingIntent;

    Route selectedRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        spnRoutes = findViewById(R.id.spnRoutes);
        btnStart = findViewById(R.id.btnStart);
        tvConsole = findViewById(R.id.tvConsole);

        routeRepository = new RouteRepository(this);
        geofencingClient = LocationServices.getGeofencingClient(this);


        final ArrayAdapter<Route> routesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        routesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRoutes.setAdapter(routesAdapter);
        spnRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoute = (Route) parent.getItemAtPosition(position);
                Log.d(TAG, "Selected: " + selectedRoute.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Thread((new Runnable() {
            @Override
            public void run() {
                List<Route> routes = routeRepository.getAllRoutes();
                routesAdapter.addAll(routes);
            }
        })).start();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRoute == null) {
                    Toast.makeText(TrackerActivity.this, "Select a route", Toast.LENGTH_SHORT).show();
                    return;
                }
                spnRoutes.setEnabled(false);
                btnStart.setEnabled(false);
                addGeofences();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Geofences remove success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Geofences remove failure");
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void addGeofences() {
        geofences = getGeofencesList(selectedRoute);
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Geofences added");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Geofences failed to add");
                    }
                });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private List<Geofence> getGeofencesList(Route route) {
        List<Geofence> geofences = new ArrayList<>();
        List<Route.Checkpoint> checkpoints = route.getCheckpoints();
        for (Route.Checkpoint c : checkpoints) {
            String requestId = route.getId() + ":" + c.getId();
            geofences.add(new Geofence.Builder()
                    .setRequestId(requestId)
                    .setCircularRegion(
                            c.getLatitude(),
                            c.getLongitude(),
                            GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLIS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()
            );
            console(requestId + ":" + c.getLatitude() + "," + c.getLongitude());
        }
        return geofences;
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.i(TAG, "Requesting permission");
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".
        ActivityCompat.requestPermissions(TrackerActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void console(String msg) {
        tvConsole.append(msg + "\n");
        Log.d(TAG, msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                routeRepository.refreshRoutes();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
