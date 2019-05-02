package com.orienteed.commerce.rest.integration.handler;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.ibm.commerce.foundation.common.util.logging.LoggingHelper;
import com.ibm.commerce.rest.classic.core.AbstractConfigBasedClassicHandler;
import javax.ws.rs.core.MediaType;

/**
 * My resource.
 */
@Path("store/{storeId}/integration")
public class IntegrationResourceHandler extends AbstractConfigBasedClassicHandler {

	private static final String CLASSNAME = IntegrationResourceHandler.class.getName();
	private static final Logger LOGGER = LoggingHelper
			.getLogger(IntegrationResourceHandler.class);

    private static final String RESOURCE_NAME = "integration";
    private static final String COMMAND_INTERFACE_JENKINS_JOBS 
        = "com.orienteed.commerce.integration.commands.IntegrationJenkinsCmd";
    private static final String COMMAND_INTERFACE_JENKINS_RUN_JOB 
    	= "com.orienteed.commerce.integration.commands.RunJenkinsJobCmd";
	private static final String PROFILE_NAME_MY_COMMAND_SUMMARY = "Orienteed_Integration";

	@Override
	public String getResourceName() {
		return RESOURCE_NAME;
	}
    
	/**
	 * REST handler for the Jenkins integration command
	 * @param storeId 
	 * @param responseFormat 
	 * @param user of Jenkins user
	 * @param password of Jenkins user
	 * @param hostName of Jenkins
	 * @param port of Jenkis
	 * @param endpoint of Jenkins
	 * @param function that the integration tool will execute against Jenkins
	 * @return The response from Jenkins.
	 */
	@POST
	@Path("jobs")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_ATOM_XML })
	public Response handleJobsRequest(
			@PathParam(value = "storeId") String storeId,
			@QueryParam(value = "responseFormat") String responseFormat,
			@QueryParam(value = "user") String user,
			@QueryParam(value = "password") String password,
			@QueryParam(value = "hostName") String hostName,
			@QueryParam(value = "port") String port,
			@QueryParam(value = "endpoint") String endpoint,
			@QueryParam(value = "jobName") String jobName,
			@QueryParam(value = "function") String function			
			) {
		// Set up trace facilities
		final String METHODNAME = "handleJobsRequest";
		// Log method start
		final boolean traceEnabled = LoggingHelper.isTraceEnabled(LOGGER);
		final boolean entryExitTraceEnabled = LoggingHelper
				.isEntryExitTraceEnabled(LOGGER);

		if (entryExitTraceEnabled) {
			Object[] objArr = new Object[] { storeId, responseFormat, user,
					port, endpoint, jobName, function};
			LOGGER.entering(CLASSNAME, METHODNAME, objArr);
		}

        HashMap params = new HashMap();
		params.put("user", user);
		params.put("password", password);
		params.put("hostName", hostName);
		params.put("port", port);
		params.put("endpoint", endpoint);
		params.put("jobName", jobName);
		params.put("function", function);

		/**
		 * Use the configuration-based REST API to automatically fill in input
		 * values, execute, and build the response.
		 */
		
		Response result = executeConfigBasedCommandWithContext(
				COMMAND_INTERFACE_JENKINS_JOBS,
				PROFILE_NAME_MY_COMMAND_SUMMARY, responseFormat, storeId, params);
		
		// Log method exit
		if (entryExitTraceEnabled) {
			LOGGER.exiting(CLASSNAME, METHODNAME, result);
		}

		return result;
	}

}
