package in.ashnehete.beetclient;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class MqttHelper {
    public MqttHelper() {

    }

    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setWill("topic", "I am going offline".getBytes(), 1, true);
        return mqttConnectOptions;
    }
}
