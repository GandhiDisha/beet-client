package in.ashnehete.beetclient;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.star_zero.sse.EventHandler;
import com.star_zero.sse.MessageEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import in.ashnehete.beetclient.db.entities.Checkpoint;
import in.ashnehete.beetclient.models.CurrentLocation;
import in.ashnehete.beetclient.ui.MapsActivity;

public class LocationEventHandler implements EventHandler {
    private MapsActivity context;
    public static final String TAG = "LocationEventHandler";

    public LocationEventHandler(MapsActivity context) {
        this.context = context;
    }

    @Override
    public void onOpen() {
        Log.d(TAG, "Open");
    }

    @Override
    public void onMessage(@NonNull MessageEvent event) {
        byte[] decodedBytes = Base64.decode(event.getData(), Base64.DEFAULT);
        String json = new String(decodedBytes);
        CurrentLocation currentLocation = new Gson().fromJson(json, CurrentLocation.class);
        Log.d(TAG, currentLocation.toString());

        // Get Location from db/server
        RouteRepository routeRepository = new RouteRepository(context.getApplicationContext());
        final LiveData<Checkpoint> checkpoint = routeRepository.getCheckpoint(currentLocation);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.updateLocation(checkpoint);
            }
        });
    }

    @Override
    public void onError(@Nullable Exception e) {
        Log.e(TAG, e.toString());
    }
}
