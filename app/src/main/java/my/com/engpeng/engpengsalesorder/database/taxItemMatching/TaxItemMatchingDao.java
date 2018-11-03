package my.com.engpeng.engpengsalesorder.database.taxItemMatching;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TaxItemMatchingDao {
    @Query("SELECT * FROM tax_item_matching ORDER BY id")
    LiveData<List<TaxItemMatchingEntry>> loadTaxItemMatchings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaxItemMatching(TaxItemMatchingEntry taxItemMatchingEntry);

    @Query("DELETE FROM tax_item_matching")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM tax_item_matching")
    LiveData<Integer> getLiveCount();
}
