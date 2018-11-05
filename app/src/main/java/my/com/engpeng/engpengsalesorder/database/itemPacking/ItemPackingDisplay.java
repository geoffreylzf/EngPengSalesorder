package my.com.engpeng.engpengsalesorder.database.itemPacking;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

public class ItemPackingDisplay extends ItemPackingEntry {

    private long standardPriceSettingId;
    private double standardPrice;
    private long customerPriceSettingId;
    private double customerPrice;

    private long taxCodeId;
    private String taxCode;
    private double taxRate;

    public ItemPackingDisplay(@NonNull long id, long itemMasterId, String skuCode, String skuName, int priceByWeight, double factor, long taxItemTypeId, int isDelete, long standardPriceSettingId, double standardPrice, long customerPriceSettingId, double customerPrice, long taxCodeId, String taxCode, double taxRate) {
        super(id, itemMasterId, skuCode, skuName, priceByWeight, factor, taxItemTypeId, isDelete);
        this.standardPriceSettingId = standardPriceSettingId;
        this.standardPrice = standardPrice;
        this.customerPriceSettingId = customerPriceSettingId;
        this.customerPrice = customerPrice;
        this.taxCodeId = taxCodeId;
        this.taxCode = taxCode;
        this.taxRate = taxRate;
    }

    public double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public long getStandardPriceSettingId() {
        return standardPriceSettingId;
    }

    public void setStandardPriceSettingId(long standardPriceSettingId) {
        this.standardPriceSettingId = standardPriceSettingId;
    }

    public long getCustomerPriceSettingId() {
        return customerPriceSettingId;
    }

    public void setCustomerPriceSettingId(long customerPriceSettingId) {
        this.customerPriceSettingId = customerPriceSettingId;
    }

    public double getCustomerPrice() {
        return customerPrice;
    }

    public void setCustomerPrice(double customerPrice) {
        this.customerPrice = customerPrice;
    }

    public long getTaxCodeId() {
        return taxCodeId;
    }

    public void setTaxCodeId(long taxCodeId) {
        this.taxCodeId = taxCodeId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
}
