package me.fenixra.magic_altar.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class PhoenixFileClass {


    public Object getReference(int number) {
        return null;
    }



    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigKey {
        String path() default "";
        String space() default "";
        boolean isSection() default false;
    }
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigHeader {
        String[] value();

        String path() default "";
    }
}
