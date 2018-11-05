package my.com.engpeng.engpengsalesorder.utilities;

import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;

import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class PrintUtils {

    private static final String PRINT_END = "\n\n\n\n\n";
    private static final String PRINT_SEPERATOR = "---------------------------------------------";
    private static final String PRINT_HALF_SEPERATOR = "----------------------";
    private static final int LINE_CHAR_COUNT = 45;

    private static String formatLine(String line) {
        if (line.length() > LINE_CHAR_COUNT) {

            StringBuilder stringBuilder = new StringBuilder();
            int count = (int) Math.ceil((double) line.length() / (double) LINE_CHAR_COUNT);

            for (int i = 0; i < count; i++) {
                int start = i * LINE_CHAR_COUNT;
                int end = (i + 1) * LINE_CHAR_COUNT;
                if (end > line.length()) {
                    end = line.length();
                }
                stringBuilder.append(line.substring(start, end));
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();

        } else {
            return String.format("%-" + LINE_CHAR_COUNT + "s\n", line);
        }
    }

    private static String halfLine(String halfLine) {
        if (halfLine.length() > 22) {
            halfLine = halfLine.substring(0, 22);
        }
        return String.format("%-22s", halfLine);
    }

    public static String constructSalesorderPrintout(AppDatabase db, SalesorderEntry salesorderEntry) {
        String s = "";
        BranchEntry company = db.branchDao().loadBranchById(salesorderEntry.getCompanyId());
        CustomerCompanyEntry customer = db.customerCompanyDao().loadCustomerCompanyById(salesorderEntry.getCustomerCompanyId());
        CustomerCompanyAddressEntry address = db.customerCompanyAddressDao().loadCustomerCompanyAddressById(salesorderEntry.getCustomerAddressId());

        s += formatLine("");
        s += formatLine("             *** Salesorder ***             ");
        s += formatLine("");
        s += formatLine(company.getBranchName());
        s += formatLine(company.getBranchRegno());
        s += formatLine(company.getInvAddress());
        s += formatLine(PRINT_SEPERATOR);
        s += formatLine("Temp Salesorder No : " + StringUtils.getDisplayRunningNo(salesorderEntry.getRunningNo()));
        s += formatLine("Document Date : " + salesorderEntry.getDocumentDate());
        s += formatLine("Delivery Date : " + salesorderEntry.getDeliveryDate());
        s += formatLine("Cust. Code : " + customer.getPersonCustomerCompanyCode());
        s += formatLine("Cust. Name : " + customer.getPersonCustomerCompanyName());
        s += formatLine("Cust. Address : " + address.getPersonCustomerAddressName());
        s += formatLine(PRINT_SEPERATOR);

        s += formatLine(String.format("%14s%10s%10s%10s", "QTY/KG", "U/PRICE", "TAX", "AMOUNT"));
        double ttlQty = 0, ttlWgt = 0, ttlPrice = 0;
        List<SalesorderDetailEntry> details = db.salesorderDetailDao().loadAllSalesorderDetailsBySalesorderId(salesorderEntry.getId());
        for (SalesorderDetailEntry detail : details) {
            ttlQty += detail.getQty();
            ttlWgt += detail.getWeight();
            ttlPrice += detail.getTotalPrice();
            ItemPackingEntry item = db.itemPackingDao().loadItemPackingById(detail.getItemPackingId());
            s += formatLine(item.getSkuName());
            s += formatLine(String.format(Locale.getDefault(),
                    "%14s%10.2f%10.2f%10.2f",
                    String.format(Locale.getDefault(), "%.0f/%.0f", detail.getQty(), detail.getWeight()),
                    detail.getPrice(),
                    detail.getTaxAmt(),
                    detail.getTotalPrice()));
        }
        s += formatLine(PRINT_SEPERATOR);
        s += formatLine("E. & O.E.");
        s += formatLine(PRINT_SEPERATOR);
        s += formatLine("Item(s) : " + String.format(Locale.getDefault(), "%.0f", ttlQty));
        //s += formatLine("Total Weight : " + String.format(Locale.getDefault(), "%28.3fKG", ttlWgt));
        s += formatLine("Total Price  : " + String.format(Locale.getDefault(), "%30.2f", ttlPrice));
        s += formatLine("Round Adj    : " + String.format(Locale.getDefault(), "%30.2f", salesorderEntry.getRoundAdj()));
        s += formatLine("Aft Round Adj: " + String.format(Locale.getDefault(), "%30.2f", ttlPrice + salesorderEntry.getRoundAdj()));
        s += formatLine(PRINT_SEPERATOR);
        s += formatLine("Printed By : " + sUsername);
        s += formatLine("Date Time : " + StringUtils.getCurrentDateTime());
        s += formatLine(PRINT_END);

        return s;
    }
}
