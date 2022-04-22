import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class App {
    class Args {
        String csvFile;
        String outputDir = "." + File.separator;    // default output is current directory
    }
    Args processArgs(String[] args){
        Args arg = new Args();
        switch (args.length) {
            default:
            case 2: arg.outputDir = args[1];
            case 1: arg.csvFile = args[0];
                break;
        }
        return arg;
    }
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage:\n\tApp.class <file.csv> [<output_dir>]");
            return;
        }
        App mainProgram = new App();
        Args arg = mainProgram.processArgs(args);
        HashMap<String, HashMap<String, Record>> insuranceRosterTable = mainProgram.processFileByInsurance(arg.csvFile);

        for (String insuranceCompany : insuranceRosterTable.keySet()){
            var companyTable = insuranceRosterTable.get(insuranceCompany);
            var entriesArray = new Record[companyTable.size()];     // from DS, we know ID-to-entry is 1:1, so list size is for every entry
            int i = 0;
            for (String user : companyTable.keySet()) {
                entriesArray[i++] = companyTable.get(user);         // and just populate it
            }

            /* Now sort the array by last name alphabetical order. This is more efficient than keeping it sorted during processing insertion.
                That method would be O(n^2) where n = entries, whereas Java's array sort (adapted from Timsort) is O(nlogn).
            */
            Arrays.sort(entriesArray);

            // Lastly, we just need to write the alphabetized entries to a new csv file under this company
            mainProgram.outputFileByInsurance(arg.outputDir, insuranceCompany, entriesArray);
        }
    }

    class Record implements Comparable<Record>{
        String userId;
        String firstName;
        String lastName;
        int version;
        String insurance;
        Record(String[] delimitedLine) {
            userId = delimitedLine[0];
            firstName = delimitedLine[1];
            lastName = delimitedLine[2];
            version = Integer.valueOf(delimitedLine[3]);
            insurance = delimitedLine[4];
        }
        @Override
        public String toString() {
            return String.join(",", userId, firstName, lastName, String.valueOf(version), insurance);
        }
        @Override
        public int compareTo(Record other) {
            int cmp = this.lastName.compareToIgnoreCase(other.lastName);       // alphabetize by last name first
            if (cmp == 0)
                return this.firstName.compareToIgnoreCase(other.firstName);    // if last names are the same, compare first names
            else
                return cmp;
            // Note: these comparators are not locale-sensitive!
        }
    }
    /* Processed data structure: -- ASSUME: user IDs per company is unique for any first-last name pair
    { "Insurance A": {  "user123" : <entry>,
                        "user456" : <entry>,
                        ...                  },
      "Insurance B": {  "user123" : <entry>,
                        "user789" : <entry>,
                        ...                  }
    */
    HashMap<String, HashMap<String, Record>> processFileByInsurance(String csvFile) {
        // We're going to store unique ID & associated csv entry pairs per insurance company
        var insuranceToIdEntries = new HashMap<String, HashMap<String, Record>>();
        var csvFileObj = new File(csvFile);

        try (var freader = new BufferedReader( new FileReader(csvFileObj) )) {
            String curLine;

            while((curLine = freader.readLine()) != null){
                if (curLine.isBlank())
                    continue;
                // Parse the comma-deliminated line
                var entry = new Record(curLine.trim().split("\\s*,\\s*"));   // regex will remove any in-between spacing too just in case

                if (insuranceToIdEntries.containsKey(entry.insurance)) {
                    // File this entry under the respective insurance company
                    HashMap<String, Record> companyRoster = insuranceToIdEntries.get(entry.insurance);
                    if (companyRoster.containsKey(entry.userId)){
                        /*  There's another entry with the same insurance, user ID but a different version.
                            We want to keep the one with the higher version, so replace the one in the table if this entry's version is higher. */
                        if (entry.version > companyRoster.get(entry.userId).version){
                            companyRoster.put(entry.userId, entry);
                        }
                    } else {
                        // Just append the new entry, and we're done (for now)
                        companyRoster.put(entry.userId, entry);
                    }
                } else {
                    // Create a new table section for this insurance company, and add this entry to its fresh roster
                    insuranceToIdEntries.put(entry.insurance, new HashMap<String, Record>());
                    insuranceToIdEntries.get(entry.insurance).put(entry.userId, entry);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Error: %s does not exist.\n", csvFile);
            return null;
        } catch (IOException e) {
            System.out.println("Error: Problem reading or closing " + csvFile);
            return null;
        }
        return insuranceToIdEntries;
    }

    void outputFileByInsurance(String outputDir, String insuranceCompany, Record[] entriesArray) {
        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            System.out.println("Error: Problem creating directories to " + outputDir);
            return;
        }
        var outCsvFile = new File(Paths.get(outputDir, insuranceCompany + ".csv").toString());

        try (var fwriter = new BufferedWriter( new FileWriter(outCsvFile) )) {
            // Write csv entries in order (stable loop); this should be performant since it's buffered
            for (Record entry : entriesArray){
                fwriter.write(entry.toString());
                fwriter.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: Failed attempt to write to " + outCsvFile);
            return;
        }
    }
}
