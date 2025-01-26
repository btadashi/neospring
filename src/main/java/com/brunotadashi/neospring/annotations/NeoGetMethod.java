package com.brunotadashi.neospring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
// Implica que ela deve ser aplicada em métodos.
@Target(ElementType.METHOD)
public @interface NeoGetMethod {
    // Recupera o valor da URL que será passado como parâmetro.
    public String value();

    // Poderíamos ter inclusive valor `default` que nos
    // permite omitir a presença desse método.
    // Porém, a ideia é que o usuário efetivamente precise
    // passar algum valor.
    // public String value() default "";

}
