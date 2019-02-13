package in.ashnehete.beetclient.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BeetService {
    @GET("routes")
    Call<List<Route>> getAllRoutes();

    @GET("routes/{routeId}")
    Call<Route> getRoute(@Path("routeId") String routeId);
}
