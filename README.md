# Google AppEngine Request Proxy

Forward your HTTP requests through Google AppEngine (as proxy) while developing.

This will make you do local development for Google AppEngine (GAE) and connect to HTTPS endpoint without
GAE throwing `javax.net.ssl.SSLHandshakeException: Could not verify SSL certificate for: https://localhost:8080/1/oauth/request_token?locale=en` .

The current GAE version 1.9.49 does not always HTTPS connections for non-Google owned servers, when you
run a local instance of your GAE web application. There has been many issues [1](https://code.google.com/p/googleappengine/issues/detail?id=5203) [2](https://code.google.com/p/googleappengine/issues/detail?id=12705) posted for Google to try to fix this.

But until Google fixes the issue, you can circumvent the problem by proxying your requests to a remote
GAE instance, that then will forward your request to the right server - like in production.

![proxy](https://github.com/JeppeLeth/Google-AppEngine-request-proxy/raw/master/art/proxy.png "Development using a proxy")


## Demo
Use http://gae-forward.appspot.com (or https) as proxe for your requests.
Use the custom HTTP header `X-domain` to control which domain to forward your request too.

**Example** of POST request targeted `https://your.domain.com/path/here` using the proxy `https://gae-forward.appspot.com/path/here`

    POST /path/here HTTP/1.1
    Host: gae-forward.appspot.com
    X-domain: https://your.domain.com
    Cache-Control: no-cache

    {"key":"123","value":"John Doe"}



### Deploy
These instructions demonstrates how to deploy your own version application on Google App Engine.

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/

## Setup
1. Update `YOUR_PUBLIC_IP_HERE` string in `com.jleth.appengine.requestproxy.ForwardServlet`
   to white-list your public IP address. Or you can disable white-listing completely by changing
   `ALLOW_ONLY_WHITELISTED` - beware that this is not advices, as bots may also make requests to your app
1. Update the `<version>1.9.49</version>` tag in `pom.xml`
   with the [latest][sdk-download] AppEngine SDK version.
1. Update the `<application>` tag in `src/main/webapp/WEB-INF/appengine-web.xml`
   with your project name.
1. Update the `<version>` tag in `src/main/webapp/WEB-INF/appengine-web.xml`
   with your version name.

## Running locally
    $ mvn appengine:devserver

## Deploying
    $ mvn appengine:update

## Limitations
Right now this is a simple proxy, that only works for GET, POST and PUT requests.

[sdk-download]: https://cloud.google.com/appengine/docs/java/

License
=======
Copyright 2017 Jeppe Leth

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
