package in.ashnehete.beetclient.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import in.ashnehete.beetclient.db.dao.CheckpointDao;
import in.ashnehete.beetclient.db.dao.HistoryDao;
import in.ashnehete.beetclient.db.entities.Checkpoint;

@Database(entities = {Checkpoint.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CheckpointDao checkpointDao();

    public abstract HistoryDao historyDao();
}