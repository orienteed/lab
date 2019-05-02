package com.orienteed.commerce.integration.commands;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.ibm.commerce.command.ControllerCommandImpl;
import com.ibm.commerce.datatype.TypedProperty;
import com.ibm.commerce.exception.ECApplicationException;
import com.ibm.commerce.exception.ECException;
import com.ibm.commerce.foundation.common.util.logging.LoggingHelper;
import com.ibm.commerce.ras.ECMessage;
import com.orienteed.commerce.integration.client.IntegrationClientFactory;
import com.orienteed.commerce.integration.client.JenkinsClient;
import com.orienteed.commerce.integration.client.JenkinsClient.FunctionType;
import com.orienteed.commerce.integration.parse.ParseJoblistResponse;
import com.orienteed.commerce.utils.StoreConfigurationCascadeRegistry;

public  class IntegrationJenkinsCmdImpl extends ControllerCommandImpl
implements IntegrationJenkinsCmd {

	private static final String CLASSNAME = IntegrationJenkinsCmdImpl.class.getName();
	private static final Logger LOGGER = LoggingHelper.getLogger(IntegrationJenkinsCmdImpl.class);

	// Variables configured externally (database, REST or Spring param)
	private String user;
	private String password;
	private String hostName;
	private String port;
	private String function; 

	private String endpoint; // Can be configured by REST call
	private String jobName; // Optional param not needed for all functions

	// Variables
	private HttpResponse response;
	private FunctionType functionType;
	
	// Constants
	private final String JENKINS = "jenkins";
	private final String CONSOLE_LOG = "consoleLog";


	/*
	 * (non-Javadoc)
	 * This function is executed when this command is executed as a WebSphere Commerce command
	 * as opposed to a REST call, which will use the setters.
	 */
	@Override
	public void setRequestProperties(TypedProperty reqProperties) throws ECException {

		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		final String METHODNAME = "setRequestProperties()";
		LOGGER.entering(CLASSNAME, METHODNAME);

		function = reqProperties.getString("function");
		Integer storeId= reqProperties.getInteger("storeId", 0);
		jobName = reqProperties.getString("jobName", null);
		LOGGER.info("Getting configs from database...");
		user = StoreConfigurationCascadeRegistry.findByStoreIdandName(storeId, "integration.jenkins.username");
		password = StoreConfigurationCascadeRegistry.findByStoreIdandName(storeId, "integration.jenkins.password");
		hostName = StoreConfigurationCascadeRegistry.findByStoreIdandName(storeId, "integration.jenkins.hostName");
		port = StoreConfigurationCascadeRegistry.findByStoreIdandName(storeId, "integration.jenkins.port");
		LOGGER.info("Done");

		super.setRequestProperties(reqProperties);
		LOGGER.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void performExecute() throws ECException {

		final String METHODNAME = "performExecute()";
		LOGGER.entering(CLASSNAME, METHODNAME);

		determineFunctionType();
		
		IntegrationClientFactory clientFactory = new IntegrationClientFactory(
				hostName,
				Integer.parseInt(port),
				user,
				password);
		JenkinsClient client = clientFactory.getJenkinsClient();
		
		TypedProperty resp = new TypedProperty();
		String parsedResponse = "NOT SET";
		
		try {
			if (endpoint == null) {
				endpoint = client.configureEndpoint(functionType, jobName);
			}
			LOGGER.info("Endpoint is: " + endpoint);
			
			response = client.execute(endpoint);
			if (FunctionType.JOBLIST.equals(functionType) ){
				checkStatusCode(200, response);
				parsedResponse = parseJoblistResponse(response);
			} else if (FunctionType.REFRESH.equals(functionType)) {
				checkStatusCode(200, response);
				parsedResponse = parseJoblistResponse(response);

				// Get console log
				endpoint = client.configureEndpoint(FunctionType.CONSOLE, jobName);
				response = client.execute(endpoint);
				String consoleLog = "***** NOT SET ******";
				try {
					checkStatusCode(200,response);
					consoleLog = parseResponse(response);
				} catch (IOException e) {
					consoleLog = "***** Problem parsing console log *****";
				} catch (RuntimeException e) {
					consoleLog = "***** Could not find any console log *****";
				} catch (Exception e) {
					consoleLog =  "***** Unexpected issue loading console log ******";
				}
				resp.put(CONSOLE_LOG, consoleLog);

			} else if (FunctionType.BUILD.equals(functionType)) {
				checkStatusCode(201, response);
				parsedResponse = "<object objectType='Jenkins'/>";
			} else {
				throw new RuntimeException(String.format("FunctionType '%s' is not implemented.", function));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ECApplicationException(ECMessage._ERR_GENERIC, CLASSNAME, METHODNAME, 
					new Object[] {e.toString() + " " + e.getStackTrace()[0]}, true);
		} finally {
			client.close();
		}

		resp.put(JENKINS, parsedResponse);
		setResponseProperties(resp);

		LOGGER.exiting(CLASSNAME, METHODNAME);
	}

	/*
	 * Parse HttpResponse for logging purposes
	 */
	private String parseResponse(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
			result.append(System.lineSeparator());
		}
		return result.toString();
	}


	/*
	 * Check that status code is as expected
	 */
	private void checkStatusCode(int expected, HttpResponse response) throws IOException {
		if ( expected != response.getStatusLine().getStatusCode() ) {
			LOGGER.severe("Jenkins response: " + parseResponse(response));
			throw new RuntimeException("Jenkins request did not succeed: " + response.toString());
		}
	}

	/*
	 * Parse HttpResponse when expecting a list of jobs from Jenkins
	 */
	private String parseJoblistResponse(HttpResponse response)
			throws IllegalStateException, IOException, TransformerFactoryConfigurationError,
			SAXException, TransformerException {

		final String METHODNAME = "parseJoblistResponse()";
		LOGGER.entering(CLASSNAME, METHODNAME);

		ParseJoblistResponse parser = new ParseJoblistResponse();
		LOGGER.info("Parsing XML response...");
		OutputStream output = new ByteArrayOutputStream();
		Source src = new SAXSource(parser, new InputSource(response.getEntity().getContent()));
		Result res = new StreamResult(output);
		TransformerFactory.newInstance().newTransformer().transform(src, res);
		LOGGER.info("Parsing done");
		String returnValue = output.toString();
		LOGGER.info("POST response: " + returnValue);

		LOGGER.exiting(CLASSNAME, METHODNAME);

		return returnValue;
	}

	/*
	 * Map input function string to a FunctionType object
	 */
	private void determineFunctionType() throws ECApplicationException {
		final String METHODNAME = "determineFunctionType()";
		if (FunctionType.JOBLIST.toString().equals(function.toUpperCase())) {
			functionType = FunctionType.JOBLIST;
		} else if (FunctionType.BUILD.toString().equals(function.toUpperCase())) {
			functionType = FunctionType.BUILD;
		} else if (FunctionType.CONSOLE.toString().equals(function.toUpperCase())) {
			functionType = FunctionType.CONSOLE;
		} else if (FunctionType.REFRESH.toString().equals(function.toUpperCase())){
			functionType = FunctionType.REFRESH;
		} else {
			throw new RuntimeException(String.format("Parameter 'function' has an invalid value of '%s'", function));
		}
	}


	@Override
	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void setFunction(String function) {
		this.function = function;
	}
	
	@Override
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String getResponse() {
		return response.toString();
	}
}

