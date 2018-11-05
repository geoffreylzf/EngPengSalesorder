package my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = TempSalesorderDetailEntry.TABLE_NAME)
public class TempSalesorderDetailEntry {
    @Ignore
    public final static String TABLE_NAME = "temp_salesorder_detail";

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "item_packing_id")
    private long itemPackingId;
    @ColumnInfo(name = "qty")
    private double qty;
    @ColumnInfo(name = "weight")
    private double weight;
    @ColumnInfo(name = "factor")
    private double factor;
    @ColumnInfo(name = "price")
    private double price;
    @ColumnInfo(name = "price_setting_id")
    private long priceSettingId;
    @ColumnInfo(name = "price_method")
    private String priceMethod;
    @ColumnInfo(name = "tax_code_id")
    private long taxCodeId;
    @ColumnInfo(name = "tax_rate")
    private double taxRate;
    @ColumnInfo(name = "tax_amt")
    private double taxAmt;
    @ColumnInfo(name = "total_price")
    private double totalPrice;

    public TempSalesorderDetailEntry(long itemPackingId, double qty, double weight, double factor, double price, long priceSettingId, String priceMethod, long taxCodeId, double taxRate, double taxAmt, double totalPrice) {
        this.itemPackingId = itemPackingId;
        this.qty = qty;
        this.weight = weight;
        this.factor = factor;
        this.price = price;
        this.priceSettingId = priceSettingId;
        this.priceMethod = priceMethod;
        this.taxCodeId = taxCodeId;
        this.taxRate = taxRate;
        this.taxAmt = taxAmt;
        this.totalPrice = totalPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemPackingId() {
        return itemPackingId;
    }

    public void setItemPackingId(long itemPackingId) {
        this.itemPackingId = itemPackingId;
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
