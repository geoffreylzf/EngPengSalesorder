package my.com.engpeng.engpengsalesorder.database.taxType;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TaxTypeDao {
    @Query("SELECT * FROM tax_type ORDER BY id")
    LiveData<List<TaxTypeEntry>> loadTaxTypes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaxType(TaxTypeEntry taxTypeEntry);

    @Query("DELETE FROM tax_type")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM tax_type")
    LiveData<Integer> getLiveCount();
}
