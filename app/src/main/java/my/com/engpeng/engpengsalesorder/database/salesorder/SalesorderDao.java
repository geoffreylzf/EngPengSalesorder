package my.com.engpeng.engpengsalesorder.database.salesorder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SalesorderDao {
    @Query("SELECT * FROM salesorder")
    LiveData<List<SalesorderEntry>> loadLiveAllSalesorders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSalesorder(SalesorderEntry salesorderEntry);
}
