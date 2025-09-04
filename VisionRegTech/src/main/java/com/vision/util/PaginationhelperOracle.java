/**
 * 
 */
package com.vision.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.vision.vb.MagnifierResultVb;

/**
 * @author kiran-kumar.karra
 *
 */
public class PaginationhelperOracle<E> {

	public static Logger logger = LoggerFactory.getLogger(PaginationhelperOracle.class);

	// Record total
	private long totalRows;
	
	public long getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	private long numPerPage;
	private long currentPage;
	private long totalPages;
	private long startIndex;
	private long lastIndex;
	public long getCurrentPage () { 
		return currentPage; 
	}

	public void setCurrentPage(long currentPage){ 
			this.currentPage = currentPage; 
	}

	public long getNumPerPage(){
		return numPerPage; 
	}

	public void setNumPerPage (long numPerPage){ 
			this.numPerPage = numPerPage; 
	}

	public long getTotalPages (){
		return totalPages; 
	}
	// Calculate the total number of pages 
	public void setTotalPages () {
		if (totalRows% numPerPage == 0) {
			this.totalPages = (totalRows / numPerPage); 
		}else{ 
			this.totalPages = ((totalRows / numPerPage) + 1); 
		}
	}
	public long getStartIndex () {
		return startIndex; 
	} 
	
	public void setStartIndex () {
		this.startIndex = (currentPage -1) * numPerPage;
	} 
	
	public long getLastIndex () {
		return lastIndex; 
	} 
	public void setLastIndex () {
		if (totalRows <numPerPage) {
			this.lastIndex = totalRows;
		} else if ((totalRows% numPerPage == 0) || (totalRows% numPerPage != 0 && currentPage<totalPages)) {
			this.lastIndex  = currentPage * numPerPage; 
		}else if (totalRows% numPerPage != 0 && currentPage == totalPages){// last page 
				this.lastIndex = totalRows;
		}
	}
	/*public List<E> fetchPage(final JdbcTemplate jt, final String sqlFetchRows, final Object args[], final int startIndex,
			final int lastIndex, final RowMapper rowMapper) {
		List<E> result = null;
		// Total number of records
		StringBuffer totalSQL = new StringBuffer("SELECT count (1) FROM (");
		totalSQL.append(sqlFetchRows);
		totalSQL.append(") totalTable");
		if (args == null) {
			setTotalRows(jt.queryForObject(totalSQL.toString(), Integer.class));
		} else {
			setTotalRows(jt.queryForObject(totalSQL.toString(), args, Integer.class));
		}
		if (getTotalRows() <= 0)
			return new ArrayList<E>(0);
		// Oracle database structure paging statement
		StringBuffer paginationSQL = new StringBuffer("SELECT * FROM (");
		paginationSQL.append("SELECT temp.*, ROWNUM num FROM (");
		paginationSQL.append(sqlFetchRows);
		int lastInd =lastIndex+1;
		if(lastInd == 1) {
			lastInd = 20;
		}
		paginationSQL.append(") temp where ROWNUM < " + lastInd);
		paginationSQL.append(") WHERE num >= " + startIndex);
		if (args == null) {
			result = jt.query(paginationSQL.toString(), rowMapper);
		} else {
			result = jt.query(paginationSQL.toString(), args, rowMapper);
		}
		return result;
	}
*/
	/*public List<E> fetchPage( final JdbcTemplate jt, final String sqlFetchRows, 
			final Object args[], final int pageNo, final int pageSize, final RowMapper rowMapper) {
		List<E> result = null;
		// Set several records per page 
		setNumPerPage(pageSize); 
		// Set up to show the number of pages 
		setCurrentPage(pageNo);
		// Total number of records 
		StringBuffer totalSQL = new StringBuffer ( "SELECT count (1) FROM ("); 
		totalSQL.append (sqlFetchRows);
		totalSQL.append ( ") totalTable");
		if (args == null) {
			setTotalRows(jt.queryForObject(totalSQL.toString(), Integer.class));
		} else {
			setTotalRows(jt.queryForObject(totalSQL.toString(), args, Integer.class));
		}
		if(getTotalRows()<=0)
			return new ArrayList<E>(0);
		if(getTotalRows() <= getNumPerPage()){
			setCurrentPage(1);
		}
		// Calculate the total number of pages
		setTotalPages();
		// Number of start-up firms 
		setStartIndex();
		// End of line number 
		setLastIndex();
		// Oracle database structure paging statement 
		StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM (SELECT * FROM (");
		paginationSQL.append (sqlFetchRows); 
		paginationSQL.append ( ") TEMP WHERE NUM <="+lastIndex+") T1 WHERE NUM>"+startIndex+" ORDER BY NUM"); 
		if(args == null){
			result =  jt.query(paginationSQL.toString(),rowMapper);
		}else{
			result = jt.query(paginationSQL.toString(),args,rowMapper);
		}
//		logger.info("Time taken to execute query ["+paginationSQL.toString()+"] is "+ (System.currentTimeMillis()-currentTime)+" ms." );
		return result;
	}*/
	public List<E> fetchPage( final JdbcTemplate jt, final String sqlFetchRows,
			final Object args[], final int pageNo, final int pageSize, final RowMapper rowMapper) {
		List<E> result = null;
		// Set several records per page 
		setNumPerPage(pageSize); 
		// Set up to show the number of pages 
		setCurrentPage(pageNo);
		// Total number of records 
		StringBuffer totalSQL = new StringBuffer ( "SELECT count (1) FROM ("); 
		totalSQL.append (sqlFetchRows); 
		totalSQL.append ( ") totalTable");
		long currentTime = System.currentTimeMillis();
		if (args == null) {
			setTotalRows(jt.queryForObject(totalSQL.toString(), Integer.class));
		} else {
			setTotalRows(jt.queryForObject(totalSQL.toString(), args, Integer.class));
		}
//		logger.info("Time taken to execute query ["+totalSQL.toString()+"] is "+ (System.currentTimeMillis()-currentTime)+" ms." );
		if(getTotalRows()<=0)
			return new ArrayList<E>(0);
		if(getTotalRows() <= getNumPerPage()){
			setCurrentPage(1);
		}
		// Calculate the total number of pages
		setTotalPages();
		// Number of start-up firms 
		setStartIndex();
		// End of line number 
		setLastIndex();
		// Oracle database structure paging statement 
		StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM ("); 
		paginationSQL.append ( "SELECT temp.*, ROWNUM num FROM ("); 
		paginationSQL.append (sqlFetchRows); 
		paginationSQL.append ( ") temp where ROWNUM <=" + lastIndex); 
		paginationSQL.append ( ") WHERE num>" + startIndex);
		currentTime = System.currentTimeMillis();
		if(args == null){
			result =  jt.query(paginationSQL.toString(),rowMapper);
		}else{
			result = jt.query(paginationSQL.toString(),args,rowMapper);
		}
//		logger.info("Time taken to execute query ["+paginationSQL.toString()+"] is "+ (System.currentTimeMillis()-currentTime)+" ms." );
		return result;
	}
	public List<E> fetchPage(final JdbcTemplate jt, final String sqlFetchRows, final Object args[], final int startIndex,
			final int lastIndex, final Long totalRows, final RowMapper rowMapper) {

		List<E> result = null;
		// Total number of records
		setTotalRows(totalRows);

		// Oracle database structure paging statement
		StringBuffer paginationSQL = new StringBuffer("SELECT * FROM (");
		paginationSQL.append("SELECT temp.*, ROWNUM num FROM (");
		paginationSQL.append(sqlFetchRows);
		int lastInd =lastIndex+1;
		paginationSQL.append(") temp where ROWNUM <= " + lastInd);
		paginationSQL.append(") WHERE num >= " + startIndex);
		long currentTime = System.currentTimeMillis();
		if (args == null) {
			result = jt.query(paginationSQL.toString(), rowMapper);
		} else {
			result = jt.query(paginationSQL.toString(), args, rowMapper);
		}
		// logger.info("Time taken to execute query ["+paginationSQL.toString()+"] is "+
		// (System.currentTimeMillis()-currentTime)+" ms." );
		return result;
	}
	public String reportFetchPage(String sqlPaginationQuery,final int pageNo, final int recordsPerPage,final int totalRows) {
		setNumPerPage(recordsPerPage);
		setCurrentPage(pageNo);
		setTotalRows(totalRows);
		// Calculate the total number of pages
		setTotalPages();
		// Number of start-up firms 
		setStartIndex();
		// End of line number 
		setLastIndex();
		
		// Oracle database structure paging statement 
		StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM (SELECT * FROM (");
		paginationSQL.append (sqlPaginationQuery); 
		paginationSQL.append ( ") TEMP WHERE NUM <="+lastIndex+") T1 WHERE NUM>"+startIndex+" ORDER BY NUM"); 
		return paginationSQL.toString();
	}
	
	@SuppressWarnings("null")
	public List<MagnifierResultVb> fetchPageSpecificConnection( final Connection connection , final String sqlFetchRows,
			final Object args[], final int pageNo, final long pageSize) {
		List<MagnifierResultVb> result = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			// Set several records per page 
			setNumPerPage(pageSize); 
			// Set up to show the number of pages 
			setCurrentPage(pageNo);
			// Total number of records 
			StringBuffer totalSQL = new StringBuffer ( "SELECT count (1) COUNT FROM ("); 
			totalSQL.append (sqlFetchRows); 
			totalSQL.append ( ") totalTable");
			stmt = connection.createStatement();
			long currentTime = System.currentTimeMillis();
			if (args == null) {
	//			setTotalRows(jt.queryForObject(totalSQL.toString(), Integer.class));
				rs = stmt.executeQuery(totalSQL.toString());
				setTotalRows(rs.getInt(1));
			} else {
	//			setTotalRows(jt.queryForObject(totalSQL.toString(), args, Integer.class));
				if(stmt != null)
				rs = stmt.executeQuery(totalSQL.toString());
				while(rs.next()) {
					setTotalRows(Long.parseLong(rs.getString("COUNT")));
				}
			}
			if(stmt != null)
				stmt.close();
			if(rs!= null)
				rs.close();	
	//		logger.info("Time taken to execute query ["+totalSQL.toString()+"] is "+ (System.currentTimeMillis()-currentTime)+" ms." );
			if(getTotalRows()<=0)
				return new ArrayList<MagnifierResultVb>(0);
			if(getTotalRows() <= getNumPerPage()){
				setCurrentPage(1);
			}
			// Calculate the total number of pages
			setTotalPages();
			// Number of start-up firms 
			setStartIndex();
			// End of line number 
			setLastIndex();
			// Oracle database structure paging statement 
			StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM ("); 
			paginationSQL.append ( "SELECT temp.*, ROWNUM num FROM ("); 
			paginationSQL.append (sqlFetchRows); 
			paginationSQL.append ( ") temp where ROWNUM <=" + lastIndex); 
			paginationSQL.append ( ") WHERE num>" + startIndex);
			currentTime = System.currentTimeMillis();
			
			if(args == null){
	//			result =  jt.query(paginationSQL.toString(),rowMapper);
				ps = connection.prepareStatement(paginationSQL.toString());
				for(int i=1;i<=args.length;i++){
					ps.setObject(i,args[i-1]);
				}
				rs = ps.executeQuery();
				
			}else{
				ps = connection.prepareStatement(paginationSQL.toString());
				rs = ps.executeQuery();
			}
			result = new ArrayList<MagnifierResultVb>();
			while(rs.next()) {
				MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
				magnifierResultVb.setColumnNine(rs.getString("ID"));
				magnifierResultVb.setColumnTen(rs.getString("DESCRIPTION"));
				magnifierResultVb.setMagnifierResult(magnifierResultVb.getColumnNine()+","+magnifierResultVb.getColumnTen());
				result.add(magnifierResultVb);
			}
		}catch(SQLException ex) {
			
		}finally {
			try {
				if(rs !=null)
					rs.close();
				if(ps !=null)
					ps.close();
			} catch (SQLException e) {
			}
		}
		return result;
	}
	public List<MagnifierResultVb> fetchPageSpecificConnection(final Connection connection , final String sqlFetchRows, final Object args[], final int startIndex,
			final int lastIndex, final Long totalRows) {
		List<MagnifierResultVb> result = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
		// Total number of records
		setTotalRows(totalRows);

		// Oracle database structure paging statement
		StringBuffer paginationSQL = new StringBuffer("SELECT * FROM (");
		paginationSQL.append("SELECT temp.*, ROWNUM num FROM (");
		paginationSQL.append(sqlFetchRows);
		int lastInd =lastIndex+1;
		paginationSQL.append(") temp where ROWNUM <= " + lastInd);
		paginationSQL.append(") WHERE num >= " + startIndex);
		long currentTime = System.currentTimeMillis();
		if(args == null){
//			result =  jt.query(paginationSQL.toString(),rowMapper);
			ps = connection.prepareStatement(paginationSQL.toString());
			for(int i=1;i<=args.length;i++){
				ps.setObject(i,args[i-1]);
			}
			rs = ps.executeQuery();
			
		}else{
			ps = connection.prepareStatement(paginationSQL.toString());
			rs = ps.executeQuery();
		}
		result = new ArrayList<MagnifierResultVb>();
		while(rs.next()) {
			MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
			magnifierResultVb.setColumnNine(rs.getString("ID"));
			magnifierResultVb.setColumnTen(rs.getString("DESCRIPTION"));
			magnifierResultVb.setMagnifierResult(magnifierResultVb.getColumnNine()+","+magnifierResultVb.getColumnTen());
			result.add(magnifierResultVb);
		}
		// logger.info("Time taken to execute query ["+paginationSQL.toString()+"] is "+
		// (System.currentTimeMillis()-currentTime)+" ms." );
		}catch(SQLException ex) {
					
				}finally {
					try {
						rs.close();
						ps.close();
					} catch (SQLException e) {
					}
				}
				return result;
			}
}