package my.com.engpeng.engpengsalesorder.database.salesorder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import java.util.List;

import my.com.engpeng.engpengsalesorder.model.SalesInfo;

@Dao
public interface SalesorderDao {
    @Query("SELECT * FROM salesorder")
    List<SalesorderEntry> loadAllSalesorders();

    @Query("SELECT * FROM salesorder WHERE id = :id")
    LiveData<SalesorderEntry> loadLiveSalesorder(Long id);

    @Query("SELECT * FROM salesorder WHERE id = :id")
    SalesorderEntry loadSalesorder(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSalesorder(SalesorderEntry salesorderEntry);

    @RawQuery(observedEntities = SalesorderEntry.class)
    LiveData<List<SoGroupByDateDisplay>> loadAllSoGroupViaQuery(SupportSQLiteQuery q);

    @RawQuery(observedEntities = SalesorderEntry.class)
    LiveData<List<SoDisplay>> loadAllSoDisplayViaQuery(SupportSQLiteQuery q);

    @Query("SELECT MAX(running_no)" +
            " FROM salesorder" +
            " WHERE running_no LIKE :prefix")
    String getLastRunningNoByPrefix(String prefix);

    @Query("DELETE FROM salesorder WHERE id = :id")
    void deleteById(Long id);

    @Query("SELECT * FROM salesorder WHERE status = :status AND is_upload = :upload")
    List<SalesorderEntry> loadAllSalesorderByStatusUpload(String status, int upload);

    @Query("SELECT COUNT(*) FROM salesorder WHERE status = :status AND is_upload = :upload")
    int getCountByStatusUpload(String status, int upload);

    @Query("SELECT COUNT(*) FROM salesorder WHERE status = :status AND is_upload = :upload")
    LiveData<Integer> getLiveCountByStatusUpload(String status, int upload);

    @Query("SELECT SUM(CASE WHEN status = 'DRAFT' THEN 1 ELSE 0 END) AS draft," +
            " SUM(CASE WHEN status = 'CONFIRM' AND is_upload = 1 THEN 1 ELSE 0 END) AS confirm," +
            " SUM(CASE WHEN status = 'CONFIRM' AND is_upload = 0 THEN 1 ELSE 0 END) AS unupload" +
            " FROM salesorder" +
            " WHERE strftime('%Y-%m', document_date) = :month" +
            " AND company_id = :companyId")
    LiveData<SalesInfo> loadLiveInfoCountByMonth(String month, long companyId);

    @Query("DELETE FROM salesorder")
    void deleteAll();
}
