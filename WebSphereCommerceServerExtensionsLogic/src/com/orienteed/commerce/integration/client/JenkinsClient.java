package com.orienteed.commerce.integration.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.commerce.foundation.common.util.logging.LoggingHelper;
import com.orienteed.commerce.integration.parse.ExtractCrumb;


public class JenkinsClient {
	
	private static final String CLASSNAME = JenkinsClient.class.getName();
	private static final Logger LOGGER = LoggingHelper.getLogger(JenkinsClient.class);

	private final HttpClient client;
    
    private final HttpHost targetHost;
    private final HttpClientContext context;
    
	private final String USER_AGENT = "Mozilla/5.0";

	private final String JOBLIST_ENDPOINT = "/api/xml?tree=jobs[name,lastBuild[timestamp,number,result,duration,result]"
			+ ",url,lastSuccessfulBuild,lastFailedBuild,color,healthReport[score]]";
	private final String JOB_XPATH = "&xpath=/hudson/job[name='%s']&wrapper=hudson";
	private final String BUILD_ENDPOINT = "/job/%s/build";
	private final String CONSOLE_ENDPOINT = "/job/%s/lastBuild/consoleText";
	private final String CRUMB_ENDPOINT = "/crumbIssuer/api/xml";
    
    /**
     * Construct a Jenkins Client that can be used to access a 
     * RESTful Jenkins API
     * 
     * @param hostName for the Jenkins server
     * @param port number for accessing Jenkins
     * @param user on the Jenkins server
     * @param password for the user on Jenkins
     */
    public JenkinsClient(String hostName, Integer port, String user, String password) {
    	targetHost = new HttpHost(hostName, port, "http");
    	
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
		        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
		        new UsernamePasswordCredentials(user, password));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);
	        
		client = HttpClientBuilder.create()
		          .addInterceptorLast(new HttpRequestInterceptor() {
		        	  @Override
		        	  public void process(HttpRequest request, HttpContext context)
		        		  throws HttpException, IOException {
		        		LOGGER.info("Processing: " + request);
		        	  }
		        	})
		          .build();
    }
    
    /**
     * Execute request against the Jenkins API.
     * 
     * @param request Http request to execute against Jenkins API
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse execute(HttpRequest request) throws ClientProtocolException, IOException {
    	return client.execute(targetHost, request, context);
    }
    

    /**
     * Execute a request against the configured Jenkins server against
     * the provided endpoint. It will be attempted to add a crumb to the
     * request header
     * 
     * @param endpoint of the request
     * @return
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws TransformerConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     */
    public HttpResponse execute(String endpoint) throws ClientProtocolException, IllegalStateException, 
    		TransformerConfigurationException, IOException, SAXException, TransformerException,
    		TransformerFactoryConfigurationError   {
    	
		final String METHODNAME = "execute()";
		LOGGER.entering(CLASSNAME, METHODNAME);

    	HttpPost postRequest = new HttpPost(endpoint);
		postRequest.addHeader("User-Agent", USER_AGENT);
		postRequest = (HttpPost) addCrumb(postRequest);

		LOGGER.info(postRequest.toString());
		HttpResponse response;
		
		response = client.execute(targetHost, postRequest, context);
		
		LOGGER.exiting(CLASSNAME, METHODNAME);
		return response;
    }
    
    /*
     * Get crumb from Jenkins and add to request header. 
     */
    private HttpRequest addCrumb(HttpRequest request) throws ClientProtocolException, 
    		IOException, IllegalStateException, TransformerConfigurationException, SAXException, 
    		TransformerException, TransformerFactoryConfigurationError {
    	
		HttpGet getRequest = new HttpGet(CRUMB_ENDPOINT);
		HttpResponse response = this.execute(getRequest);
		if (200 == response.getStatusLine().getStatusCode()) {
    		String[] crumbInfo = this.getCrumbFromResponse(response);
    		String crumb = crumbInfo[0];
    		String crumbField = crumbInfo[1];
        	request.addHeader(crumbField, crumb);
		}
    	return request;
    }

    /*
     * Request crumb from the Jenkins API and return a String array
     * that contains the value of the crumb and the field name for 
     * the header entry.
     */
    private String[] getCrumbFromResponse(HttpResponse response) throws SAXException, 
    		IllegalStateException, IOException, TransformerConfigurationException,
    		TransformerException, TransformerFactoryConfigurationError {
    	
		ExtractCrumb parser = new ExtractCrumb();
		LOGGER.info("Getting crumb from Jenkins response: ");
        OutputStream output = new ByteArrayOutputStream();
        Source src = new SAXSource(parser, new InputSource(response.getEntity().getContent()));
        Result res = new StreamResult(output);
        TransformerFactory.newInstance().newTransformer().transform(src, res);
        LOGGER.info(parser.crumb);
        String[] crumbInfo = { parser.crumb, parser.crumbField };
        
        return crumbInfo;
	}

    
	/*
	 * Set the value for the Jenkins endpoint depending on the functionality
	 * we want to execute.
	 */
	public String configureEndpoint(FunctionType function, String jobName) {
		final String METHODNAME = "configureEndpoint()";
		String endpoint = null;
		if (FunctionType.JOBLIST.equals(function)) {
			endpoint = JOBLIST_ENDPOINT;
		} else if (FunctionType.REFRESH.equals(function)) {
			if (jobName != null) {
				endpoint = JOBLIST_ENDPOINT + String.format(JOB_XPATH, jobName);
			}
		} else if (FunctionType.BUILD.equals(function)) {
			if (jobName != null) {
				endpoint = String.format(BUILD_ENDPOINT, jobName);
			}
		} else if (FunctionType.CONSOLE.equals(function)) {
			if (jobName != null) {
				endpoint = String.format(CONSOLE_ENDPOINT, jobName);
			}
		}

		if (endpoint == null) {
			throw new RuntimeException("Endpoint is not correctly set. Job name must be provided");
		}
		return endpoint;
	}
	
	/**
	 * Close the current client and all connections.
	 */
	public void close() {
		HttpClientUtils.closeQuietly(client);
	}

	/**
	 * The different configurations available for the Jenkins client
	 */
	public enum FunctionType {
		JOBLIST,
		REFRESH,
		CONSOLE,
		BUILD
	}
}
