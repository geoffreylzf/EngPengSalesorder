package my.com.engpeng.engpengsalesorder.database.priceSetting;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PriceSettingDao {
    @Query("SELECT * FROM price_setting ORDER BY id")
    LiveData<List<PriceSettingEntry>> loadAllPriceSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPriceSetting(PriceSettingEntry priceSettingEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePriceSetting(PriceSettingEntry priceSettingEntry);

    @Delete
    void deletePriceSetting(PriceSettingEntry priceSettingEntry);

    @Query("DELETE FROM price_setting")
    void deleteAll();

    @Query("SELECT * FROM price_setting WHERE id = :id")
    PriceSettingEntry loadPriceSettingById(Long id);

    @Query("SELECT COUNT(*) FROM price_setting")
    LiveData<Integer> getLiveCount();
}
