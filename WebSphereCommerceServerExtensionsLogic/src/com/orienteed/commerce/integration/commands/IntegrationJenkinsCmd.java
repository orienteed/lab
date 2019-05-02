package com.orienteed.commerce.integration.commands;

import com.ibm.commerce.command.ControllerCommand;

public interface IntegrationJenkinsCmd extends ControllerCommand {

    public static final String NAME = IntegrationJenkinsCmd.class.getName();
    public static final String defaultCommandClassName = NAME + "Impl";
    
    public void setUser(String user);
    
    public void setPassword(String password);
    
    public void setHostName(String hostName);
    
    public void setPort(String port);
    
    public void setEndpoint(String endpoint);
    
    public void setFunction(String function);
    
    public void setJobName(String jobName);
    
    public String getResponse();

}
