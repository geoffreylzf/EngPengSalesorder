package my.com.engpeng.engpengsalesorder.database.customerCompanyAddress;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
    LiveData<List<CustomerCompanyAddressEntry>> loadLiveCustomerCompanyAddressesByPersonCustomerCompanyIdFilter(Long personCustomerCompanyId, String filter);

    @Query("SELECT * FROM person_customer_company_address WHERE id = :id")
    LiveData<CustomerCompanyAddressEntry> loadLiveCustomerCompanyAddressById(Long id);

    @Query("SELECT * " +
            " FROM person_customer_company_address" +
            " WHERE person_customer_company_id = :personCustomerCompanyId " +
            " AND is_delete = 0" +
            " ORDER BY id ")
    List<CustomerCompanyAddressEntry> loadCustomerCompanyAddressesByPersonCustomerCompanyId(Long personCustomerCompanyId);

    @Query("SELECT * FROM person_customer_company_address WHERE id = :id")
    CustomerCompanyAddressEntry loadCustomerCompanyAddressById(Long id);
}
