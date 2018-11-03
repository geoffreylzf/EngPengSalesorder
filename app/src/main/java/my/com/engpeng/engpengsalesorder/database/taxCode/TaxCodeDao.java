package my.com.engpeng.engpengsalesorder.database.taxCode;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TaxCodeDao {
    @Query("SELECT * FROM tax_code ORDER BY id")
    LiveData<List<TaxCodeEntry>> loadTaxCodes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaxCode(TaxCodeEntry taxCodeEntry);

    @Query("DELETE FROM tax_code")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM tax_code")
    LiveData<Integer> getLiveCount();
}
