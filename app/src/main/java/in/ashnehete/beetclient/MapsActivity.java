package in.ashnehete.beetclient;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.star_zero.sse.EventSource;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import in.ashnehete.beetclient.db.entities.Checkpoint;

import static in.ashnehete.beetclient.AppConstants.SSE_URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = "MapsActivity";
    private EventSource eventSource;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Open Event Source
        eventSource = new EventSource(SSE_URL, new LocationEventHandler(this));
        eventSource.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventSource.close();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public void updateLocation(LiveData<Checkpoint> checkpoint) {
        checkpoint.observe(this, new Observer<Checkpoint>() {
            @Override
            public void onChanged(Checkpoint checkpoint) {
                if (checkpoint != null) {
                    Log.d(TAG, checkpoint.toString());
                    LatLng latLng = new LatLng(checkpoint.latitude, checkpoint.longitude);
                    map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } else {
                    Log.d(TAG, "onChange: checkpoint == null");
                }
            }
        });
    }
}
