package in.ashnehete.beetclient;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static in.ashnehete.beetclient.AppConstants.CLIENT_ID;
import static in.ashnehete.beetclient.AppConstants.MQTT_URL;

public class MqttHelper {

    private static final String TAG = "MqttHelper";
    private static MqttHelper instance;
    private MqttAndroidClient mqttAndroidClient;
    private Context context;

    public MqttHelper(Context context) {
        this.context = context;
        mqttAndroidClient = new MqttAndroidClient(context, MQTT_URL, CLIENT_ID);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d(TAG, "Reconnected: " + serverURI);
                } else {
                    Log.d(TAG, "Connected: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection Lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "Incoming message (" + topic + "): " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Log.d(TAG, "Delivery: " + token.getMessage());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static MqttHelper getInstance(Context context) {
        if (instance == null) {

            instance = new MqttHelper(context);
        }
        return instance;
    }

    public MqttAndroidClient getMqttAndroidClient() {
        return mqttAndroidClient;
    }

    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setWill("topic", "I am going offline".getBytes(), 1, true);
        return mqttConnectOptions;
    }
}
