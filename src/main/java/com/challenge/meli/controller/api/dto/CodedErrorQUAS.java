package com.challenge.meli.controller.api.dto;

import com.challenge.meli.utils.CodedError;
import com.challenge.meli.utils.Vali;

import java.text.MessageFormat;

public enum CodedErrorQUAS {

    INFO_SATELLITES_REQUIRED("001", "Satellite information can't be empty"),
    NAME_SATELLITE_REQUIRED("002", "Satellite's name is required"),
    MESSAGE_SATELLITE_REQUIRED("003", "Satellite's message is required"),
    DISTANCE_SATELLITE_REQUIRED("004", "Satellite's distance is required");

    private static final String QUAS = "QUAS-";
    private String codigo;
    private String mensage;

    CodedErrorQUAS(String codigo, String mensage) {
        this.codigo = codigo;
        this.mensage = mensage;
    }

    public CodedError toCodedError(Object... vars) {
        String msg = MessageFormat.format(this.mensage, vars);
        return new CodedError(QUAS + this.codigo, msg);
    }

    public <T> Vali<T> toVali(Object... vars) {
        return Vali.fail(toCodedError(vars));
    }

}
