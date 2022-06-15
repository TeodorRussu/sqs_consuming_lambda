package cke_migration.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationDataEntity {
    private Integer platformId;
    private String subsidiaryName;
    private Integer originSource;
    private String originToken;
    private String originReference;
    private Integer targetSource;
    private String targetToken;
    private String status;
    private Timestamp creationDatetime;
    private Timestamp processedDateTime;
    private String originTokenNotFoundAction;
    private String originReferenceNotFoundAction;
    private String additionalData;
    private String paymentInstrumentType;
    private String displayValue;
}
