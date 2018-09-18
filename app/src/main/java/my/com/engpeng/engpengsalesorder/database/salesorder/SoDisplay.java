package my.com.engpeng.engpengsalesorder.database.salesorder;

public class SoDisplay extends SalesorderEntry {

    private String companyName, customerCompanyName, customerAddressName;

    public SoDisplay(long id,
                     long companyId, long customerCompanyId, long customerAddressId,
                     String documentDate, String deliveryDate,
                     String lpo, String remark,
                     String status, String runningNo, int isUpload,
                     String createDatetime, String modifyDatetime,
                     String companyName, String customerCompanyName, String customerAddressName) {
        super(id, companyId, customerCompanyId, customerAddressId, documentDate, deliveryDate, lpo, remark, status, runningNo, isUpload, createDatetime, modifyDatetime);

        this.companyName = companyName;
        this.customerCompanyName = customerCompanyName;
        this.customerAddressName = customerAddressName;
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
}
