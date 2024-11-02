package io.github.dfauth.mrfixit;

import io.github.dfauth.mrfixit.fields.FieldProcessor;
import lombok.extern.slf4j.Slf4j;
import quickfix.Field;

import java.beans.Introspector;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.dfauth.trycatch.ExceptionalRunnable.tryCatch;

@Slf4j
public class ReflectiveBuilder<T extends ReflectiveBuilder<T>> {

    public T populate(Object obj) {
        streamProperties(obj).forEach(e -> {
            var field = new FieldProcessor(e.getKey()).invoke(e.getValue());
            setField(this, e.getKey(), field);
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

    public static Stream<Map.Entry<String, Object>> streamProperties(Object obj) {
        return Stream.of(tryCatch(() -> Introspector.getBeanInfo(obj.getClass(), Object.class).getPropertyDescriptors()))
                .map(pd -> Map.entry(pd.getName(), tryCatch(() -> pd.getReadMethod().invoke(obj))));
    }

}
