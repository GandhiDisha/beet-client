package in.ashnehete.beetclient.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import in.ashnehete.beetclient.db.dao.CheckpointDao;
import in.ashnehete.beetclient.db.dao.HistoryDao;
import in.ashnehete.beetclient.db.entities.Checkpoint;
import in.ashnehete.beetclient.db.entities.History;

@Database(entities = {Checkpoint.class, History.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "app_database.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME).build();
    }

    public abstract CheckpointDao checkpointDao();

    public abstract HistoryDao historyDao();
}