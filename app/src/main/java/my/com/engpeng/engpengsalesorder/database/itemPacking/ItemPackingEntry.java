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
    @ColumnInfo(name = "item_master_id")
    private long itemMasterId;
    @ColumnInfo(name = "sku_code")
    private String skuCode;
    @ColumnInfo(name = "sku_name")
    private String skuName;
    @ColumnInfo(name = "price_by_weight")
    private int priceByWeight;
    @ColumnInfo(name = "factor")
    private double factor;
    @ColumnInfo(name = "tax_item_type_id")
    private long taxItemTypeId;
    @ColumnInfo(name = "is_delete")
    private int isDelete;

    public ItemPackingEntry(@NonNull long id, long itemMasterId, String skuCode, String skuName, int priceByWeight, double factor, long taxItemTypeId, int isDelete) {
        this.id = id;
        this.itemMasterId = itemMasterId;
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.priceByWeight = priceByWeight;
        this.factor = factor;
        this.taxItemTypeId = taxItemTypeId;
        this.isDelete = isDelete;
    }

    @Ignore
    public ItemPackingEntry(JSONObject jsonObject) {
        try {
            setId(jsonObject.getLong("id"));
            setItemMasterId(jsonObject.isNull("im_i") ? 0 : jsonObject.getLong("im_i"));
            setSkuCode(jsonObject.isNull("sc") ? null : jsonObject.getString("sc"));
            setSkuName(jsonObject.isNull("sn") ? null : jsonObject.getString("sn"));
            setPriceByWeight(jsonObject.isNull("pbw") ? 0 : jsonObject.getInt("pbw"));
            setFactor(jsonObject.isNull("f") ? 0 : jsonObject.getDouble("f"));
            setTaxItemTypeId(jsonObject.isNull("tit_i") ? 0 : jsonObject.getLong("tit_i"));
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

    public long getItemMasterId() {
        return itemMasterId;
    }

    public void setItemMasterId(long itemMasterId) {
        this.itemMasterId = itemMasterId;
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

    public long getTaxItemTypeId() {
        return taxItemTypeId;
    }

    public void setTaxItemTypeId(long taxItemTypeId) {
        this.taxItemTypeId = taxItemTypeId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
