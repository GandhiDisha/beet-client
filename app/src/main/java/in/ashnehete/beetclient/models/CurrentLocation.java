package in.ashnehete.beetclient.models;

public class CurrentLocation {
    String route_id;
    int checkpoint;
    Kafka _kafka;

    public CurrentLocation() {
    }

    @Override
    public String toString() {
        return "Route ID: " + route_id + "\n" +
                "Checkpoint: " + checkpoint + "\n" +
                "Kafka: " + _kafka.toString();
    }
}
