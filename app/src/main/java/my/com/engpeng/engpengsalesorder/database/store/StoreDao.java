package my.com.engpeng.engpengsalesorder.database.store;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StoreDao {
    @Query("SELECT * FROM store ORDER BY id")
    LiveData<List<StoreEntry>> getLiveStores();

    @Query("SELECT * FROM store")
    List<StoreEntry> getStores();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStore(StoreEntry storeEntry);

    @Query("DELETE FROM store")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM store")
    LiveData<Integer> getLiveCount();
}
