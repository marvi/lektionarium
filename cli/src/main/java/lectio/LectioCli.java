package lectio;

import lectio.format.CsvFormat;
import lectio.format.IcalFormat;
import lectio.format.JsonFormat;
import lectio.format.TextFormat;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@CommandLine.Command(description = "Generates a liturgical calendar")
public class LectioCli implements Callable<Void> {

    @Parameters(index = "0", description = "The year to create calendar for")
    private int year;

    @Option(names = {"-f", "--format"}, description = "json, txt, csv, ical")
    private String format = "txt";

    @Option(names = {"-s", "--stdout"}, description = "Print result to standard out")
    private boolean stdout = false;


    public static void main(String[] argv) throws Exception {
      CommandLine.call(new LectioCli(), argv);
    }

    @Override
    public Void call() throws Exception {
      String result = "";
      switch(format) {
        case "json":
          result = JsonFormat.getJsonForYear(year);
          break;
        case "ical":
          result = IcalFormat.getIcalForYear(year);
          break;
        case "txt":
          result = TextFormat.getTextForYear(year);
          break;
        case "csv":
          result = CsvFormat.getCsvForYear(year);
          break;
      }
      System.out.println(result);
      return null;
    }


}
