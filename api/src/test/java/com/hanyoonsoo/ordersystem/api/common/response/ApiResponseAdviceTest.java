package com.hanyoonsoo.ordersystem.api.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseAdviceTest {

    private final ApiResponseAdvice apiResponseAdvice = new ApiResponseAdvice(new ObjectMapper());

    @Test
    void 이미_ApiResponse인_응답은_그대로_반환한다() throws Exception {
        // given
        ApiResponse<String> body = ApiResponse.success("ok");

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                body,
                methodParameter("apiResponse"),
                MediaType.APPLICATION_JSON,
                org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class,
                request("/api/test"),
                response()
        );

        // then
        assertThat(actual).isSameAs(body);
    }

    @Test
    void 일반_객체_응답은_ApiResponse로_감싼다() throws Exception {
        // given

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                Map.of("message", "ok"),
                methodParameter("object"),
                MediaType.APPLICATION_JSON,
                org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class,
                request("/api/test"),
                response()
        );

        // then
        assertThat(actual).isInstanceOf(ApiResponse.class);
        ApiResponse<?> apiResponse = (ApiResponse<?>) actual;
        assertThat(apiResponse.isSuccess()).isTrue();
        assertThat(apiResponse.getData()).isEqualTo(Map.of("message", "ok"));
    }

    @Test
    void 에러_응답은_실패_ApiResponse로_감싼다() throws Exception {
        // given
        ErrorResponse errorResponse = ErrorResponse.of("A001", "잘못된 요청입니다.", "/api/test");

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                errorResponse,
                methodParameter("object"),
                MediaType.APPLICATION_JSON,
                org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class,
                request("/api/test"),
                response()
        );

        // then
        assertThat(actual).isInstanceOf(ApiResponse.class);
        ApiResponse<?> apiResponse = (ApiResponse<?>) actual;
        assertThat(apiResponse.isSuccess()).isFalse();
        assertThat(apiResponse.getError()).isEqualTo(errorResponse);
    }

    @Test
    void 문자열_응답은_직렬화된_JSON_문자열로_반환한다() throws Exception {
        // given

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                "ok",
                methodParameter("string"),
                MediaType.TEXT_PLAIN,
                StringHttpMessageConverter.class,
                request("/api/test"),
                response()
        );

        // then
        assertThat(actual).isInstanceOf(String.class);
        assertThat((String) actual).contains("\"success\":true");
        assertThat((String) actual).contains("\"data\":\"ok\"");
    }

    @Test
    void 바이트배열_응답은_감싸지_않는다() throws Exception {
        // given
        byte[] body = "ok".getBytes();

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                body,
                methodParameter("bytes"),
                MediaType.APPLICATION_OCTET_STREAM,
                ByteArrayHttpMessageConverter.class,
                request("/api/test"),
                response()
        );

        // then
        assertThat(actual).isSameAs(body);
    }

    @Test
    void 액추에이터_경로는_응답을_감싸지_않는다() throws Exception {
        // given
        Map<String, String> body = Map.of("status", "UP");

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                body,
                methodParameter("object"),
                MediaType.APPLICATION_JSON,
                org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class,
                request("/actuator/health"),
                response()
        );

        // then
        assertThat(actual).isEqualTo(body);
    }

    @Test
    void 리소스_응답은_감싸지_않는다() throws Exception {
        // given
        ByteArrayResource resource = new ByteArrayResource("file".getBytes());

        // when
        Object actual = apiResponseAdvice.beforeBodyWrite(
                resource,
                methodParameter("resource"),
                MediaType.APPLICATION_OCTET_STREAM,
                org.springframework.http.converter.ResourceHttpMessageConverter.class,
                request("/api/file"),
                response()
        );

        // then
        assertThat(actual).isSameAs(resource);
    }

    private MethodParameter methodParameter(String methodName) throws NoSuchMethodException {
        Method method = SampleController.class.getDeclaredMethod(methodName);
        return new MethodParameter(method, -1);
    }

    private ServletServerHttpRequest request(String path) {
        return new ServletServerHttpRequest(new MockHttpServletRequest("GET", path));
    }

    private ServletServerHttpResponse response() {
        return new ServletServerHttpResponse(new MockHttpServletResponse());
    }

    @SuppressWarnings("unused")
    static class SampleController {
        ApiResponse<String> apiResponse() {
            return ApiResponse.success("ok");
        }

        Object object() {
            return Map.of("message", "ok");
        }

        String string() {
            return "ok";
        }

        byte[] bytes() {
            return "ok".getBytes();
        }

        ByteArrayResource resource() {
            return new ByteArrayResource("file".getBytes());
        }
    }
}
