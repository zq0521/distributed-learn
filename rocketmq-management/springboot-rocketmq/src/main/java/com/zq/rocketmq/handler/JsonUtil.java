package com.zq.rocketmq.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * JSON序列化工具
 */
public class JsonUtil {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转化为String串
     *
     * @param obj 实体类对象
     * @return
     * @throws JsonProcessingException
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }


    /**
     * 反序列化
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return MAPPER.readValue(json, clazz);
    }


    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) throws IOException {
        return MAPPER.readValue(json, valueTypeRef);
    }


    public static <T> T fromJson(InputStream stream, Class<T> clazz) throws IOException {
        return MAPPER.readValue(stream, clazz);
    }


}
