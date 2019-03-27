package in.ashnehete.beetclient;

public class AppConstants {
    public static final String IP = "192.168.1.6";
    public static final String SSE_URL = "http://" + IP + ":6917/test";
    public static final String SERVER_URL = "http://" + IP + ":3000/";
    public static final String MQTT_URL = "tcp://" + IP + ":1883";
    public static final String CLIENT_ID = "TestClient";
    public static final float GEOFENCE_RADIUS_IN_METERS = 50;
    public static final long GEOFENCE_EXPIRATION_IN_MILLIS = 12 * 60 * 60 * 1000; // 12 hours
    public static final String TOPIC = "test";
}
