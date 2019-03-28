package my.com.engpeng.engpengsalesorder.database.itemPacking;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

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

    @Query("SELECT * FROM " +
            " (SELECT " +
            " item_packing.*," +
            " tax_code.id AS taxCodeId," +
            " tax_code.tax_code AS taxCode," +
            " tax_code.tax_rate AS taxRate," +
            " A.price_setting_id AS standardPriceSettingId," +
            " A.selling_price AS standardPrice," +
            " B.price_setting_id AS customerPriceSettingId," +
            " B.selling_price AS customerPrice" +
            " FROM item_packing" +
            " INNER JOIN item_company" +
            "         ON item_packing.item_master_id = item_company.item_master_id" +
            "         AND item_company.is_delete = 0" +
            " INNER JOIN tax_item_matching " +
            "         ON tax_item_matching.tax_item_type_id = item_packing.tax_item_type_id" +
            "         AND tax_item_matching.is_delete = 0" +
            "         AND tax_item_matching.tax_zone_id = (SELECT tax_zone_id FROM person_customer_company_address WHERE id = :customerAddressId)" +
            " INNER JOIN tax_code" +
            "         ON tax_item_matching.tax_code_id = tax_code.id" +
            "         AND tax_code.is_delete = 0" +
            " INNER JOIN tax_type" +
            "         ON tax_item_matching.tax_type_id = tax_type.id" +
            "         AND tax_type.is_delete = 0" +
            " LEFT JOIN" +
            "        (SELECT" +
            "            id AS price_setting_id, item_packing_id, selling_price " +
            "        FROM" +
            "            (SELECT * FROM price_setting ORDER BY starting_date DESC, end_date DESC, create_date DESC) ps" +
            "        WHERE is_delete = 0 " +
            "            AND IFNULL(person_customer_company_id, 0) = 0 " +
            "            AND starting_date <= :deliveryDate " +
            "            AND doc_type_id = 44" +
            "            AND (end_date >= :deliveryDate " +
            "                OR IFNULL(end_date,'') = '') " +
            "            AND price_group_id = (SELECT price_group_id FROM person_customer_company WHERE id = :customerCompanyId) "+
            "        GROUP BY item_packing_id ) A " +
            "        ON item_packing.id = A.item_packing_id " +
            " LEFT JOIN" +
            "        (SELECT" +
            "            id AS price_setting_id, item_packing_id, selling_price " +
            "        FROM" +
            "            (SELECT * FROM price_setting ORDER BY starting_date DESC, end_date DESC, create_date DESC) ps" +
            "        WHERE is_delete = 0" +
            "            AND person_customer_company_id = :customerCompanyId" +
            "            AND starting_date <= :deliveryDate " +
            "            AND (end_date >= :deliveryDate " +
            "                OR IFNULL(end_date,'') = '') " +
            "        GROUP BY item_packing_id " +
            "        ORDER BY starting_date DESC, end_date DESC, create_date DESC) B " +
            "        ON item_packing.id = B.item_packing_id " +
            " WHERE item_packing.is_delete = 0" +
            " AND DATE('now') >= tax_type.effective_date_from" +
            " AND (tax_type.effective_date_to IS NULL OR DATE('now') <= tax_type.effective_date_to)" +
            " AND sku_code||sku_name LIKE :filter" +
            " AND item_packing.id NOT IN (SELECT item_packing_id FROM temp_salesorder_detail)" +
            " GROUP BY item_packing.id" +
            " ORDER BY item_packing.sku_name) C" +
            " WHERE IFNULL(standardPrice, 0) > 0 " +
            " OR IFNULL(customerPrice, 0) > 0" +
            " LIMIT 100")
    LiveData<List<ItemPackingDisplay>> loadLiveAllItemPackingsByFilter(String filter, Long customerCompanyId, Long customerAddressId, String deliveryDate);

    @Query("SELECT * FROM item_packing WHERE id = :id")
    ItemPackingEntry loadItemPackingById(long id);
}
