package com.challenge.meli.controller.api.dto;

import lombok.Builder;

@Builder
public class TopSecretRespDto {

    private PositionDto position;
    private String message;

}
