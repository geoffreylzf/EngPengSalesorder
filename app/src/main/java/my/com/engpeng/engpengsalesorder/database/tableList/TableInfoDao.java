package my.com.engpeng.engpengsalesorder.database.tableList;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.util.TableInfo;

import java.util.List;

@Dao
public interface TableInfoDao {
    @Query("SELECT * FROM table_info")
    LiveData<List<TableInfoEntry>> loadLiveAllTableInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTableInfo(TableInfoEntry tableInfoEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTableInfo(TableInfoEntry tableInfoEntry);

    @Delete
    void deleteTableInfo(TableInfoEntry tableInfoEntry);

    @Query("SELECT * FROM table_info WHERE type = :type")
    LiveData<TableInfoEntry> loadLiveTableInfoByType(String type);

    @Query("SELECT * FROM table_info WHERE type = :type")
    TableInfoEntry loadTableInfoByType(String type);

    @Query("DELETE FROM table_info")
    void deleteAll();
}
