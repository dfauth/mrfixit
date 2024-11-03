package io.github.dfauth.mrfixit;

import io.github.dfauth.mrfixit.fields.FieldProcessor;
import lombok.extern.slf4j.Slf4j;
import quickfix.Field;
import quickfix.FieldConvertError;
import quickfix.field.converter.*;

import java.beans.Introspector;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.dfauth.trycatch.ExceptionalRunnable.tryCatch;

@Slf4j
public class ReflectiveBuilder<T extends ReflectiveBuilder<T>> {

    public static <T> T convertValue(String value, Class<T> classOfT) {
        try {
            if(String.class.equals(classOfT)) {
                return (T) value;
            } else if(Integer.class.equals(classOfT)) {
                return (T) Integer.valueOf(IntConverter.convert(value));
            } else if(Double.class.equals(classOfT)) {
                return (T) Double.valueOf(DoubleConverter.convert(value));
            } else if(BigDecimal.class.equals(classOfT)) {
                return (T) DecimalConverter.convert(value);
            } else if(Boolean.class.equals(classOfT)) {
                return (T) Boolean.valueOf(BooleanConverter.convert(value));
            } else if(LocalDateTime.class.equals(classOfT)) {
                return (T) UtcTimestampConverter.convertToLocalDateTime(value);
            } else if(Character.class.equals(classOfT)) {
                return (T) Character.valueOf(quickfix.field.converter.CharConverter.convert(value));
            } else if(char[].class.equals(classOfT)) {
                return (T) CharArrayConverter.convert(value);
            } else if(Date.class.equals(classOfT)) {
                return (T) UtcDateOnlyConverter.convert(value);
            } else if(LocalTime.class.equals(classOfT)) {
                return (T) UtcTimeOnlyConverter.convertToLocalTime(value);
            } else {
                throw new IllegalArgumentException("Failed value conversion: Unknown or unsupported type "+classOfT);
            }
        } catch (FieldConvertError e) {
            throw new RuntimeException(e);
        }
    }

    public T populate(Object obj) {
        streamProperties(obj).forEach(e -> {
            e.getValue().ifPresent(v -> {
                var field = new FieldProcessor(e.getKey()).invoke(v);
                setField(this, e.getKey(), field);
            });
        });
        return (T) this;
    }

    public static void setField(Object target, String key, Field<?> field) {
        try {
            var f = target.getClass().getDeclaredField(key);
            f.setAccessible(true);
            f.set(target, field);
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static Stream<Map.Entry<String, Optional<Object>>> streamProperties(Object obj) {
        return Stream.of(tryCatch(() -> Introspector.getBeanInfo(obj.getClass(), Object.class).getPropertyDescriptors()))
                .map(pd -> Map.entry(pd.getName(), tryCatch(() -> Optional.ofNullable(pd.getReadMethod().invoke(obj)))));
    }

    protected <T> T mandatory(T t, String fieldName) {
        return Optional.ofNullable(t).orElseThrow(() -> new IllegalArgumentException("No value supplied for mandatory field "+fieldName));
    }

}
