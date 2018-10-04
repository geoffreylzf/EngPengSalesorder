package my.com.engpeng.engpengsalesorder.database.salesorderDetail;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SalesorderDetailDao {
    @Query("SELECT * FROM salesorder_detail WHERE salesorder_id = :salesorderId")
    List<SalesorderDetailEntry> loadAllSalesorderDetailsBySalesorderId(Long salesorderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSalesorderDetail(SalesorderDetailEntry salesorderDetailEntry);

    @Query("DELETE FROM salesorder_detail WHERE salesorder_id = :salesorderId")
    void deleteAllBySalesorderId(Long salesorderId);

    @Query("DELETE FROM salesorder_detail")
    void deleteAll();
}
