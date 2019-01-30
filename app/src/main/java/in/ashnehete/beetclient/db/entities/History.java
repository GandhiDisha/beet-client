package in.ashnehete.beetclient.db.entities;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "route_id")
    public String route_id;

    @ColumnInfo(name = "checkpoint")
    public int checkpoint;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;
}
