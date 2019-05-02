# Management Center - Jenkins integration
The goal with this project is to create a new custom tool for IBM WebSphere Commerece - Management Center. The tool will be able to request information from Jenkins and to run builds for jobs. We implement a custom controller command to communicate with Jenkins. We have implemented two ways to initiate this command. One way is to go through the Management Center user interface. This is the usual way to execute the command. The other way, which can be useful while developing, is to use execute it by way of a REST request. If you do not need REST capabilites for the command, you can ignore classes that have explicitly to do with making a REST call.

# Get started
To use the tool, import the repository files into your workspace. In order to execute the Jenkins command as a REST service, we need an access policy in place. Copy the files in DataLoad/xml, to a similar location in the xml folder outside of the workspace (where the rest of the access policies are located). Then load it to the database, by using the acpload.bat script.

# Configurations
The tool reads configurations from the STORECONF database table:
'integration.jenkins.hostName',
'integration.jenkins.port',
'integration.jenkins.username',
'integration.jenkins.password'
To insert data into the STORECONF table, use a command similar to
INSERT INTO WCS.STORECONF (STOREENT_ID, NAME, VALUE, OPTCOUNTER) VALUES(0, 'integration.jenkins.hostName', 'ThisIsMyHostName', 1);
where, the string 'ThisIsMyHostName' is the host name of the Jenkins server.