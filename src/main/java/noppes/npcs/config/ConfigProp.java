package noppes.npcs.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigProp {

   String def() default "";

   String info() default "";

   String max() default "";

   String min() default "";

   String name() default "";

   String type() default "common"; // "client", "server"

}
