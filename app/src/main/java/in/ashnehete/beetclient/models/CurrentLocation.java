package in.ashnehete.beetclient.models;

public class CurrentLocation {

    private String route_id;
    private int checkpoint;

    public CurrentLocation() {
    }

    public String getRouteId() {
        return route_id;
    }

    public void setRouteId(String route_id) {
        this.route_id = route_id;
    }

    public int getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
    }

    @Override
    public String toString() {
        return "Route ID: " + route_id + "\n" +
                "Checkpoint: " + checkpoint + "\n";
    }
}
