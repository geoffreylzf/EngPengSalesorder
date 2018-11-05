package my.com.engpeng.engpengsalesorder.database.salesorder;

public class SoDisplay extends SalesorderEntry {

    private String companyName, customerCompanyName, customerAddressName;
    private int count;

    public SoDisplay(long id, long companyId, long customerCompanyId, long customerAddressId, String documentDate, String deliveryDate, String lpo, String remark, String status, double roundAdj, String runningNo, String latitude, String longitude, int isUpload, String createDatetime, String modifyDatetime, String companyName, String customerCompanyName, String customerAddressName, int count) {
        super(id, companyId, customerCompanyId, customerAddressId, documentDate, deliveryDate, lpo, remark, status, roundAdj, runningNo, latitude, longitude, isUpload, createDatetime, modifyDatetime);
        this.companyName = companyName;
        this.customerCompanyName = customerCompanyName;
        this.customerAddressName = customerAddressName;
        this.count = count;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerCompanyName() {
        return customerCompanyName;
    }

    public void setCustomerCompanyName(String customerCompanyName) {
        this.customerCompanyName = customerCompanyName;
    }

    public String getCustomerAddressName() {
        return customerAddressName;
    }

    public void setCustomerAddressName(String customerAddressName) {
        this.customerAddressName = customerAddressName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
