![Jenkins Integration Tool](https://raw.githubusercontent.com/orienteed/wcs-to-jenkins/assets/images/WCS-Jenkins.png)
# Management Center - Jenkins Integration Tool
The goal with this project is to create a new custom tool for IBM WebSphere Commerece - Management Center. The tool will be able to request information from Jenkins and to run builds for jobs. We implement a custom controller command to communicate with Jenkins. We have implemented two ways to initiate this command. One way is to go through the Management Center user interface. This is the usual way to execute the command. The other way, which can be useful while developing, is to use execute it by way of a REST request. If you do not need REST capabilites for the command, you can ignore classes that have explicitly to do with making a REST call.

## Get started
To use the tool, import the repository files (or relevant parts of them) into your workspace. 
* If you have existing customizations in your workspace, take care to not overwrite them, and only copy relevant sections of the following files:
    - `LOBTools/WebContent/WEB-INF/spring-extension.xml`
    - `LOBTools/WebContent/WEB-INF/src/xml/commerce/shell/ApplicationMenuItems.xml`
    - `Rest/WebContent/WEB-INF/config/resources-ext.properties`
* In order to execute the Jenkins command as a REST service, we need an access policy in place. Copy the files in DataLoad/xml, to a similar location in the xml folder outside of the workspace (where the rest of the access policies are located). Then load it to the database, by using the acpload.bat script.

## Configurations
The tool reads configurations from the `STORECONF` database table:
```sql
'integration.jenkins.hostName',
'integration.jenkins.port',
'integration.jenkins.username',
'integration.jenkins.password'
```

To insert data into the `STORECONF` table, use commands similar to
```sql
INSERT INTO WCS.STORECONF (STOREENT_ID, NAME, VALUE, OPTCOUNTER) VALUES(0, 'integration.jenkins.hostName', 'ThisIsMyHostName', 1);
```
where, the string `ThisIsMyHostName` is the host name of the Jenkins server.

## Usage
Open Management Center and select the Integration Tool from the Menu.
![Open Integration Tool](https://raw.githubusercontent.com/orienteed/wcs-to-jenkins/assets/images/integrationToolMenu.jpg)
Select the Jenkins object in the Explorer view.
![Select Object](https://raw.githubusercontent.com/orienteed/wcs-to-jenkins/assets/images/integrationToolNoSelection.jpg)
This will display a list of all the available jobs on the Jenkins server, along with some basic information about it. Select a job to see the detailed properites view.
![View Jenkins Job List](https://raw.githubusercontent.com/orienteed/wcs-to-jenkins/assets/images/integrationToolJobList.jpg)
When an object is selected we can execute a build of the job by clicking on the Jenkins build button. We can also refresh the information by clicking on the refresh button in the toolbar. Note that in order to view the console log information the job must be manually refreshed. 
![View Jenkins Job Properties](https://raw.githubusercontent.com/orienteed/wcs-to-jenkins/assets/images/integrationToolJobProperties.jpg)