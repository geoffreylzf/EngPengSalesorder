package my.com.engpeng.engpengsalesorder.database.priceSetting;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
