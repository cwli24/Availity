import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

public class AppTest {
    @Test
    public void testProcessArgs() {
        App p = new App();
        var arg1 = p.processArgs(new String[]{"some_file.csv"});
        assertEquals(arg1.csvFile, "some_file.csv");
        assertEquals(arg1.outputDir, ".\\");
        var arg2 = p.processArgs(new String[]{"some_other_file.csv", "\\some_dir"});
        assertEquals(arg2.csvFile, "some_other_file.csv");
        assertEquals(arg2.outputDir, "\\some_dir");
    }

    @Test
    public void testProcessFileByInsurance() {
        App p = new App();
        File file = new File("src\\testfiles\\apptest.csv");
        assertTrue(file.exists());

        HashMap<String, HashMap<String, App.Record>> ds = p.processFileByInsurance(file.getAbsolutePath());
        var rec1 = ds.get("CompanyA").get("123");
        assertEquals(rec1.toString(), "123,John,Doe,12,CompanyA");
        var rec2 = ds.get("CompanyB").get("123");
        assertEquals(rec2.toString(), "123,John,Deere,12,CompanyB");
        var rec3 = ds.get("CompanyA").get("456");
        assertEquals(rec3.toString(), "456,Shang,Chi,3,CompanyA");
        var rec4 = ds.get("CompanyA").get("789");
        assertEquals(rec4.toString(), "789,Doctor,Strange,1,CompanyA");
    }

    @Test
    public void testEntriesSorting() {
        App p = new App();
        File file = new File("src\\testfiles\\apptest.csv");
        HashMap<String, HashMap<String, App.Record>> ds = p.processFileByInsurance(file.getAbsolutePath());

        // This section is just from the source code BTW
        var companyTable = ds.get("CompanyA");
        var entriesArray = new App.Record[companyTable.size()];
        int i = 0;
        for (String user : companyTable.keySet()) {
            entriesArray[i++] = companyTable.get(user);
        }
        assertTrue(entriesArray.length == 3);
        assertTrue(i == 3);
        
        Arrays.sort(entriesArray);
        assertEquals("456,Shang,Chi,3,CompanyA", entriesArray[0].toString());
        assertEquals("123,John,Doe,12,CompanyA", entriesArray[1].toString());
        assertEquals("789,Doctor,Strange,1,CompanyA", entriesArray[2].toString());
    }

    @Test
    public void testOutputFileByInsurance() {
        App p = new App();
        App.Record[] arr = {
            p.new Record("456,Shang,Chi,3,CompanyA".split(",")),
            p.new Record("123,John,Doe,12,CompanyA".split(",")),
            p.new Record("789,Doctor,Strange,1,CompanyA".split(","))
        };
        p.outputFileByInsurance("output", "companyA", arr);
    }
}
