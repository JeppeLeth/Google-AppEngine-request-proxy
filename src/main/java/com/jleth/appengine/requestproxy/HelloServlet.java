/**
 * Copyright 2017 Jeppe Leth.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jleth.appengine.requestproxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    Enumeration headerNames = request.getHeaderNames();
    while(headerNames.hasMoreElements()) {
      String headerName = (String)headerNames.nextElement();
      out.println("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
    }
    Enumeration params = request.getParameterNames();
    while(params.hasMoreElements()){
      String paramName = (String)params.nextElement();
      out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
    }
    String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    if (ipAddress == null) {
      ipAddress = request.getRemoteAddr();
    }
    out.println("IP address = "+ipAddress);
    String pathInfo = request.getPathInfo();
    out.println("Path info = "+pathInfo);

    out.println("ContextPath: "+request.getContextPath());
    out.println("LocalAddr: "+request.getLocalAddr());
    out.println("LocalName: "+request.getLocalName());
    out.println("LocalPort: "+request.getLocalPort());
    out.println("Method: "+request.getMethod());
    out.println("PathInfo: "+request.getPathInfo());
    out.println("Protocol: "+request.getProtocol());
    out.println("QueryString: "+request.getQueryString());
    out.println("RequestedSessionId: "+request.getRequestedSessionId());
    out.println("RequestURI: "+request.getRequestURI());
    out.println("RequestURL: "+request.getRequestURL());
    out.println("Scheme: "+request.getScheme());
    out.println("ServerName: "+request.getServerName());
    out.println("ServerPort: "+request.getServerPort());
    out.println("ServletPath: "+request.getServletPath());

    out.println("Hello, world");
  }
}
// [END example]
