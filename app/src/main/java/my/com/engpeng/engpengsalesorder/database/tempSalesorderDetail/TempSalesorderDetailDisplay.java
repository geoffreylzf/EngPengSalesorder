package my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail;

public class TempSalesorderDetailDisplay extends TempSalesorderDetailEntry {

    private String skuCode;
    private String skuName;

    public TempSalesorderDetailDisplay(long itemPackingId, double qty, double weight, double factor, double price, long priceSettingId, String priceMethod, double totalPrice,
                                       String skuCode, String skuName) {
        super(itemPackingId, qty, weight, factor, price, priceSettingId, priceMethod, totalPrice);
        this.skuCode = skuCode;
        this.skuName = skuName;
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
}
