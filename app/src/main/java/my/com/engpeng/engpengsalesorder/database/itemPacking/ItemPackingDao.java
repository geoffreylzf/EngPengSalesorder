package my.com.engpeng.engpengsalesorder.database.itemPacking;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;

@Dao
public interface ItemPackingDao {
    @Query("SELECT * FROM item_packing ORDER BY id")
    LiveData<List<ItemPackingEntry>> loadAllItemPackings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemPacking(ItemPackingEntry itemPackingEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItemPacking(ItemPackingEntry itemPackingEntry);

    @Query("DELETE FROM item_packing")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM item_packing")
    LiveData<Integer> getLiveCount();

    @Query("SELECT item_packing.*," +
            " A.price_setting_id AS standardPriceSettingId," +
            " A.selling_price AS standardPrice," +
            " B.price_setting_id AS customerPriceSettingId," +
            " B.selling_price AS customerPrice" +
            " FROM item_packing" +
            " LEFT JOIN" +
            "        (SELECT" +
            "            id AS price_setting_id, item_packing_id, selling_price " +
            "        FROM" +
            "            price_setting " +
            "        WHERE is_delete = 0 " +
            "            AND IFNULL(person_customer_company_id, 0) = 0 " +
            "            AND starting_date <= :deliveryDate " +
            "            AND doc_type_id = 44" +
            "            AND (end_date >= :deliveryDate " +
            "                OR IFNULL(end_date,'') = '') " +
            "            AND price_group_id = (SELECT price_group_id FROM person_customer_company WHERE id = :customerCompanyId) "+
            "        GROUP BY item_packing_id " +
            "        ORDER BY starting_date DESC, end_date DESC, create_date DESC) A " +
            "        ON id = A.item_packing_id " +
            " LEFT JOIN" +
            "        (SELECT" +
            "            id AS price_setting_id, item_packing_id, selling_price " +
            "        FROM" +
            "            price_setting " +
            "        WHERE is_delete = 0" +
            "            AND person_customer_company_id = :customerCompanyId" +
            "            AND starting_date <= :deliveryDate " +
            "            AND (end_date >= :deliveryDate " +
            "                OR IFNULL(end_date,'') = '') " +
            "        GROUP BY item_packing_id " +
            "        ORDER BY starting_date DESC, end_date DESC, create_date DESC) B " +
            "        ON id = B.item_packing_id " +
            " WHERE sku_code||sku_name LIKE :filter" +
            " AND is_delete = 0" +
            " AND id NOT IN (SELECT item_packing_id FROM temp_salesorder_detail)" +
            " ORDER BY item_packing.id" +
            " LIMIT 100")
    LiveData<List<ItemPackingDisplay>> loadLiveAllItemPackingsByFilter(String filter, Long customerCompanyId, String deliveryDate);

    @Query("SELECT * FROM item_packing WHERE id = :id")
    ItemPackingEntry loadItemPackingById(long id);
}
