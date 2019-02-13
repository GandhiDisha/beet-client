package in.ashnehete.beetclient.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import in.ashnehete.beetclient.db.entities.History;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history")
    List<History> getAll();

    @Insert
    void insertAll(History... histories);
}
