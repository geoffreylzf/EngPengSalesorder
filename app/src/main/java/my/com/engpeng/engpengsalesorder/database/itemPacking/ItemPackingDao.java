package my.com.engpeng.engpengsalesorder.database.itemPacking;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ItemPackingDao {
    @Query("SELECT * FROM " + ItemPackingEntry.TABLE_NAME + " ORDER BY id")
    LiveData<List<ItemPackingEntry>> loadAllItemPackings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemPacking(ItemPackingEntry itemPackingEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItemPacking(ItemPackingEntry itemPackingEntry);

    @Query("DELETE FROM " + ItemPackingEntry.TABLE_NAME)
    void deleteAll();

    @Query("SELECT COUNT(*) FROM " + ItemPackingEntry.TABLE_NAME)
    LiveData<Integer> getLiveCount();

    @Query("SELECT * " +
            "FROM " + ItemPackingEntry.TABLE_NAME + " " +
            "WHERE sku_code||sku_name LIKE :filter " +
            "ORDER BY id "+
            "LIMIT 200")
    LiveData<List<ItemPackingEntry>> loadLiveAllItemPackingsByFilter(String filter);
}
