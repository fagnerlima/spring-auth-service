package br.pro.fagnerlima.spring.auth.api.infrastructure.security.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception.CustomOAuth2Exception;

public class CustomOAuth2ExceptionSerializer extends StdSerializer<CustomOAuth2Exception> {

    private static final long serialVersionUID = 4162240606452072965L;

    protected CustomOAuth2ExceptionSerializer() {
        super(CustomOAuth2Exception.class);
    }

    @Override
    public void serialize(CustomOAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("data", null);
        gen.writeObjectField("errors", Arrays.asList(value.getMessage()));
        gen.writeObjectField("links", new ArrayList<>());
        gen.writeEndObject();
        gen.close();
    }

}
