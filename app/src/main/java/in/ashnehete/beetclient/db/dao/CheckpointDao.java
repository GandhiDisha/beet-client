package in.ashnehete.beetclient.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import in.ashnehete.beetclient.db.entities.Checkpoint;

@Dao
public interface CheckpointDao {
    @Query("SELECT * FROM checkpoint")
    List<Checkpoint> getAll();

    @Query("SELECT * FROM checkpoint WHERE route_id = :routeId AND checkpoint=:checkpoint LIMIT 1")
    LiveData<Checkpoint> getCheckpoint(String routeId, int checkpoint);

    @Query("SELECT COUNT(*) FROM checkpoint WHERE route_id = :routeId")
    int getNumberOfCheckpoints(String routeId);

    @Insert
    void insertAll(Checkpoint... checkpoints);
}
