package my.com.engpeng.engpengsalesorder.database.salesorderDetail;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

@Entity(tableName = SalesorderDetailEntry.TABLE_NAME)
public class SalesorderDetailEntry {
    @Ignore
    public final static String TABLE_NAME = "salesorder_detail";

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "salesorder_id")
    private long salesorderId;
    @ColumnInfo(name = "item_packing_id")
    private long itemPackingId;
    @ColumnInfo(name = "price_setting_id")
    private long priceSettingId;
    @ColumnInfo(name = "price_method")
    private String priceMethod;
    @ColumnInfo(name = "qty")
    private double qty;
    @ColumnInfo(name = "weight")
    private double weight;
    @ColumnInfo(name = "factor")
    private double factor;
    @ColumnInfo(name = "price")
    private double price;
    @ColumnInfo(name = "tax_code_id")
    private long taxCodeId;
    @ColumnInfo(name = "tax_rate")
    private double taxRate;
    @ColumnInfo(name = "tax_amt")
    private double taxAmt;
    @ColumnInfo(name = "total_price")
    private double totalPrice;

    public SalesorderDetailEntry(long id, long salesorderId, long itemPackingId, long priceSettingId, String priceMethod, double qty, double weight, double factor, double price, long taxCodeId, double taxRate, double taxAmt, double totalPrice) {
        this.id = id;
        this.salesorderId = salesorderId;
        this.itemPackingId = itemPackingId;
        this.priceSettingId = priceSettingId;
        this.priceMethod = priceMethod;
        this.qty = qty;
        this.weight = weight;
        this.factor = factor;
        this.price = price;
        this.taxCodeId = taxCodeId;
        this.taxRate = taxRate;
        this.taxAmt = taxAmt;
        this.totalPrice = totalPrice;
    }

    @Ignore
    public SalesorderDetailEntry(long salesorderId, long itemPackingId, long priceSettingId, String priceMethod, double qty, double weight, double factor, double price, long taxCodeId, double taxRate, double taxAmt, double totalPrice) {
        this.salesorderId = salesorderId;
        this.itemPackingId = itemPackingId;
        this.priceSettingId = priceSettingId;
        this.priceMethod = priceMethod;
        this.qty = qty;
        this.weight = weight;
        this.factor = factor;
        this.price = price;
        this.taxCodeId = taxCodeId;
        this.taxRate = taxRate;
        this.taxAmt = taxAmt;
        this.totalPrice = totalPrice;
    }

    @Ignore
    public SalesorderDetailEntry(JSONObject jsonObject) {
        try {
            setItemPackingId(jsonObject.isNull("ip_i") ? 0 : jsonObject.getLong("ip_i"));
            setPriceSettingId(jsonObject.isNull("ps_i") ? 0 : jsonObject.getLong("ps_i"));
            setPriceMethod(jsonObject.isNull("pm") ? null : jsonObject.getString("pm"));
            setQty(jsonObject.isNull("q") ? 0 : jsonObject.getDouble("q"));
            setWeight(jsonObject.isNull("w") ? 0 : jsonObject.getDouble("w"));
            setFactor(jsonObject.isNull("f") ? 0 : jsonObject.getDouble("f"));
            setPrice(jsonObject.isNull("p") ? 0 : jsonObject.getDouble("p"));
            setTaxCodeId(jsonObject.isNull("tc_i") ? 0 : jsonObject.getLong("tc_i"));
            setTaxRate(jsonObject.isNull("tr") ? 0 : jsonObject.getDouble("tr"));
            setTaxAmt(jsonObject.isNull("ta") ? 0 : jsonObject.getDouble("ta"));
            setTotalPrice(jsonObject.isNull("tp") ? 0 : jsonObject.getDouble("tp"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSalesorderId() {
        return salesorderId;
    }

    public void setSalesorderId(long salesorderId) {
        this.salesorderId = salesorderId;
    }

    public long getItemPackingId() {
        return itemPackingId;
    }

    public void setItemPackingId(long itemPackingId) {
        this.itemPackingId = itemPackingId;
    }

    public long getPriceSettingId() {
        return priceSettingId;
    }

    public void setPriceSettingId(long priceSettingId) {
        this.priceSettingId = priceSettingId;
    }

    public String getPriceMethod() {
        return priceMethod;
    }

    public void setPriceMethod(String priceMethod) {
        this.priceMethod = priceMethod;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getTaxCodeId() {
        return taxCodeId;
    }

    public void setTaxCodeId(long taxCodeId) {
        this.taxCodeId = taxCodeId;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(double taxAmt) {
        this.taxAmt = taxAmt;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
