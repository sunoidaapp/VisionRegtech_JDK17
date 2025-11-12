package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.CustomersVb;
import com.vision.vb.VisionUsersVb;

@Component
public class CustomerManualDao extends AbstractDao<CustomerManualColVb> {

	public List<CustomerManualColVb> getQueryPopupResults1(CustomerManualColVb dObj) {
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from ("+
				"SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,\r\n"
						+ "				       CUST_MOD_STATUS_NT, CUST_MOD_STATUS,\r\n"
						+ "				       RECORD_INDICATOR_NT, RECORD_INDICATOR,\r\n"
						+ "				       MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED\r\n"
						+ "				  FROM CUSTOMER_MANUAL\r\n" + "				 WHERE COUNTRY = ?\r\n"
						+ "				   AND LE_BOOK = ?\r\n" + "				   AND CUSTOMER_ID = ? ) TAppr"
						+ "				   ");

		String strWhereNotExists = new String(" Not Exists (Select 'X' "
				+ "  From CUSTOMER_MANUAL_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY And TPend.LE_BOOK = TAppr.LE_BOOK And TPend.CUSTOMER_ID = TAppr.CUSTOMER_ID AND TAppr.COLUMN_NAME= TPend.COLUMN_NAME)");
		StringBuffer strBufPending = new StringBuffer("select * from ("+
				"SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,\r\n"
						+ "				       CUST_MOD_STATUS_NT, CUST_MOD_STATUS,\r\n"
						+ "				       RECORD_INDICATOR_NT, RECORD_INDICATOR,\r\n"
						+ "				       MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED\r\n"
						+ "				  FROM CUSTOMER_MANUAL_PEND" + "				 WHERE COUNTRY = ?\r\n"
						+ "				   AND LE_BOOK = ?\r\n" + "				   AND CUSTOMER_ID = ?)TPend"
						+ "				   ");
		try {
			params.add(dObj.getCountry());
			params.add(dObj.getLeBook());
			params.add(dObj.getCustomerId());
			params.add(dObj.getCountry());
			params.add(dObj.getLeBook());
			params.add(dObj.getCustomerId());

			String orderBy = " Order By DATE_LAST_MODIFIED  DESC";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params,
					getMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((strBufApprove == null) ? "strBufApprove is Null" : strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			if (params != null)
				for (int i = 0; i < params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;

		}
	}

	public List<CustomerManualColVb> getQueryPopupResults(CustomerManualColVb dObj) {
	    // Validate required inputs early
	    Objects.requireNonNull(dObj, "dObj must not be null");
	    final String country   = Objects.requireNonNull(dObj.getCountry(),   "country must not be null");
	    final String leBook    = Objects.requireNonNull(dObj.getLeBook(),    "leBook must not be null");
	    final String customerId= Objects.requireNonNull(dObj.getCustomerId(), "customerId must not be null");

	    // SQL text blocks (Java 15+) for readability
	    final String SQL_APPROVE = """
	        select * from (
	            SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
	                   CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
	                   RECORD_INDICATOR_NT, RECORD_INDICATOR,
	                   MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
	              FROM CUSTOMER_MANUAL
	             WHERE COUNTRY = ?
	               AND LE_BOOK = ?
	               AND CUSTOMER_ID = ?
	        ) TAppr
	        """;

	    final String SQL_PENDING = """
	        select * from (
	            SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
	                   CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
	                   RECORD_INDICATOR_NT, RECORD_INDICATOR,
	                   MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
	              FROM CUSTOMER_MANUAL_PEND
	             WHERE COUNTRY = ?
	               AND LE_BOOK = ?
	               AND CUSTOMER_ID = ?
	        ) TPend
	        """;

	    final String WHERE_NOT_EXISTS = """
	        NOT EXISTS (
	            SELECT 1
	              FROM CUSTOMER_MANUAL_PEND TPend
	             WHERE TPend.COUNTRY     = TAppr.COUNTRY
	               AND TPend.LE_BOOK     = TAppr.LE_BOOK
	               AND TPend.CUSTOMER_ID = TAppr.CUSTOMER_ID
	               AND TAppr.COLUMN_NAME = TPend.COLUMN_NAME
	        )
	        """;

	    final String ORDER_BY = " ORDER BY DATE_LAST_MODIFIED DESC";

	    Vector<Object> params = new Vector<>();
	    params.add(country);
	    params.add(leBook);
	    params.add(customerId);
	 

	    try {
	        // If your downstream method requires StringBuffer, wrap the strings here.
	        return getQueryPopupResults(
	            dObj,
	            new StringBuffer(SQL_PENDING),
	            new StringBuffer(SQL_APPROVE),
	            WHERE_NOT_EXISTS,
	            ORDER_BY,
	            params,
	            getMapper()
	        );
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	        logger.error("Error in getQueryPopupResults", ex);
	        logger.error("SQL_APPROVE:\n{}", SQL_APPROVE);
	        logger.error("SQL_PENDING:\n{}", SQL_PENDING);
	        logger.error("WHERE_NOT_EXISTS:\n{}", WHERE_NOT_EXISTS);
	        for (int i = 0; i < params.size(); i++) {
	            logger.error("param[{}]={}", i, params.get(i));
	        }
	        return null;
	    }
	}


	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerManualColVb v = new CustomerManualColVb();
				v.setCountry(rs.getString("COUNTRY"));
				v.setLeBook(rs.getString("LE_BOOK"));
				v.setCustomerId(rs.getString("CUSTOMER_ID"));
				String columnName = rs.getString("COLUMN_NAME");
				v.setColumnName(columnName);
				v.setColumnValue(rs.getString("COLUMN_VALUE"));
				v.setCustModStatusNt(rs.getInt("CUST_MOD_STATUS_NT"));
				v.setCustModStatus(rs.getInt("CUST_MOD_STATUS"));
				v.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				v.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				v.setMaker(rs.getInt("MAKER"));
				v.setVerifier(rs.getInt("VERIFIER"));
				// Use java.sql.Timestamp in your VB getters/setters or map to LocalDateTime
				// then convert on insert
				v.setDateCreation(rs.getString("DATE_CREATION"));
				v.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				v.setVariableName(CommonUtils.toCamelCase(columnName));
				return v;
			}
		};
		return mapper;
	}

	public List<CustomerManualColVb> findAll() {

		String sql = """
				SELECT COUNTRY,
				       LE_BOOK,
				       COLUMN_NAME,
				       COLUMN_STATUS_NT,
				       COLUMN_STATUS,
				       RECORD_INDICATOR_NT,
				       RECORD_INDICATOR,
				       MAKER,
				       VERIFIER,
				       DATE_CREATION,
				       DATE_LAST_MODIFIED
				FROM CUSTOMER_MANUAL_COL_LIST
				ORDER BY COUNTRY, LE_BOOK, COLUMN_NAME
				""";

		return jdbcTemplate.query(sql, new CustomerManualColRowMapper());
	}

	public List<CustomersVb> findAllGroupedAsCustomers() {
		var all = findAll(); // List<CustomerManualColVb> (already has columnName, statuses, variableName,
								// and COUNTRY/LE_BOOK in grouping map)

		// sort rows inside each group (optional)
		Comparator<CustomerManualColVb> rowOrder = Comparator.comparing(CustomerManualColVb::getColumnName,
				Comparator.nullsLast(String::compareTo));

		// group by COUNTRY -> LE_BOOK
		var grouped = all.stream().collect(Collectors.groupingBy(CustomerManualColVb::getCountry,
				Collectors.groupingBy(CustomerManualColVb::getLeBook)));

		// flatten to List<CustomersVb>
		return grouped.entrySet().stream()
				.flatMap(countryEntry -> countryEntry.getValue().entrySet().stream().map(leEntry -> {
					var cvb = new CustomersVb(); // no-arg POJO
					cvb.setCbDomicile(countryEntry.getKey()); // COUNTRY -> cbDomicile
					cvb.setVisionSbu(leEntry.getKey()); // LE_BOOK -> visionSbu
					cvb.setManualList(leEntry.getValue().stream().sorted(rowOrder).toList());
					cvb.setEntity(cvb.getManualList().get(0).getEntity());
					return cvb;
				})).sorted(Comparator.comparing(CustomersVb::getCbDomicile) // sort on CustomersVb getters
						.thenComparing(CustomersVb::getVisionSbu))
				.toList();
	}

	public class CustomerManualColRowMapper implements RowMapper<CustomerManualColVb> {

		@Override
		public CustomerManualColVb mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustomerManualColVb v = new CustomerManualColVb();

			v.setCountry(rs.getString("COUNTRY"));
			v.setLeBook(rs.getString("LE_BOOK"));
			String colName = rs.getString("COLUMN_NAME");
			v.setColumnName(colName);

			v.setColumnStatusNt(getNullableInt(rs, "COLUMN_STATUS_NT"));
			v.setColumnStatus(getNullableInt(rs, "COLUMN_STATUS"));

			v.setRecordIndicatorNt(getNullableInt(rs, "RECORD_INDICATOR_NT"));
			v.setRecordIndicator(getNullableInt(rs, "RECORD_INDICATOR"));

			v.setMaker(rs.getInt("MAKER"));
			v.setVerifier(rs.getInt("VERIFIER"));

			v.setDateCreation(getDateTime(rs, "DATE_CREATION"));
			v.setDateLastModified(getDateTime(rs, "DATE_LAST_MODIFIED"));

			v.setVariableName(CommonUtils.toCamelCase(colName));

			return v;
		}

		private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
			int val = rs.getInt(column);
			return rs.wasNull() ? null : val;
		}

		private String getDateTime(ResultSet rs, String column) throws SQLException {
			var ts = rs.getTimestamp(column);
			return ts == null ? null : ts.toLocalDateTime().toString();
		}
	}

	protected int doInsertCustomerManualHistory(CustomerManualColVb vObject) {
		
		int refId = getMaxSeq(vObject);
		var sql = """
				INSERT INTO CUSTOMER_MANUAL_HIS (REF_ID,
				    COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
				    CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
				    RECORD_INDICATOR_NT, RECORD_INDICATOR,
				    MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
				) VALUES ( ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, %S, %S)
				""".formatted(systemDate, systemDate);
		var args = new Object[] { refId,vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				vObject.getColumnName(), vObject.getColumnValue(), vObject.getCustModStatusNt(),
				vObject.getCustModStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier()};

		return getJdbcTemplate().update(sql, args);
	}

	private CustomerManualColVb findExistingManual(CustomerManualColVb customerManualColVb, boolean flag) {
		String tableName = "";
		if (flag) {
			tableName = "CUSTOMER_MANUAL";
		} else {
			tableName = "CUSTOMER_MANUAL_PEND";
		}
		var sql = """
				SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
				       CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
				       RECORD_INDICATOR_NT, RECORD_INDICATOR,
				       MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
				  FROM %S
				 WHERE COUNTRY = ?
				   AND LE_BOOK = ?
				   AND CUSTOMER_ID = ?
				   AND COLUMN_NAME = ?
				""".formatted(tableName);

		return getJdbcTemplate().query(sql, rs -> {
			if (!rs.next())
				return null;
			var v = new CustomerManualColVb();
			v.setCountry(rs.getString("COUNTRY"));
			v.setLeBook(rs.getString("LE_BOOK"));
			v.setCustomerId(rs.getString("CUSTOMER_ID"));
			v.setColumnName(rs.getString("COLUMN_NAME"));
			v.setColumnValue(rs.getString("COLUMN_VALUE"));
			v.setCustModStatusNt(rs.getInt("CUST_MOD_STATUS_NT"));
			v.setCustModStatus(rs.getInt("CUST_MOD_STATUS"));
			v.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
			v.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
			v.setMaker(rs.getInt("MAKER"));
			v.setVerifier(rs.getInt("VERIFIER"));
			// Use java.sql.Timestamp in your VB getters/setters or map to LocalDateTime
			// then convert on insert
			v.setDateCreation(rs.getString("DATE_CREATION"));
			v.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
			return v;
		}, customerManualColVb.getCountry(), customerManualColVb.getLeBook(), customerManualColVb.getCustomerId(),
				customerManualColVb.getColumnName());
	}

	protected int doUpdateCustomerManual(CustomerManualColVb vObject, boolean flag) {
		var tableName = flag ? "CUSTOMER_MANUAL" : "CUSTOMER_MANUAL_PEND";

		var sql = """
				UPDATE %s
				   SET COLUMN_VALUE = ?,
				       CUST_MOD_STATUS_NT = ?,
				       CUST_MOD_STATUS = ?,
				       RECORD_INDICATOR_NT = ?,
				       RECORD_INDICATOR = ?,
				       VERIFIER = ?,
				       DATE_LAST_MODIFIED = %s
				 WHERE COUNTRY = ?
				   AND LE_BOOK = ?
				   AND CUSTOMER_ID = ?
				   AND COLUMN_NAME = ?
				""".formatted(tableName, systemDate); // keep your systemDate token

		var args = new Object[] { vObject.getColumnValue(), vObject.getCustModStatusNt(), vObject.getCustModStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getVerifier(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(), vObject.getColumnName() };

		return getJdbcTemplate().update(sql, args);
	}

	protected int doInsertCustomerManual(CustomerManualColVb vObject, boolean flag) {
		var tableName = flag ? "CUSTOMER_MANUAL" : "CUSTOMER_MANUAL_PEND";

		var sql = """
				INSERT INTO %s (
				    COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
				    CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
				    RECORD_INDICATOR_NT, RECORD_INDICATOR,
				    MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
				) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, %s, %s )
				""".formatted(tableName, systemDate, systemDate);

		var args = new Object[] { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				vObject.getColumnName(), vObject.getColumnValue(), vObject.getCustModStatusNt(),
				vObject.getCustModStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };

		return getJdbcTemplate().update(sql, args);
	}

	protected int doDeleteCustomerManual(CustomerManualColVb vObject, boolean flag) {
		var tableName = flag ? "CUSTOMER_MANUAL" : "CUSTOMER_MANUAL_PEND";

		var sql = """
				DELETE FROM %s
				 WHERE COUNTRY = ?
				   AND LE_BOOK = ?
				   AND CUSTOMER_ID = ?
				   AND COLUMN_NAME = ?
				""".formatted(tableName);

		var args = new Object[] { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				vObject.getColumnName() };

		return getJdbcTemplate().update(sql, args);
	}
	protected int doDeleteCustomerManual(CustomersVb vObject, boolean flag) {
		var tableName = flag ? "CUSTOMER_MANUAL" : "CUSTOMER_MANUAL_PEND";

		var sql = """
				DELETE FROM %s
				 WHERE COUNTRY = ?
				   AND LE_BOOK = ?
				   AND CUSTOMER_ID = ?
				""".formatted(tableName);

		var args = new Object[] { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				 };

		return getJdbcTemplate().update(sql, args);
	}
	@org.springframework.transaction.annotation.Transactional
	public int deleteAndInsertCustomerManualAppr(CustomerManualColVb newRow) {
		boolean main = true; // CUSTOMER_MANUAL
		var oldRow = findExistingManual(newRow, main);

		int count = 0;
		if (oldRow != null) {
			count += doInsertCustomerManualHistory(oldRow); // backup
			count += doDeleteCustomerManual(oldRow, main); // delete from main
		}
		count += doInsertCustomerManual(newRow, main); // insert new into main
		return count;
	}

	@org.springframework.transaction.annotation.Transactional
	public int deleteAndInsertCustomerManualPend(CustomerManualColVb newRow) {
		boolean pend = false; // CUSTOMER_MANUAL_PEND
		var oldRow = findExistingManual(newRow, pend);

		int count = 0;
		if (oldRow != null) {
			count += doInsertCustomerManualHistory(oldRow); // backup (to HIS)
			count += doDeleteCustomerManual(oldRow, pend); // delete from PEND
		}
		count += doInsertCustomerManual(newRow, pend); // insert new into PEND
		return count;
	}
	public int getMaxSeq(CustomerManualColVb vObject) {
	    String sql = """
	            SELECT %s(MAX(REF_ID), 0)+1
	              FROM CUSTOMER_MANUAL_HIS
	             WHERE COUNTRY = ?
	               AND LE_BOOK = ?
	               AND CUSTOMER_ID = ?
	               AND COLUMN_NAME = ?
	            """.formatted(nullFun);

	    Object[] args = {
	            vObject.getCountry(),
	            vObject.getLeBook(),
	            vObject.getCustomerId(),
	            vObject.getColumnName()
	    };

	    return getJdbcTemplate().queryForObject(sql, Integer.class, args);
	}
	// === BEGIN: Additions for approval flow ===

	/** Fetch all MANUAL_PEND rows for a customer key. */
	public List<CustomerManualColVb> selectPendByKey(CustomerManualColVb key) {
	    final String sql = """
	        SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
	               CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
	               RECORD_INDICATOR_NT, RECORD_INDICATOR,
	               MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
	          FROM CUSTOMER_MANUAL_PEND
	         WHERE COUNTRY = ?
	           AND LE_BOOK = ?
	           AND CUSTOMER_ID = ?
	        """;
	    return getJdbcTemplate().query(sql, (rs, rn) -> {
	        CustomerManualColVb v = new CustomerManualColVb();
	        v.setCountry(rs.getString("COUNTRY"));
	        v.setLeBook(rs.getString("LE_BOOK"));
	        v.setCustomerId(rs.getString("CUSTOMER_ID"));
	        String col = rs.getString("COLUMN_NAME");
	        v.setColumnName(col);
	        v.setColumnValue(rs.getString("COLUMN_VALUE"));
	        v.setCustModStatusNt(rs.getInt("CUST_MOD_STATUS_NT"));
	        v.setCustModStatus(rs.getInt("CUST_MOD_STATUS"));
	        v.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
	        v.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
	        v.setMaker(rs.getInt("MAKER"));
	        v.setVerifier(rs.getInt("VERIFIER"));
	        v.setDateCreation(rs.getString("DATE_CREATION"));
	        v.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
	        v.setVariableName(CommonUtils.toCamelCase(col));
	        return v;
	    }, key.getCountry(), key.getLeBook(), key.getCustomerId());
	}

	/** Fetch all MANUAL (approved) rows for a customer key. */
	public List<CustomerManualColVb> selectApprByKey(CustomerManualColVb key) {
	    final String sql = """
	        SELECT COUNTRY, LE_BOOK, CUSTOMER_ID, COLUMN_NAME, COLUMN_VALUE,
	               CUST_MOD_STATUS_NT, CUST_MOD_STATUS,
	               RECORD_INDICATOR_NT, RECORD_INDICATOR,
	               MAKER, VERIFIER, DATE_CREATION, DATE_LAST_MODIFIED
	          FROM CUSTOMER_MANUAL
	         WHERE COUNTRY = ?
	           AND LE_BOOK = ?
	           AND CUSTOMER_ID = ?
	        """;
	    return getJdbcTemplate().query(sql, (rs, rn) -> {
	        CustomerManualColVb v = new CustomerManualColVb();
	        v.setCountry(rs.getString("COUNTRY"));
	        v.setLeBook(rs.getString("LE_BOOK"));
	        v.setCustomerId(rs.getString("CUSTOMER_ID"));
	        String col = rs.getString("COLUMN_NAME");
	        v.setColumnName(col);
	        v.setColumnValue(rs.getString("COLUMN_VALUE"));
	        v.setCustModStatusNt(rs.getInt("CUST_MOD_STATUS_NT"));
	        v.setCustModStatus(rs.getInt("CUST_MOD_STATUS"));
	        v.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
	        v.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
	        v.setMaker(rs.getInt("MAKER"));
	        v.setVerifier(rs.getInt("VERIFIER"));
	        v.setDateCreation(rs.getString("DATE_CREATION"));
	        v.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
	        v.setVariableName(CommonUtils.toCamelCase(col));
	        return v;
	    }, key.getCountry(), key.getLeBook(), key.getCustomerId());
	}

	/** Batch insert into MANUAL (approved). Uses your existing single-row insert. */
	public int insertApproved(List<CustomerManualColVb> rows) {
	    if (rows == null || rows.isEmpty()) return Constants.SUCCESSFUL_OPERATION;
	    int total = 0;
	    for (CustomerManualColVb r : rows) {
	        total += doInsertCustomerManual(r, /*flag: main*/ true);
	    }
	    return (total >= rows.size()) ? Constants.SUCCESSFUL_OPERATION : Constants.ERRONEOUS_OPERATION;
	}

	/** Batch insert into HISTORY (uses your existing single-row history insert). */
	public int insertHistory(List<CustomerManualColVb> rows, String opTag) {
	    if (rows == null || rows.isEmpty()) return Constants.SUCCESSFUL_OPERATION;
	    int total = 0;
	    for (CustomerManualColVb r : rows) {
	        // If you want to record opTag, add a column in HIS or ignore it. We just insert the snapshot.
	        total += doInsertCustomerManualHistory(r);
	    }
	    return (total >= rows.size()) ? Constants.SUCCESSFUL_OPERATION : Constants.ERRONEOUS_OPERATION;
	}

	/** Delete ALL approved rows by key (COUNTRY, LE_BOOK, CUSTOMER_ID). */
	public int deleteApprovedByKey(CustomerManualColVb key) {
	    CustomersVb cvb = new CustomersVb();
	    cvb.setCountry(key.getCountry());
	    cvb.setLeBook(key.getLeBook());
	    cvb.setCustomerId(key.getCustomerId());
	    return doDeleteCustomerManual(cvb, /*flag: main*/ true);
	}

	/** Delete ALL pending rows by key (COUNTRY, LE_BOOK, CUSTOMER_ID). */
	public int deletePendByKey(CustomerManualColVb key) {
	    CustomersVb cvb = new CustomersVb();
	    cvb.setCountry(key.getCountry());
	    cvb.setLeBook(key.getLeBook());
	    cvb.setCustomerId(key.getCustomerId());
	    return doDeleteCustomerManual(cvb, /*flag: pend*/ false);
	}

	// === END: Additions for approval flow ===



}
