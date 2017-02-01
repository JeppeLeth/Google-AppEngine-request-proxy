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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple HTTP/HTTPS proxy for Google AppEngine.
 * Add your
 *
 * @author JeppeLeth
 */
@SuppressWarnings("serial")
public class ForwardServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ForwardServlet.class.getName());

    private static final boolean ALLOW_ONLY_WHITELISTED = true;
    private static final List<String> WHITE_LIST_IPS = Arrays.asList(new String[]{
            "127.0.0.1",
            "YOUR_PUBLIC_IP_HERE"
    });

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        forwardRequest("GET", req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        forwardRequest("POST", req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        forwardRequest("PUT", req, resp);
    }

    private void forwardRequest(String method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final boolean hasoutbody = (method.equals("POST") || method.equals("PUT"));

        URL url = null;

        try {
            String ipAddress = req.getHeader("HTTP_X_FORWARDED_FOR");
            if (ipAddress == null) {
                ipAddress = req.getRemoteAddr();
            }

            if (ALLOW_ONLY_WHITELISTED && !WHITE_LIST_IPS.contains(ipAddress)) {
                System.out.println("Unknown requester: " + ipAddress);
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String domain = req.getHeader("X-domain");
            if (domain == null || domain.isEmpty()) {
                domain = "http://httpbin.org";
            }
            url = new URL(domain // no trailing slash
                    + req.getRequestURI()
                    + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            final Enumeration<String> headers = req.getHeaderNames();
            log.info(" -- HEADER BEGIN --");
            while (headers.hasMoreElements()) {
                final String header = headers.nextElement();
                final Enumeration<String> values = req.getHeaders(header);
                while (values.hasMoreElements()) {
                    final String value = values.nextElement();
                    conn.addRequestProperty(header, value);
                    log.info(header + ": " + value);
                }
            }
            log.info(" -- HEADER END  --");

            //conn.setFollowRedirects(false);  // throws AccessDenied exception
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(hasoutbody);
            conn.connect();

            final byte[] buffer = new byte[16384];
            log.info(" -- REQUEST BEGIN --");
            while (hasoutbody) {
                final int read = req.getInputStream().read(buffer);
                if (read <= 0) break;
                conn.getOutputStream().write(buffer, 0, read);
                String s = new String(buffer, 0, read);
                log.info(s);
            }

            log.info(" -- REQUEST END  --");
            int statusCode = conn.getResponseCode();
            log.info("statusCode=" + statusCode + ", url=" + url);
            resp.setStatus(statusCode);
            for (int i = 0; ; ++i) {
                final String header = conn.getHeaderFieldKey(i);
                if (header == null) break;
                final String value = conn.getHeaderField(i);
                resp.setHeader(header, value);
            }

            log.info(" -- RESPONSE BEGIN --");
            while (true) {
                final int read = conn.getInputStream().read(buffer);
                if (read <= 0) break;
                resp.getOutputStream().write(buffer, 0, read);
                String s = new String(buffer, 0, read);
                log.info(s);
            }
            log.info(" -- RESPONSE END  --");

        } catch (Exception e) {
            log.log(Level.WARNING, "Error while forwarding request to url; " + url, e);
            // pass
        }
    }
}