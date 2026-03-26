package com.hanyoonsoo.ordersystem.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ObjectMapperUtilsTest {

    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ObjectMapperUtils objectMapperUtils;

    @Test
    void 객체를_JSON_문자열로_직렬화한다() throws Exception {
        // given
        given(objectMapper.writeValueAsString("value")).willReturn("\"value\"");

        // when
        String actual = objectMapperUtils.writeValueAsString("value");

        // then
        assertThat(actual).isEqualTo("\"value\"");
    }

    @Test
    void 직렬화에_실패하면_런타임_예외를_발생시킨다() throws Exception {
        // given
        given(objectMapper.writeValueAsString("value")).willThrow(new JsonProcessingException("boom") {});

        // when & then
        assertThatThrownBy(() -> objectMapperUtils.writeValueAsString("value"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to serialize payload");
    }
}
