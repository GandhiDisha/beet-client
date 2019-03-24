package in.ashnehete.beetclient.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Checkpoint {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "route_id")
    public String routeId;

    @ColumnInfo(name = "route_name")
    public String routeName;

    @ColumnInfo(name = "checkpoint")
    public int checkpoint;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @NonNull
    @Override
    public String toString() {
        return "Id: " + id + "\n" +
                "RouteId: " + routeId + "\n" +
                "Checkpoint: " + checkpoint + "\n" +
                "Latitude: " + latitude + "\n" +
                "Longitude: " + longitude;
    }
}
