package my.com.engpeng.engpengsalesorder.database.salesorderDetail;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SalesorderDetailDao {
    @Query("SELECT * FROM salesorder_detail WHERE salesorder_id = :salesorderId")
    List<SalesorderDetailEntry> loadAllSalesorderDetailsBySalesorderId(Long salesorderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSalesorderDetail(SalesorderDetailEntry salesorderDetailEntry);

    @Query("DELETE FROM salesorder_detail WHERE salesorder_id = :salesorderId")
    void deleteAllBySalesorderId(Long salesorderId);
}
