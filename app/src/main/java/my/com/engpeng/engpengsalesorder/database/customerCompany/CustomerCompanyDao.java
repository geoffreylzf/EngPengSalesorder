package my.com.engpeng.engpengsalesorder.database.customerCompany;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface CustomerCompanyDao {
    @Query("SELECT * FROM " + CustomerCompanyEntry.TABLE_NAME + " ORDER BY id")
    LiveData<List<CustomerCompanyEntry>> loadLiveAllCustomerCompanies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomerCompany(CustomerCompanyEntry customerCompanyEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCustomerCompany(CustomerCompanyEntry customerCompanyEntry);

    @Query("DELETE FROM " + CustomerCompanyEntry.TABLE_NAME)
    void deleteAll();

    @Query("SELECT COUNT(*) FROM " + CustomerCompanyEntry.TABLE_NAME)
    LiveData<Integer> getLiveCount();

    @Query("SELECT * " +
            "FROM " + CustomerCompanyEntry.TABLE_NAME + " " +
            "WHERE person_customer_company_code||person_customer_company_name LIKE :filter " +
            "AND company_id = :companyId " +
            "ORDER BY id " +
            "LIMIT 100")
    LiveData<List<CustomerCompanyEntry>> loadLiveAllCustomerCompaniesByCompanyIdFilter(Long companyId, String filter);

    @Query("SELECT * FROM " + CustomerCompanyEntry.TABLE_NAME + " WHERE id = :id")
    LiveData<CustomerCompanyEntry> loadLiveCustomerCompanyById(Long id);
}
