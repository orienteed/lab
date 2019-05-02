package com.orienteed.commerce.integration.client;

public class IntegrationClientFactory {
	
	private final String HOSTNAME;
	private final Integer PORT;
	private final String USER;
	private final String PASSWORD;
	
	private JenkinsClient jenkinsClient;
	
	public IntegrationClientFactory(String hostName, Integer port, String user, String password) {
		this.HOSTNAME = hostName;
		this.PORT = port;
		this.USER = user;
		this.PASSWORD = password;
	}
	
	public IntegrationClientFactory(String hostName, Integer port) {
		this(hostName, port, "user", "password");
	}
	
	public JenkinsClient getJenkinsClient() {
		if (jenkinsClient == null) {
			jenkinsClient = new JenkinsClient(HOSTNAME, PORT, USER, PASSWORD);
		}
		return jenkinsClient;
	}

}
