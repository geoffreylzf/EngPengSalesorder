package my.com.engpeng.engpengsalesorder.database.salesorder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import java.util.List;

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

    @Query("SELECT salesorder.*, " +
            " branch_name AS companyName," +
            " pcc.person_customer_company_name AS customerCompanyName," +
            " pcca.person_customer_address_name AS customerAddressName," +
            " COUNT(salesorder_detail.id) AS count" +
            " FROM salesorder" +
            " LEFT JOIN branch ON salesorder.company_id = branch.id" +
            " LEFT JOIN person_customer_company pcc ON salesorder.customer_company_id = pcc.id" +
            " LEFT JOIN person_customer_company_address pcca ON salesorder.customer_address_id = pcca.id" +
            " LEFT JOIN salesorder_detail ON salesorder.id = salesorder_detail.salesorder_id" +
            " WHERE salesorder.document_date = :documentDate" +
            " GROUP BY salesorder.id" +
            " ORDER BY salesorder.id DESC")
    LiveData<List<SoDisplay>> loadAllSoDisplayByDocumentDate(String documentDate);

    @RawQuery(observedEntities = SalesorderEntry.class)
    LiveData<List<SoDisplay>> loadAllSoDisplayViaQuery(SupportSQLiteQuery q);

    @Query("SELECT MAX(running_no)" +
            " FROM salesorder" +
            " WHERE running_no LIKE :prefix")
    LiveData<String> getLastRunningNoByPrefix(String prefix);

    @Query("DELETE FROM salesorder WHERE id = :id")
    void deleteById(Long id);

    @Query("SELECT * FROM salesorder WHERE status = :status AND is_upload = :upload")
    List<SalesorderEntry> loadAllSalesorderByStatusUpload(String status, int upload);

    @Query("SELECT COUNT(*) FROM salesorder WHERE status = :status AND is_upload = :upload")
    int getCountByStatusUpload(String status, int upload);
}
