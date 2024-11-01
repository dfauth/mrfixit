package io.github.dfauth.mrfixit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectiveBuilder<T extends ReflectiveBuilder<T>> {

    public T populate(Object obj) {
        return (T) this;
    }

}
