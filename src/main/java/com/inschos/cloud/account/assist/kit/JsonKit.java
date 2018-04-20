package com.inschos.cloud.account.assist.kit;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonKit {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T json2Bean(String json, Class<T> clazz) {
        try {
            if (StringKit.isEmpty(json)) {
                return null;
            }
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            T value = objectMapper.readValue(json, clazz);
            return value;
        } catch (Exception e) {
            L.log.error("json 2 bean Exception : {}",e.getMessage(),e);
        }
        return null;
    }

    public static <T> T json2Bean(String json, TypeReference<T> typeReference) {
        try {
            if (StringKit.isEmpty(json)) {
                return null;
            }

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            T value = objectMapper.readValue(json, typeReference);
            return value;
        } catch (IOException e) {
            L.log.error("json 2 bean Exception : {}",e.getMessage(),e);
        }
        return null;
    }

    public static String bean2Json(Object object) {
        try {
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            L.log.error("bean 2 json Exception : {}",e.getMessage(),e);
            return null;
        }
    }
}
