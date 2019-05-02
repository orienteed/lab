package com.orienteed.commerce.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ejb.SessionContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ibm.commerce.base.helpers.BaseJDBCHelper;

public class CommonHelper extends BaseJDBCHelper {

	private static final String CLASSNAME = CommonHelper.class.getName();
	private static final Logger logger = Logger.getLogger(CLASSNAME);
	
	private final static String QUERY_GENERIC_STORE = "select RELATEDSTORE_ID from storerel where STATE = 1 AND STORE_ID = ?  and STRELTYP_ID = - 5 and RELATEDSTORE_ID<> ?";
    private final static String CONTENT_RELATED_STORE_SQL = "select STORE_ID from STOREREL where RELATEDSTORE_ID = ? and STRELTYP_ID = (select STRELTYP_ID from STRELTYP where name = ?)";

	public SessionContext getSessionContext() {
		return null;
	}

	/**
	 * Recover the generic store of a specific store.
	 * Returns the StoreId of StorefrontAssetStore
	 * 
	 * @param storeId that we want to find generic storeId for
	 * @return 
	 * @throws NamingException
	 * @throws SQLException
	 */
	public Long findGenericStore(Long storeId) throws NamingException, SQLException {
		String METHODNAME = "findGenericStore";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Long genericStoreId = null;
		
		try {
			
			makeConnection();
			
			String sql = QUERY_GENERIC_STORE;			
				
			pstmt = this.getPreparedStatement(sql);
			pstmt.setLong(1, storeId.longValue());
			pstmt.setLong(2, storeId.longValue());
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {			
				genericStoreId = rs.getLong("RELATEDSTORE_ID");
			}
			
				
		} finally {
			if (rs != null) try{ rs.close(); } catch(Exception ex){logger.error(METHODNAME + "ERROR when try to close the connection. ",ex);} 
			if (pstmt != null) try{ pstmt.close(); } catch(Exception ex){logger.error(METHODNAME + "ERROR when try to close the connection. ",ex);}
			try {closeConnection(); } catch (SQLException ex) {logger.error(METHODNAME + "ERROR when try to close the connection ",ex);}		 
			
		}
		
		return genericStoreId;
	}
	
	/**
	 * Find child related stores.
	 * 
	 * @param storeId
	 * @param relationTypeName
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public ArrayList<Long> findChildStores(Long storeId, String relationTypeName) throws NamingException, SQLException {
		String METHODNAME = "findChildStores";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<Long> relatedStores = new ArrayList<Long>();
		
		try {
			makeConnection();
			
			String sql = CONTENT_RELATED_STORE_SQL;			
				
			pstmt = this.getPreparedStatement(sql);
			pstmt.setLong(1, storeId.longValue());
			pstmt.setString(2, relationTypeName);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {	
				relatedStores.add(new Long(rs.getLong("STORE_ID")));
			}
		} finally {
			if (rs != null) try{ rs.close(); } catch(Exception ex){logger.error(METHODNAME + "ERROR when try to close the connection. ",ex);} 
			if (pstmt != null) try{ pstmt.close(); } catch(Exception ex){logger.error(METHODNAME + "ERROR when try to close the connection. ",ex);}
			try {closeConnection(); } catch (SQLException ex) {logger.error(METHODNAME + "ERROR when try to close the connection ",ex);}		 
			
		}
		
		return relatedStores;
	}
	
	


}