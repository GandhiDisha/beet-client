package in.ashnehete.beetclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.star_zero.sse.EventHandler;
import com.star_zero.sse.MessageEvent;

import in.ashnehete.beetclient.models.CurrentLocation;

public class LocationEventHandler implements EventHandler {
    private Context mContext;
    public static final String TAG = "LocationEventHandler";

    public LocationEventHandler(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onOpen() {
        Log.d(TAG, "Open");
    }

    @Override
    public void onMessage(@NonNull MessageEvent event) {
        CurrentLocation currentLocation = new Gson().fromJson(event.getData(), CurrentLocation.class);
        Log.d(TAG, currentLocation.toString());
    }

    @Override
    public void onError(@Nullable Exception e) {
        Log.e(TAG, e.toString());
    }
}
