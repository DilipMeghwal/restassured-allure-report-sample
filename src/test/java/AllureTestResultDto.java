import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Data
public class AllureTestResultDto {
    @CsvBindByName(column = "Status")
    private String status;

    @CsvBindByName(column = "Start Time")
    private String startTime;

    @CsvBindByName(column = "Stop Time")
    private String stopTime;

    @CsvBindByName(column = "Duration in ms")
    private String durationInMs;

    @CsvBindByName(column = "Parent Suite")
    private String parentSuite;

    @CsvBindByName(column = "Suite")
    private String suite;

    @CsvBindByName(column = "Sub Suite")
    private String subSuite;

    @CsvBindByName(column = "Test Class")
    private String testClass;

    @CsvBindByName(column = "Test Method")
    private String testMethod;

    @CsvBindByName(column = "Name")
    private String name;

    @CsvBindByName(column = "Description")
    private String description;

    @Override
    public String toString() {
        String color = "white";
        switch (status) {
            case "failed":
                color = "#F2928C";
                break;
            case "broken":
                color = "#F5F28F";
                break;
            case "passed":
                color = "#92DD96";
                break;
            case "skipped":
                color = "#8AF";
                break;
            case "unknown":
                color = "#F5B975";
                break;
        }
        long days = TimeUnit.MILLISECONDS.toDays(new BigDecimal(durationInMs).longValue());
        long hours = TimeUnit.MILLISECONDS.toHours(new BigDecimal(durationInMs).longValue()) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(new BigDecimal(durationInMs).longValue()) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(new BigDecimal(durationInMs).longValue()) % 60;
        long milliseconds = new BigDecimal(durationInMs).longValue() % 1000;

        String durationTime = (days > 0 ? days + " days " : "") + (hours > 0 ? hours + " hrs " : "") +
                (minutes > 0 ? minutes + " min " : "") + (seconds > 0 ? seconds + " sec " : "") +
                (milliseconds > 0 ? milliseconds + " ms" : "");
        return "\n<tr>\n" +
                "   <td class=\"testclass\">" + testClass + "</td>\n" +
                "   <td class=\"testcase\">" + name + "</td>\n" +
                "   <td class=\"duration\">" + durationTime + "</td>\n" +
                "   <td class=\"status\" bgcolor=" + color + ">" + status + "</td>\n" +
                "</tr>\n";
    }
}
