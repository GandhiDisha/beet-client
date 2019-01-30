package in.ashnehete.beetclient.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Checkpoint {
    @PrimaryKey
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
}
