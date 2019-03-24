package in.ashnehete.beetclient;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import in.ashnehete.beetclient.db.AppDatabase;
import in.ashnehete.beetclient.db.dao.CheckpointDao;
import in.ashnehete.beetclient.db.entities.Checkpoint;
import in.ashnehete.beetclient.models.CurrentLocation;
import in.ashnehete.beetclient.server.Route;
import in.ashnehete.beetclient.server.RouteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static in.ashnehete.beetclient.AppConstants.SERVER_URL;

public class RouteRepository {
    private static int TIMEOUT = 30000;

    private final CheckpointDao checkpointDao;
    private final RouteService routeService;

    public RouteRepository(Context context) {
        this(
                AppDatabase.getInstance(context).checkpointDao(),
                new Retrofit.Builder()
                        .baseUrl(SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(RouteService.class)
        );
    }

    public RouteRepository(CheckpointDao checkpointDao, RouteService routeService) {
        this.checkpointDao = checkpointDao;
        this.routeService = routeService;
    }

    public LiveData<Checkpoint> getCheckpoint(CurrentLocation currentLocation) {
        refreshCheckpoint(currentLocation);
        return checkpointDao.getCheckpoint(currentLocation.getRouteId(), currentLocation.getCheckpoint());
    }

    public void refreshCheckpoint(CurrentLocation currentLocation) {
        int numberOfCheckpoints = checkpointDao.getNumberOfCheckpoints(currentLocation.getRouteId());
        // Fetch from server if route doesn't exist in local db
        if (numberOfCheckpoints == 0) {
            Call<Route> routeCall = routeService.getRoute(currentLocation.getRouteId());
            routeCall.enqueue(new Callback<Route>() {
                @Override
                public void onResponse(Call<Route> call, Response<Route> response) {
                    Route route = response.body();
                    insertCheckpoints(route);
                }

                @Override
                public void onFailure(Call<Route> call, Throwable throwable) {
                    // TODO: Failure
                }
            });
        }
    }

    public void insertCheckpoints(Route route) {
        List<Checkpoint> checkpoints = new ArrayList<>();
        for (Route.Checkpoint rc :
                route.getCheckpoints()) {
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.routeId = route.getId();
            checkpoint.routeName = route.getName();
            checkpoint.checkpoint = rc.getId();
            checkpoint.latitude = rc.getLatitude();
            checkpoint.longitude = rc.getLongitude();
            checkpoints.add(checkpoint);
        }
        Checkpoint[] temp = new Checkpoint[checkpoints.size()];
        temp = checkpoints.toArray(temp);
        final Checkpoint[] finalTemp = temp;
        new Thread() {
            public void run() {
                checkpointDao.insertAll(finalTemp);
            }
        }.start();
    }

    public List<Route> getAllRoutes() {
        List<Checkpoint> checkpoints = checkpointDao.getAll();
        Map<String, Route> routeMap = new HashMap<>();

        for (Checkpoint c : checkpoints) {
            if (routeMap.containsKey(c.routeId)) {
                routeMap.get(c.routeId).addCheckpoint(c.checkpoint, c.latitude, c.longitude);
            } else {
                Route route = new Route(c.routeId, c.routeName);
                route.addCheckpoint(c.checkpoint, c.latitude, c.longitude);
                routeMap.put(c.routeId, route);
            }
        }

        List<Route> routes = new ArrayList<>(routeMap.size());
        for (Map.Entry<String, Route> entry : routeMap.entrySet()) {
            routes.add(entry.getValue());
        }

        return routes;
    }

    public void refreshRoutes() {
        Call<List<Route>> routeCall = routeService.getAllRoutes();
        routeCall.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                List<Route> routes = response.body();
                for (Route r :
                        routes) {
                    insertCheckpoints(r);
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                // TODO: Failure
            }
        });
    }
}