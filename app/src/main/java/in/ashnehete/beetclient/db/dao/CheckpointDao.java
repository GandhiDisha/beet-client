package in.ashnehete.beetclient.db.dao;

import java.util.List;

import androidx.room.Insert;
import androidx.room.Query;
import in.ashnehete.beetclient.db.entities.Checkpoint;

public interface CheckpointDao {
    @Query("SELECT * FROM checkpoint")
    List<Checkpoint> getAll();

    @Query("SELECT * FROM checkpoint WHERE route_id = :routeId AND checkpoint=:checkpoint LIMIT 1")
    Checkpoint getCheckpoint(String routeId, int checkpoint);

    @Insert
    void insertAll(Checkpoint... checkpoints);
}
