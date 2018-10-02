package my.com.engpeng.engpengsalesorder.utilities;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;

public class PrintUtils {

    private static final String PRINT_END = "\n\n\n\n\n";
    private static final String PRINT_SEPERATOR = "---------------------------------------------";
    private static final String PRINT_HALF_SEPERATOR = "----------------------";

    private static String formatLine(String line) {
        if (line.length() > 45) {
            line = line.substring(0, 45);
        }
        return String.format("%-42s", line) + "\n";
    }

    private static String halfLine(String halfLine) {
        if (halfLine.length() > 22) {
            halfLine = halfLine.substring(0, 22);
        }
        return String.format("%-22s", halfLine);
    }

    public static String constructSalesorderPrintout(AppDatabase db, SalesorderEntry salesorderEntry) {
        String s = "";
        BranchEntry branchEntry = db.branchDao().loadBranchById(salesorderEntry.getCompanyId());

        s += formatLine("");
        s += formatLine(branchEntry.getBranchName());
        s += formatLine(branchEntry.getBranchRegno());
        s += formatLine(branchEntry.getInvAddress());

        return s;
    }
}
