<%--
 =================================================================
  Licensed Materials - Property of IBM

  WebSphere Commerce

  (C) Copyright IBM Corp. 2012 All Rights Reserved.

  US Government Users Restricted Rights - Use, duplication or
  disclosure restricted by GSA ADP Schedule Contract with
  IBM Corp.
 =================================================================
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://commerce.ibm.com/foundation" prefix="wcf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix = "x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>



<c:set var="responseMap" value="${requestScope['com.ibm.commerce.responseMap']}"/>

<x:parse xml="${responseMap.jenkins}" var="parsedResponse"/>
<x:set var="job" select="$parsedResponse/hudson/job"/>
<object objectType="Jenkins">
	<integrationId readonly="true"><x:out select="$job/url"/></integrationId>
	<jobName readonly="true"><x:out select="$job/name"/></jobName>
	<color readonly="true"><x:out select="$job/color"/></color>
	<lastBuild readonly="true"><x:out select="$job/lastBuild/timestamp"/> - #<x:out select="$job/lastBuild/number"/></lastBuild>
	<lastDuration readonly="true"><x:out select="$job/lastBuild/duration"/></lastDuration>
	<lastResult readonly="true"><x:out select="$job/lastBuild/result"/></lastResult>
	<lastConsoleLog readonly="true"><c:out value="${responseMap.consoleLog}"/></lastConsoleLog>
</object>
	