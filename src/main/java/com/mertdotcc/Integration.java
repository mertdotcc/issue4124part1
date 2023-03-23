package com.mertdotcc;
// camel-k: language=java
// camel-k: name=issue4124-part1-integration

import com.mertdotcc.sources.IntegrationBean;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class Integration extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .setHeader("CamelHttpResponseCode").constant("400")
                .setBody().simple("resource:file:/etc/camel/resources/sample-error-response.json")
                .log(LoggingLevel.ERROR, "exception", "${exception}").routeId("exception");

        // POST
        from("direct://addStudent").routeId("addStudent")
                .log(LoggingLevel.INFO, "before-mapping", "headers: ${headers}; body: ${body}")
                .removeHeaders("*")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    IntegrationBean bean =
                        (IntegrationBean) this.getCamelContext().getRegistry().lookupByName("IntegrationBean");
                    bean.handleMapping(exchange);
                }) 
                .log(LoggingLevel.INFO, "after-mapping", "headers: ${headers}; body: ${body}");
    }
}
