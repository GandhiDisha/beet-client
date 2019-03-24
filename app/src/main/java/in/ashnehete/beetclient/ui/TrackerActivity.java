package in.ashnehete.beetclient.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import in.ashnehete.beetclient.R;
import in.ashnehete.beetclient.RouteRepository;
import in.ashnehete.beetclient.server.Route;

public class TrackerActivity extends AppCompatActivity {

    public static final String TAG = "TrackerActivity";
    Spinner spnRoutes;
    RouteRepository routeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        spnRoutes = (Spinner) findViewById(R.id.spnRoutes);

        routeRepository = new RouteRepository(this);

        final ArrayAdapter<String> routesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        routesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRoutes.setAdapter(routesAdapter);
        spnRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Selected: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Thread((new Runnable() {
            @Override
            public void run() {
                Map<String, Route> routeMap = routeRepository.getAllRoutes();
                List<String> routeNames = getRouteNames(routeMap);
                routesAdapter.addAll(routeNames);
            }
        })).start();
    }

    private List<String> getRouteNames(Map<String, Route> routeMap) {
        List<String> routeNames = new ArrayList<>(routeMap.size());
        for (Map.Entry<String, Route> entry : routeMap.entrySet()) {
            routeNames.add(entry.getValue().getName());
        }
        return routeNames;
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
