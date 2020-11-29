package com.challenge.meli.utils.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import fj.data.NonEmptyList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import static fj.data.List.list;
import static fj.data.NonEmptyList.nel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"codigo"})
public class CodedError {
    private String codigo;
    private String mensaje;
    @JsonIgnore
    private Throwable throwable;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CodedError> causas;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Boolean esParaUsuarioFinal;

    public CodedError(String codigo, String mensaje, List<CodedError> causas, Throwable throwable) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.causas = causas;
        this.throwable = throwable;
        this.esParaUsuarioFinal = false;
    }

    public CodedError(String codigo, String mensaje, boolean esParaUsuarioFinal) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.causas = null;
        this.throwable = null;
        this.esParaUsuarioFinal = esParaUsuarioFinal;
    }

    public CodedError(String codigo, String mensaje) {
        this(codigo, mensaje, null, new CodedException(""));
    }

    public CodedError(String codigo, String mensaje, Throwable ex) {
        this(codigo, mensaje, null, ex);
    }

    public <T> Vali<T> toVali() {
        return Vali.fail(this);
    }

    public static class CodedException extends RuntimeException {
        public CodedException(String message) {
            super(message);
        }
    }

    private CodedError setCausas(List<CodedError> causas) {
        return new CodedError(this.codigo, this.mensaje, causas, this.throwable);
    }

    public NonEmptyList<CodedError> chainCausas(NonEmptyList<CodedError> causas) {
        return causas.head().esParaUsuarioFinal ? causas : nel(this.setCausas(causas.toList().toJavaList()));
    }
    public <T> Vali<T> chainCausasInVali(NonEmptyList<CodedError> causas) {
        return Vali.fail(this.chainCausas(causas));
    }
    public List<CodedError> toList() {
        return list(this).toJavaList();
    }

    @Override
    public String toString() {
        return "CodedError{" +
                "codigo='" + codigo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", throwable=" + throwable +
                ", causas=" + causas +
                '}';
    }
}

