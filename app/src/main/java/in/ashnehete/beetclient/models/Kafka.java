package in.ashnehete.beetclient.models;

public class Kafka {
    String topic;
    int partition;
    int offset;
    long timestamp;
    String key;

    public Kafka() {
    }

    @Override
    public String toString() {
        return "Topic: " + topic + "\n" +
                "Partition: " + partition + "\n" +
                "Offset: " + offset + "\n" +
                "Timestamp: " + timestamp + "\n" +
                "Key: " + key;
    }
}
