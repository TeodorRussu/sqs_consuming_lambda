package cke_migration.utils;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import cke_migration.entity.MigrationDataEntity;
import lombok.experimental.UtilityClass;

import java.sql.Types;

@UtilityClass
public class SqlBuildHelper {
	private static final DbTable table;
	private static final DbColumn platformId;
	private static final DbColumn subsidiaryName;
	private static final DbColumn originSource;
	private static final DbColumn originToken;
	private static final DbColumn originReference;
	private static final DbColumn targetSource;
	private static final DbColumn targetToken;
	private static final DbColumn displayValue;
	private static final DbColumn status;
	private static final DbColumn creationDatetime;
	private static final DbColumn processedDateTime;
	private static final DbColumn originTokenNotFoundAction;
	private static final DbColumn originReferenceNotFoundAction;
	private static final DbColumn additionalData;
	public static final String EMPTY_STRING = "";

	static {
		DbSpec spec = new DbSpec();
		DbSchema schema = spec.addDefaultSchema();
		table = schema.addTable("alfred_payment_instrument_token_migration");
		platformId = table.addColumn("platform_id", Types.BIGINT, null);
		subsidiaryName = table.addColumn("subsidiary_name", Types.VARCHAR, 128);
		originSource = table.addColumn("origin_source", Types.BIGINT, null);
		originToken = table.addColumn("origin_token", Types.VARCHAR, 1024);
		originReference = table.addColumn("origin_reference", Types.VARCHAR, 1024);
		targetSource = table.addColumn("target_source", Types.BIGINT, null);
		targetToken = table.addColumn("target_token", Types.VARCHAR, 1024);
		displayValue = table.addColumn("display_value", Types.VARCHAR, 128);
		status = table.addColumn("status", Types.VARCHAR, 32);
		creationDatetime = table.addColumn("creation_datetime", Types.TIMESTAMP, null);
		processedDateTime = table.addColumn("processed_datetime", Types.TIMESTAMP, null);
		originTokenNotFoundAction = table.addColumn("origin_token_not_found_action", Types.VARCHAR, 128);
		originReferenceNotFoundAction = table.addColumn("origin_reference_not_found_action", Types.VARCHAR, 128);
		additionalData = table.addColumn("additional_data", Types.VARCHAR, 4096);
	}

	public static String createInsertString(MigrationDataEntity migrationDataEntity) {
		if (migrationDataEntity == null){
			return EMPTY_STRING;
		}
		return new InsertQuery(table)
			.addColumn(platformId, migrationDataEntity.getPlatformId())
			.addColumn(subsidiaryName, migrationDataEntity.getSubsidiaryName())
			.addColumn(originSource, migrationDataEntity.getOriginSource())
			.addColumn(originToken, migrationDataEntity.getOriginToken())
			.addColumn(originReference, migrationDataEntity.getOriginReference())
			.addColumn(targetSource, migrationDataEntity.getTargetSource())
			.addColumn(targetToken, migrationDataEntity.getTargetToken())
			.addColumn(displayValue, migrationDataEntity.getDisplayValue())
			.addColumn(status, migrationDataEntity.getStatus())
			.addColumn(creationDatetime, migrationDataEntity.getCreationDatetime())
			.addColumn(processedDateTime, migrationDataEntity.getProcessedDateTime())
			.addColumn(originTokenNotFoundAction, migrationDataEntity.getOriginTokenNotFoundAction())
			.addColumn(originReferenceNotFoundAction, migrationDataEntity.getOriginReferenceNotFoundAction())
			.addColumn(additionalData, migrationDataEntity.getAdditionalData())
			.validate()
			.toString();
	}
}
