package my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail;

public class TempSalesorderDetailDisplay extends TempSalesorderDetailEntry {

    private String skuCode;
    private String skuName;
    private int priceByWeight;
    private String taxCode;

    public TempSalesorderDetailDisplay(long itemPackingId, double qty, double weight, double factor, double price, long priceSettingId, String priceMethod, long taxCodeId, double taxRate, double taxAmt, double totalPrice, String skuCode, String skuName, int priceByWeight, String taxCode) {
        super(itemPackingId, qty, weight, factor, price, priceSettingId, priceMethod, taxCodeId, taxRate, taxAmt, totalPrice);
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.priceByWeight = priceByWeight;
        this.taxCode = taxCode;
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

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }
}
