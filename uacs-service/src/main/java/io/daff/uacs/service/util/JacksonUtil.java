package io.daff.uacs.service.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.daff.logging.DaffLogger;
import io.daff.logging.module.InnerModule;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson工具类
 *
 * @author daffupman
 * @since 2020/7/13
 */
@Component
public class JacksonUtil {

    private static final DaffLogger log = DaffLogger.getLogger(JacksonUtil.class);

    public static String beanToString(Object bean) {
        try {
            return JacksonConfig.fromBean(bean);
        } catch (JsonProcessingException e) {
            log.error("jackson序列化错误", InnerModule.INNER, e);
            throw new BaseException(Hint.SYSTEM_ERROR);
        }
    }

    public static <T> T stringToBean(String json, TypeReference<T> typeReference) {
        try {
            return JacksonConfig.toBean(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("jackson序列化错误", InnerModule.INNER, e);
            throw new BaseException(Hint.SYSTEM_ERROR);
        }
    }

    public static <T> T stringToBean(String json, Class<?> clazz) {
        try {
            return JacksonConfig.toBean(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("jackson序列化错误", InnerModule.INNER, e);
            throw new BaseException(Hint.SYSTEM_ERROR);
        }
    }

    static class JacksonConfig {
        /**
         * 时间格式
         */
        private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);

        private static final ObjectMapper objectMapper = generateDefaultObjectMapper();

        private static ObjectMapper generateDefaultObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf))
                    .addSerializer(new LocalDateTimeSerializer(dtf));

            return objectMapper.registerModule(javaTimeModule)
                    .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    // 不序列化null和""
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .setDateFormat(sdf);
        }

        public static ObjectMapper getInstance() {
            return objectMapper;
        }

        /**
         * 将bean实例转换成Json字符串
         *
         * @param bean bean实例
         * @param <T>  bean实例的类型
         * @return bean实例的json字符串
         */
        public static <T> String fromBean(T bean) throws JsonProcessingException {
            if (bean == null) {
                return null;
            }

            return bean instanceof String ? ((String) bean) : objectMapper.writeValueAsString(bean);
        }

        /**
         * 将json字符串转换成bean对象
         */
        @SuppressWarnings("unchecked")
        public static <T> T toBean(String json, TypeReference<T> typeReference) throws JsonProcessingException {
            if (StringUtils.isEmpty(json)) {
                return null;
            }

            return (T) (typeReference.getType().equals(String.class) ? json : objectMapper.readValue(json, typeReference));
        }

        /**
         * 将json字符串转换成bean对象
         */
        @SuppressWarnings("unchecked")
        public static <T> T toBean(String json, Class<?> clazz) throws JsonProcessingException {
            if (StringUtils.isEmpty(json)) {
                return null;
            }

            return (T) (clazz.equals(String.class) ? json : objectMapper.readValue(json, clazz));
        }
    }
}
