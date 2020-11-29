package com.challenge.meli.controller.api.dto;

import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSecretReqDto {
    private List<SatelliteDto> satellites;
}
