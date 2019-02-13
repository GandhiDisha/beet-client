package in.ashnehete.beetclient.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import in.ashnehete.beetclient.db.dao.CheckpointDao;
import in.ashnehete.beetclient.db.dao.HistoryDao;
import in.ashnehete.beetclient.db.entities.Checkpoint;
import in.ashnehete.beetclient.db.entities.History;

@Database(entities = {Checkpoint.class, History.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract CheckpointDao checkpointDao();

    public abstract HistoryDao historyDao();
}