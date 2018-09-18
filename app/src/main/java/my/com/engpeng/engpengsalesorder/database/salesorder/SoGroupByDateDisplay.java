package my.com.engpeng.engpengsalesorder.database.salesorder;

public class SoGroupByDateDisplay {
    private String dateType;
    private String documentDate;
    private int count;

    public SoGroupByDateDisplay(String dateType, String documentDate, int count) {
        this.dateType = dateType;
        this.documentDate = documentDate;
        this.count = count;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
