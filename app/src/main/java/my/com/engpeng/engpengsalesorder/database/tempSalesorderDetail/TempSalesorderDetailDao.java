package my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TempSalesorderDetailDao {
    @Query("SELECT * FROM temp_salesorder_detail ORDER BY id")
    LiveData<List<TempSalesorderDetailEntry>> loadAllTempSalesorderDetails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTempSalesorderDetail(TempSalesorderDetailEntry tempSalesorderDetailEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTempSalesorderDetail(List<TempSalesorderDetailEntry> tempSalesorderDetailEntryList);

    @Query("DELETE FROM temp_salesorder_detail WHERE item_packing_id = :itemPackingId")
    void deleteByItemPackingId(long itemPackingId);

    @Query("DELETE FROM temp_salesorder_detail")
    void deleteAll();

    @Query("SELECT temp_salesorder_detail.*," +
            " IFNULL(item_packing.sku_code, 'ID: '||temp_salesorder_detail.item_packing_id) AS skuCode," +
            " IFNULL(item_packing.sku_name, 'ID: '||temp_salesorder_detail.item_packing_id) AS skuName," +
            " item_packing.price_by_weight AS priceByWeight" +
            " FROM temp_salesorder_detail" +
            " LEFT JOIN item_packing ON item_packing.id = temp_salesorder_detail.item_packing_id" +
            " ORDER BY id DESC")
    LiveData<List<TempSalesorderDetailDisplay>> loadAllTempSalesorderDetailsWithItemPacking();

    @Query("SELECT SUM(total_price) FROM temp_salesorder_detail")
    LiveData<Double> getLiveSumTotalPrice();
}
