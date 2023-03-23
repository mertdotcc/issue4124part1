package com.mertdotcc.sources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mertdotcc.mappingresources.destination.StudentWithGrades;
import com.mertdotcc.mappingresources.source.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.mapstruct.factory.Mappers;

public class IntegrationBean extends RouteBuilder {

    @BindToRegistry
    public IntegrationBean IntegrationBean() {
        return new IntegrationBean();
    }

    public void handleMapping(Exchange exchange) throws JsonProcessingException {
        StudentConverter converter = Mappers.getMapper(StudentConverter.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        Student student = mapper.readValue(exchange.getIn().getBody(String.class), Student.class);

        System.out.println("\nstudent: " + student);
        StudentWithGrades studentWithGrades = converter.map(student);
        System.out.println("\nstudentWithGrades: " + studentWithGrades);

        exchange.getIn().setBody(mapper.writeValueAsString(studentWithGrades));
    }

    @Override
    public void configure() throws Exception {
        this.getContext().getRegistry().bind("integrationBean", this);
    }
}
