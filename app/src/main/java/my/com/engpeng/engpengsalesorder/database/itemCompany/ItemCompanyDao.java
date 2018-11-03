package my.com.engpeng.engpengsalesorder.database.itemCompany;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemCompanyDao {
    @Query("SELECT * FROM item_company ORDER BY id")
    LiveData<List<ItemCompanyEntry>> loadItemCompanies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemCompany(ItemCompanyEntry itemCompanyEntry);

    @Query("DELETE FROM item_company")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM item_company")
    LiveData<Integer> getLiveCount();
}
