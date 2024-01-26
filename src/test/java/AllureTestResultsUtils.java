import com.google.common.util.concurrent.AtomicDouble;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import dto.AllureTestResultDto;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AllureTestResults {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void main(String[] args) {
        log.info("Reading allure test results");
        try {
            String suitesCsvFile = System.getProperty("user.dir") + File.separator + "target" +
                    File.separator + "site" + File.separator + "allure-maven-plugin" + File.separator + "data" +
                    File.separator + "suites.csv";

            Path suitesCsvFilePath = Paths.get(suitesCsvFile);

            HeaderColumnNameMappingStrategy<AllureTestResultDto> beanStrategy = new HeaderColumnNameMappingStrategy<AllureTestResultDto>();
            beanStrategy.setType(AllureTestResultDto.class);

            BufferedReader reader = Files.newBufferedReader(suitesCsvFilePath,
                    StandardCharsets.UTF_8);

            CsvToBean<AllureTestResultDto> csvToBean = new CsvToBeanBuilder<AllureTestResultDto>(reader)
                    .withMappingStrategy(beanStrategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<AllureTestResultDto> testResults = csvToBean.parse();

            reader.close();

            log.info("Test Suite csv loaded");

            AtomicInteger failed = new AtomicInteger();
            AtomicInteger broken = new AtomicInteger();
            AtomicInteger passed = new AtomicInteger();
            AtomicInteger skipped = new AtomicInteger();
            AtomicInteger unknown = new AtomicInteger();
            AtomicDouble duration = new AtomicDouble();

            StringBuffer buffer = new StringBuffer();
            testResults.forEach(result -> {
                buffer.append(result.toString());
                duration.getAndAdd(Double.parseDouble(result.getDurationInMs()));
                switch (result.getStatus()) {
                    case "failed":
                        failed.getAndIncrement();
                        break;
                    case "broken":
                        broken.getAndIncrement();
                        break;
                    case "passed":
                        passed.getAndIncrement();
                        break;
                    case "skipped":
                        skipped.getAndIncrement();
                        break;
                    case "unknown":
                        unknown.getAndIncrement();
                        break;
                }
            });

            int totalTestCases = failed.get() + broken.get() + passed.get() + skipped.get() + unknown.get();

            long days = TimeUnit.MILLISECONDS.toDays(duration.longValue());
            long hours = TimeUnit.MILLISECONDS.toHours(duration.longValue()) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration.longValue()) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration.longValue()) % 60;
            long milliseconds = duration.longValue() % 1000;

            String durationTime = (days > 0 ? days + " days " : "") + (hours > 0 ? hours + " hrs " : "") +
                    (minutes > 0 ? minutes + " min " : "") + (seconds > 0 ? seconds + " sec " : "") +
                    (milliseconds > 0 ? milliseconds + " ms" : "");

            String tableHead = "<table id=\"tablesorter\" class=\"stats-table table-hover\" border=\"1px\">" +
                    " <thead>\n" +
                    "  <tr class=\"header dont-sort\" bgcolor=\"#66CCEE\">\n" +
                    "   <th colspan=\"1\">Test Class</th>\n" +
                    "   <th colspan=\"1\">Test Case</th>\n" +
                    "   <th colspan=\"1\">Duration</th>\n" +
                    "   <th colspan=\"1\">Status</th>\n" +
                    "  </tr>\n" +
                    " </thead>";

            String tableFooter = "</tbody>\n" +
                    " <tfoot class=\"total\" bgcolor=\"lightgray\">\n" +
                    "  <tr>\n" +
                    "   <td>" +
                    "       Passed : " + df.format(passed.doubleValue() * 100 / totalTestCases) + "%<br/>\n" +
                    "       Failed : " + df.format(failed.doubleValue() * 100 / totalTestCases) + "%<br/>\n" +
                    "       Broken : " + df.format(broken.doubleValue() * 100 / totalTestCases) + "%<br/>\n" +
                    "       Skipped : " + df.format(skipped.doubleValue() * 100 / totalTestCases) + "%<br/>\n" +
                    "       Unknown : " + df.format(unknown.doubleValue() * 100 / totalTestCases) + "%</br/>\n" +
                    "   </td>\n" +
                    "   <td>" +
                    "       Total : " + totalTestCases + "<br/>\n" +
                    "       Passed : " + passed.get() + "<br/>\n" +
                    "       Failed : " + failed.get() + "<br/>\n" +
                    "       Broken : " + broken.get() + "<br/>\n" +
                    "       Skipped : " + skipped.get() + "<br/>\n" +
                    "       Unknown : " + unknown.get() + "<br/>\n" +
                    "   </td>\n" +
                    "   <td>Total Execution Time : " + durationTime + "</td>\n" +
                    "   <td></td>\n" +
                    "  </tr>\n" +
                    " </tfoot>\n" +
                    "</table>";

            File theDir = new File(System.getProperty("user.dir") + File.separator + "target" +
                    File.separator + "temp");

            if (!theDir.exists()) {
                theDir.mkdirs();
            }

            File file = new File(System.getProperty("user.dir") + File.separator + "target" +
                    File.separator + "temp" + File.separator + "AllureTestResults.txt");

            if (file.createNewFile()) {
                log.info("AllureTestResults file created: " + file.getName());
                FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + File.separator + "target" +
                        File.separator + "temp" + File.separator + "AllureTestResults.txt");
                myWriter.write(tableHead + buffer + tableFooter);
                myWriter.close();
            } else {
                log.info("AllureTestResults file already exists.");
                FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + File.separator + "target" +
                        File.separator + "temp" + File.separator + "AllureTestResults.txt");
                myWriter.write(tableHead + buffer + tableFooter);
                myWriter.close();
            }

            log.info("AllureTestResults.txt created successfully");
        } catch (Exception e) {
            log.info("Error : {0}", e);
            System.setProperty("AllureTestResults", "Failed to capture allure Test Results");
        }
    }
}
