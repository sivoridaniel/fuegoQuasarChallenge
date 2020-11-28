package com.challenge.meli.controller.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TopSecretReqDto {
    private List<SatelliteDto> satellites;
}
