package io.github.dfauth.mrfixit.fields;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import quickfix.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static io.github.dfauth.trycatch.ExceptionalRunnable.tryCatch;
import static org.apache.commons.lang3.StringUtils.capitalize;

@Slf4j
@Data
public class FieldProcessor {

    private final String packageName;
    private final String fieldName;

    public FieldProcessor(String fieldName) {
        this(tryCatch(FieldProcessor.class::getPackageName), fieldName);
    }

    public FieldProcessor(String packageName, String fieldName) {
        this.packageName = packageName;
        this.fieldName = capitalize(fieldName);
    }

    public <T extends Field<?>> Class<T> getFieldClass() {
        return tryCatch(() -> (Class<T>) Class.forName(String.format("%s.%s",packageName,fieldName)));
    }

    public int getFieldTag() {
        return tryCatch(() -> (int)getFieldClass().getField("FIELD").get(getFieldClass()));
    }

    public Class<?> getFieldType() {
        return tryCatch(() -> {
            var fieldClass = getFieldClass();
            if(IntField.class.isAssignableFrom(fieldClass)) {
                return Integer.class;
            } else if(DoubleField.class.isAssignableFrom(fieldClass)) {
                return Double.class;
            } else if(DecimalField.class.isAssignableFrom(fieldClass)) {
                return BigDecimal.class;
            } else if(CharField.class.isAssignableFrom(fieldClass)) {
                return Character.class;
            } else if(StringField.class.isAssignableFrom(fieldClass)) {
                return String.class;
            } else {
                throw new IllegalArgumentException("Unsupported field type: "+fieldClass);
            }
        });
    }

    public Field<?> invoke(Object obj) {
        return tryCatch(() -> Stream.of(getFieldClass().getDeclaredConstructors()).filter(c -> c.getParameters().length == 1).findFirst()
                .map(c -> tryCatch(() -> (Field<?>) c.newInstance(obj))).orElseThrow());
    }
}
