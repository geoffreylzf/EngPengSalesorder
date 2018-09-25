package my.com.engpeng.engpengsalesorder.database.customerCompanyAddress;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CustomerCompanyAddressDao {
    @Query("SELECT * FROM person_customer_company_address ORDER BY id")
    LiveData<List<CustomerCompanyAddressEntry>> loadAllCustmerCompanyAddresses();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomerCompanyAddress(CustomerCompanyAddressEntry customerCompanyAddressEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCustomerCompanyAddress(CustomerCompanyAddressEntry customerCompanyAddressEntry);

    @Query("DELETE FROM person_customer_company_address")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM person_customer_company_address")
    LiveData<Integer> getLiveCount();

    @Query("SELECT * " +
            " FROM person_customer_company_address" +
            " WHERE person_customer_company_id = :personCustomerCompanyId " +
            " AND person_customer_address_code||person_customer_address_name LIKE :filter " +
            " AND is_delete = 0" +
            " ORDER BY id ")
    LiveData<List<CustomerCompanyAddressEntry>> loadAllCustmerCompanyAddressesByPersonCustomerCompanyIdFilter(Long personCustomerCompanyId, String filter);

    @Query("SELECT * FROM " + CustomerCompanyAddressEntry.TABLE_NAME + " WHERE id = :id")
    LiveData<CustomerCompanyAddressEntry> loadLiveCustomerCompanyAddressById(Long id);
}
