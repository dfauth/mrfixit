package io.github.dfauth.mrfixit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import quickfix.DoubleField;
import quickfix.Field;
import quickfix.IntField;

import static io.github.dfauth.trycatch.ExceptionalRunnable.tryCatch;

@Slf4j
@Data
@AllArgsConstructor
public class FieldProcessor {

    private final String packageName;
    private final String fieldName;

    public FieldProcessor(String fieldName) {
        this(tryCatch(FieldProcessor.class::getPackageName), fieldName);
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
            if(fieldClass.equals(IntField.class)) {
                return Integer.class;
            } else if(fieldClass.equals(DoubleField.class)) {
                return Double.class;
            } else {
                return String.class;
            }
        });
    }
}
