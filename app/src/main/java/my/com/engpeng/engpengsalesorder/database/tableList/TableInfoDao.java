package my.com.engpeng.engpengsalesorder.database.tableList;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.util.TableInfo;

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
