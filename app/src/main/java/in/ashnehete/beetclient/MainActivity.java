package in.ashnehete.beetclient;

import android.os.Bundle;
import android.util.Log;

import com.star_zero.sse.EventSource;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import in.ashnehete.beetclient.server.BeetService;
import in.ashnehete.beetclient.server.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String SSE_URL = "http://192.168.1.5:6917/locations";
    public static final String SERVER_URL = "http://192.168.1.6:3000/";
    public static final String TAG = "MainActivity";
    private EventSource eventSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        eventSource = new EventSource(SSE_URL, new LocationEventHandler(this.getApplicationContext()));
//
//        eventSource.connect();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BeetService beetService = retrofit.create(BeetService.class);
        Call<List<Route>> routesCall = beetService.getAllRoutes();
        routesCall.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                List<Route> routes = response.body();
                for (Route route : routes) {
                    Log.d(TAG, "Routes:\n" + route.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
//        eventSource.close();
    }
}
