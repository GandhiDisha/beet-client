package in.ashnehete.beetclient.ui;

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

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import in.ashnehete.beetclient.R;
import in.ashnehete.beetclient.RouteRepository;
import in.ashnehete.beetclient.models.MqttCheckpoint;
import in.ashnehete.beetclient.server.Route;

import static in.ashnehete.beetclient.AppConstants.CLIENT_ID;
import static in.ashnehete.beetclient.AppConstants.MQTT_URL;

public class TrackerActivity extends AppCompatActivity {

    public static final String TAG = "TrackerActivity";

    Spinner spnRoutes;
    Button btnStart;
    TextView tvConsole;

    RouteRepository routeRepository;
    MqttAndroidClient mqttAndroidClient;
    Gson gson;

    Route selectedRoute;
    String topic = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        spnRoutes = findViewById(R.id.spnRoutes);
        btnStart = findViewById(R.id.btnStart);
        tvConsole = findViewById(R.id.tvConsole);

        routeRepository = new RouteRepository(this);
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), MQTT_URL, CLIENT_ID);
        initMqtt();

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
                connectMqtt();
            }
        });
    }

    private void initMqtt() {
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    console("Reconnected: " + serverURI);
                } else {
                    console("Connected: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                console("Connection Lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                console("Incoming message (" + topic + "): " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    console("Delivery: " + token.getMessage());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connectMqtt() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        try {
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
//                    subscribeToTopic();

                    final List<Route.Checkpoint> checkpoints = selectedRoute.getCheckpoints();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (Route.Checkpoint c : checkpoints) {
                                Log.d(TAG, c.toString());
                                MqttCheckpoint mqttCheckpoint = new MqttCheckpoint(selectedRoute.getId(), c.getId());
                                String msg = new Gson().toJson(mqttCheckpoint);
                                Log.d(TAG, "JSON: " + msg);
                                publishMessage(topic, msg);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    console("Failed to connect to: " + MQTT_URL);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    console("Subscribed (" + topic + ")");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    console("Failed to subscribe (" + topic + ")");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String topic, String msg) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(topic, message);
            console("Published (" + topic + ": " + msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void console(String msg) {
        tvConsole.append(msg + "\n");
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
