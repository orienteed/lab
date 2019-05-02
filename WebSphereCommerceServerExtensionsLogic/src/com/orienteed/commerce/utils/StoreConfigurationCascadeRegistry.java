package com.orienteed.commerce.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.commerce.foundation.internal.server.services.registry.StoreConfigurationRegistry;

public class StoreConfigurationCascadeRegistry {
	
	public final static String CLASSNAME = StoreConfigurationCascadeRegistry.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASSNAME);
	
	protected static String PARENT_STOREID = "PARENT_STOREID";
	protected static int BASE_STOREID = 0;
	
	public static String findByStoreIdandName(Integer storeId, String name) {
		String METHODNAME = "findByStoreIdLangIdandName";
		LOGGER.entering(CLASSNAME, METHODNAME);
		String result = null;
		try {
			StoreConfigurationRegistry storeConfReg=new StoreConfigurationRegistry().getSingleton();
			
			result = storeConfReg.getValue(storeId, name);
			if (storeId == null) {
				LOGGER.logp(Level.WARNING, CLASSNAME, METHODNAME, "No result found for " + name + " at all.");
			}
			else if(result != null) {
				LOGGER.logp(Level.FINER, CLASSNAME, METHODNAME, "Result found for " + name + ": " + result + " at storeId: " + storeId + ".");
			}
			else { //item not found, recursive case
				LOGGER.logp(Level.FINEST, CLASSNAME, METHODNAME, "No result found for " + name + " at storeId: " + storeId + ".");
				if (storeId != 0) {
					Integer parentStoreId = getParentStoreId(storeId);
					result = findByStoreIdandName(parentStoreId, name);
				}
			}
		} catch(Exception e) {
			LOGGER.logp(Level.WARNING, CLASSNAME, METHODNAME, "Exception occurred while trying to find registry value", e);
		}
		
		LOGGER.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	protected static Integer getParentStoreId(Integer storeId) throws Exception {
		String METHODNAME = "getParentStoreId";
		LOGGER.entering(CLASSNAME, METHODNAME);
		
		CommonHelper commonHelper = new CommonHelper();
		Long parentStoreIdString = commonHelper.findGenericStore(new Long(storeId));
				
		Integer parentStoreId = BASE_STOREID;
		if(parentStoreIdString != null) { //this storeId has a parent storeId, let's use that
			try {
				parentStoreId = Integer.parseInt(parentStoreIdString.toString());
				LOGGER.logp(Level.FINE, CLASSNAME, METHODNAME, "Result found for PARENT_STOREID: " + parentStoreIdString);
			}
			catch(NumberFormatException e) {
				throw new Exception("Could not convert value '" + parentStoreIdString + "' found in XSTORECONF to Integer.", e);
			}
		}
		
		LOGGER.exiting(CLASSNAME, METHODNAME);
		return parentStoreId;
	}
	
}
