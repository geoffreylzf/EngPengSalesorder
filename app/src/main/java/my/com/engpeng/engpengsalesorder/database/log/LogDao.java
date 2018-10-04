package my.com.engpeng.engpengsalesorder.database.log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {

    @Query("SELECT * FROM log WHERE task = :task ORDER BY id DESC")
    LiveData<List<LogEntry>> loadLiveLogByTask(String task);

    @Query("SELECT * FROM (SELECT * FROM log ORDER BY id) GROUP BY task")
    LiveData<List<LogEntry>> loadLiveLastLogGroupByTask();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(LogEntry LogEntry);

    @Query("DELETE FROM log")
    void deleteAll();
}
