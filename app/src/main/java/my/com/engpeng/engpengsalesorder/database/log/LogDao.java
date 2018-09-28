package my.com.engpeng.engpengsalesorder.database.log;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LogDao {

    @Query("SELECT * FROM log WHERE task = :task ORDER BY id DESC")
    LiveData<List<LogEntry>> loadLiveLogByTask(String task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(LogEntry LogEntry);

    @Query("DELETE FROM log")
    void deleteAll();
}
