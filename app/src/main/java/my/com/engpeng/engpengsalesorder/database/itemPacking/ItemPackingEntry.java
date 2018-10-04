package my.com.engpeng.engpengsalesorder.database.itemPacking;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import org.json.JSONObject;

@Entity(tableName = ItemPackingEntry.TABLE_NAME)
public class ItemPackingEntry {
    @Ignore
    public final static String TABLE_NAME = "item_packing";
    @Ignore
    public final static String TABLE_DISPLAY_NAME = "Item Packing";

    @PrimaryKey()
    @NonNull
    private long id;
    @ColumnInfo(name = "sku_code")
    private String skuCode;
    @ColumnInfo(name = "sku_name")
    private String skuName;
    @ColumnInfo(name = "price_by_weight")
    private int priceByWeight;
    @ColumnInfo(name = "factor")
    private double factor;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public ItemPackingEntry(long id, String skuCode, String skuName, int priceByWeight, double factor, int isDelete) {
        this.id = id;
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.priceByWeight = priceByWeight;
        this.factor = factor;
        this.isDelete = isDelete;
    }

    @Ignore
    public ItemPackingEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setSkuCode(jsonObject.isNull("sc") ? null : jsonObject.getString("sc"));
            setSkuName(jsonObject.isNull("sn") ? null : jsonObject.getString("sn"));
            setPriceByWeight(jsonObject.isNull("pbw") ? 0 : jsonObject.getInt("pbw"));
            setFactor(jsonObject.isNull("f") ? 0 : jsonObject.getDouble("f"));
            setIsDelete(jsonObject.isNull("i_d") ? 0 : jsonObject.getInt("i_d"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public int getPriceByWeight() {
        return priceByWeight;
    }

    public void setPriceByWeight(int priceByWeight) {
        this.priceByWeight = priceByWeight;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
