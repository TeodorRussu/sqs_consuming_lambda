package cke_migration.checkout_com;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import cke_migration.entity.MigrationDataEntity;
import cke_migration.utils.SqlBuildHelper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class CheckoutComMigrationApp {
    //manually update with data from production db before running the script
    static final Integer PLATFORM_ID = 10001;
    static final String SUBSIDIARY_NAME = "FP_KH";
    static final Integer ORIGIN_SOURCE = 10001;
    static final Integer TARGET_SOURCE = 440010001;

    public static final int FLUSH_THRESHOLD = 100000000;
    public static final String ORIGIN_TOKEN_NOT_FOUND_ACTION = "Skip";
    public static final String ORIGIN_REFERENCE_NOT_FOUND_ACTION = "Skip";

    static final String CUSTOMER_ID = "old_customer_id";
    static final String ORIGIN_TOKEN = "old_card_id";
    static final String TARGET_TOKEN = "cko_cards_id";
    static final String STATUS = "Pending";
    public static final String CHECKOUT_CUSTOMER_ID = "cko_customer_id";
    public static final String DISPLAY_VALUE = "Last4";
    private String sourceFilePath;
    private String targetFileDir;
    List<String>outputFiles = new ArrayList<>();

    private List<MigrationDataEntity> entities = new ArrayList<>(FLUSH_THRESHOLD);

    public CheckoutComMigrationApp(String sourceFilePath, String targetFileDir) {
        this.sourceFilePath = sourceFilePath;
        this.targetFileDir = targetFileDir;

        entities = new ArrayList<>(FLUSH_THRESHOLD);
    }


    public List<String> startMigration(File input) throws IOException {
         return createSqlScripts(input);
    }


    private static List<Map<String, String>> parseInputFile(File file) throws IOException {
        List<Map<String, String>> response = new LinkedList<>();
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try (MappingIterator<Map<String, String>> iterator = mapper.readerFor(Map.class).with(schema).readValues(file)) {
            while (iterator.hasNext()) {
                response.add(iterator.next());
            }
        }
        return response;
    }

    public List<String> createSqlScripts(File input) throws IOException {
        var parsedCsv = parseInputFile(input);

        parsedCsv.forEach(row -> {
            var entry = convertRecordToMigrationEntity(row);
            processRecord(entry);
        });
        flushEntities();
        return outputFiles;
    }

    private void processRecord(MigrationDataEntity entity) {
        entities.add(entity);
        if (entities.size() == FLUSH_THRESHOLD) {
            flushEntities();
        }
    }

    @SneakyThrows(IOException.class)
    private void flushEntities() {
        writeSqlScriptFile();
        entities.clear();
    }

    private MigrationDataEntity convertRecordToMigrationEntity(Map<String, String> row) {

        String oldCustomerProfileId = row.get(CUSTOMER_ID).strip();
        String oldPaymentProfileId = row.get(ORIGIN_TOKEN).strip();
        String paymentInstrumentId = row.get(TARGET_TOKEN).strip();
        String displayValue = row.get(DISPLAY_VALUE).strip();
        Timestamp creationDateTime = new Timestamp(System.currentTimeMillis());

        return MigrationDataEntity.builder()
                .platformId(PLATFORM_ID)
                .subsidiaryName(SUBSIDIARY_NAME)
                .originSource(ORIGIN_SOURCE)
                .originToken(oldPaymentProfileId)
                .originReference(oldCustomerProfileId)
                .targetSource(TARGET_SOURCE)
                .targetToken(paymentInstrumentId)
                .displayValue(displayValue)
                .status(STATUS)
                .creationDatetime(creationDateTime)
                .processedDateTime(null)
                .originTokenNotFoundAction(ORIGIN_TOKEN_NOT_FOUND_ACTION)
                .originReferenceNotFoundAction(ORIGIN_REFERENCE_NOT_FOUND_ACTION)
                .additionalData(String.format("{\"checkout_customer_id\": \"%s\",\"isUsedForSubscription\":false\",\"bin\":%s\"}", row.get(CHECKOUT_CUSTOMER_ID), row.get("BIN")))
                .build();
    }

    private void writeSqlScriptFile() throws IOException {
        String filename = "/tmp/"+System.currentTimeMillis() + ".sql";

        try (FileWriter writer = new FileWriter(String.valueOf(Paths.get(filename)))) {
            writer.write("BEGIN TRANSACTION;" + System.lineSeparator());
            for (MigrationDataEntity migrationDataEntity : entities) {
                var insertSql = SqlBuildHelper.createInsertString(migrationDataEntity);
                if (insertSql != null && !insertSql.trim().isEmpty()) {
                    writer.write(insertSql + ";" + System.lineSeparator());
                }
            }
            writer.write("COMMIT;" + System.lineSeparator());
        }
        outputFiles.add(filename);

    }

}
