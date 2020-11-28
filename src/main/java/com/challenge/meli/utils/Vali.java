package com.challenge.meli.utils;

import fj.*;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Validation;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Objects;
import static fj.data.List.iterableList;
import static fj.data.NonEmptyList.nel;

public class Vali<T> {
    private Validation<NonEmptyList<CodedError>, T> val;

    private Vali(Validation<NonEmptyList<CodedError>, T> val) {
        this.val = val;
    }

    public static <T> Vali<T> success(T success) {
        return new Vali<>(Validation.success(success));
    }

    public static <T> Vali<T> fail(CodedError e) {
        return new Vali<>(Validation.fail(nel(e)));
    }

    public static <T> Vali<T> fail(NonEmptyList<CodedError> ls) {
        return new Vali<>(Validation.fail(ls));
    }

    public static <T> Vali<T> from(Validation<NonEmptyList<CodedError>, T> v) {
        return new Vali<>(v);
    }

    public static <T> Vali<List<T>> fromNonEmptyJavaList(java.util.List<T> list, CodedError ce) {
        return list != null && !list.isEmpty() ? Vali.success(iterableList(list)) : ce.toVali();
    }

    public static <T> Vali<T> fromNotNullOrBlank(T text, CodedError ce) {
        return Vali.iff(text!=null && !text.toString().isEmpty(), () -> text, () -> ce);
    }

    public static <T> Vali<T> iff(boolean b, Supplier<T> succSupplier, Supplier<CodedError> failSupplier) {
        return b ? success(succSupplier.get()) : fail(failSupplier.get());
    }

    public boolean isSuccess() {
        return val.isSuccess();
    }

    public T success() {
        return val.success();
    }

    public <A> Vali<A> map(F<T, A> f) {
        return from(val.map(f));
    }

    public <A> Vali<A> bind(F<T, Vali<A>> f) {
        return from(val.bind(t -> f.f(t).val));
    }

    public ResponseEntity<?> toResponseEntity(HttpStatus errorStatus, HttpStatus okStatus) {
        return isSuccess() ? new ResponseEntity<>(val.success(), okStatus) : new ResponseEntity<>(val.fail().toList().toJavaList(), errorStatus);
    }

    public ResponseEntity<?> toResponseEntity() {
        return toResponseEntity(HttpStatus.BAD_REQUEST, HttpStatus.OK);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vali<?> vali = (Vali<?>) o;
        return Objects.equals(val, vali.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public String toString() {
        return val.toEither().either(e -> "Vali:fail(" + e.toString() + ")", t -> "Vali:success(" + t.toString() + ")");
    }
}
