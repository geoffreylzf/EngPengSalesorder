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

    @Query("SELECT 'YEAR' AS dateType, strftime('%Y', document_date) AS documentDate, COUNT(*) AS count" +
            " FROM salesorder" +
            " GROUP BY strftime('%Y', document_date)" +
            " ORDER BY document_date DESC")
    LiveData<List<SoGroupByDateDisplay>> loadAllSoGroupByYearDisplay();

    @Query("SELECT 'MONTH' AS dateType, strftime('%Y-%m', document_date) AS documentDate, COUNT(*) AS count" +
            " FROM salesorder" +
            " WHERE strftime('%Y', document_date) = :year" +
            " GROUP BY strftime('%Y-%m', document_date)" +
            " ORDER BY document_date DESC")
    LiveData<List<SoGroupByDateDisplay>> loadAllSoGroupByYearMonthDisplayByYear(String year);

    @Query("SELECT 'DAY' AS dateType, document_date AS documentDate, COUNT(*) AS count" +
            " FROM salesorder" +
            " WHERE strftime('%Y-%m', document_date) = :yearMonth" +
            " GROUP BY document_date" +
            " ORDER BY document_date DESC")
    LiveData<List<SoGroupByDateDisplay>> loadAllSoGroupByDateDisplay(String yearMonth);

    @Query("SELECT salesorder.*, " +
            " branch_name AS companyName," +
            " pcc.person_customer_company_name AS customerCompanyName," +
            " pcca.person_customer_address_name AS customerAddressName" +
            " FROM salesorder" +
            " LEFT JOIN branch ON salesorder.company_id = branch.id" +
            " LEFT JOIN person_customer_company pcc ON salesorder.customer_company_id = pcc.id" +
            " LEFT JOIN person_customer_company_address pcca ON salesorder.customer_address_id = pcca.id" +
            " WHERE salesorder.document_date = :documentDate" +
            " ORDER BY ID DESC")
    LiveData<List<SoDisplay>> loadAllSoDisplayByDocumentDate(String documentDate);

    @Query("SELECT MAX(running_no)" +
            " FROM salesorder" +
            " WHERE running_no LIKE :prefix")
    LiveData<String> getLastRunningNoByPrefix(String prefix);
}
