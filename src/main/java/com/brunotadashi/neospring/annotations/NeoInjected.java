package com.brunotadashi.neospring.annotations;


// O termo "Injected" foi pegado emprestado do Quarkus,
// que seria equivalente ao AutoWired do Spring

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
// Field, pois ele é um atributo.
@Target(ElementType.FIELD)
public @interface NeoInjected {
}
