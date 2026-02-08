package de.njsm.stocks.server.v2.web.meta;

import de.njsm.stocks.common.api.Response;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice(annotations = RestController.class)
public class StatusCodeToHttpCodeFilter implements ResponseBodyAdvice<Response> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getGenericParameterType().getTypeName().equals(Response.class.getCanonicalName());
    }

    @Override
    public Response beforeBodyWrite(@Nullable Response entity, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (entity == null) {
            return entity;
        }
        jakarta.ws.rs.core.Response.Status code = entity.getStatus().toHttpStatus();
        response.setStatusCode(HttpStatusCode.valueOf(code.getStatusCode()));
        return entity;
    }
}
