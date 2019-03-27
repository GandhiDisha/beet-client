package in.ashnehete.beetclient.models;

public class MqttCheckpoint {
    String route_id;
    int checkpoint;

    public MqttCheckpoint() {
    }

    public MqttCheckpoint(String route_id, int checkpoint) {
        this.route_id = route_id;
        this.checkpoint = checkpoint;
    }
}
