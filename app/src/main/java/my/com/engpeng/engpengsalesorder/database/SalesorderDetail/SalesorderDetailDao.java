package my.com.engpeng.engpengsalesorder.database.SalesorderDetail;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SalesorderDetailDao {
    @Query("SELECT * FROM salesorder_detail")
    LiveData<List<SalesorderDetailEntry>> loadLiveAllSalesorderDetails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSalesorderDetail(SalesorderDetailEntry salesorderDetailEntry);
}
