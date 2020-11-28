package com.challenge.meli.controller;

import com.challenge.meli.controller.api.dto.*;
import fj.data.Array;
import fj.data.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.challenge.meli.service.QuasarService;
import com.challenge.meli.utils.Vali;

import java.util.Arrays;

import static fj.function.Strings.*;

@RestController
public class QuasarController {

    private QuasarService quasarService;

    @Autowired
    public QuasarController(QuasarService quasarService){
        this.quasarService = quasarService;
    }

    @PostMapping(value = "/topsecret")
    public ResponseEntity<?> topsecret(@RequestBody TopSecretReqDto topSecretReqDto){

        Vali<TopSecretRespDto> vTSResp = valiRequestTopSecret(topSecretReqDto).map(topSecretCMD -> {
            Array<Float> location = quasarService.getLocation(topSecretCMD.getDistances());
            String message = quasarService.getMessage(topSecretCMD.getMessages());
            return TopSecretRespDto.builder()
                                   .message(message)
                                   .position(PositionDto.builder()
                                                        .x(location.get(0))
                                                        .x(location.get(1))
                                                        .build())
                                   .build();
        });

        return vTSResp.toResponseEntity();
    }

    @Builder(toBuilder = true)
    @Getter
    static class TopSecretCMD{
        private Array<Array<String>> messages;
        private Array<Float> distances;
    }

    private Vali<TopSecretCMD> valiRequestTopSecret(TopSecretReqDto tsrdto) {
        Vali<List<SatelliteDto>> v1 = Vali.fromNonEmptyJavaList(tsrdto.getSatellites(), CodedErrorQUAS.INFO_SATELLITES_REQUIRED
                                                                                                      .toCodedError());

        return v1.bind(satelliteDtos ->
                fj.data.List.iterableList(satelliteDtos)
                            .foldLeft((val, sat) ->
                            {
                                Vali.fromNotNullOrBlank(sat.getName(), CodedErrorQUAS.NAME_SATELLITE_REQUIRED.toCodedError())
                                    .bind(name -> Vali.fromNonEmptyJavaList(Arrays.asList(sat.getMessage()),
                                                                            CodedErrorQUAS.MESSAGE_SATELLITE_REQUIRED
                                                                                          .toCodedError()))
                                                      .map(message -> message.toArray())
                                        .bind(message ->{
                                            TopSecretCMD tsCMD = val.success();
                                            Float distance = sat.getDistance();
                                            return Vali.fromNotNullOrBlank(distance, CodedErrorQUAS.DISTANCE_SATELLITE_REQUIRED
                                                                                                   .toCodedError())
                                                       .map(d -> tsCMD.toBuilder()
                                                                      .distances(Array.array(d))
                                                                      .messages(Array.array(message))
                                                                      .build());
                                        });
                                return Vali.success(TopSecretCMD.builder().build());
                            }, Vali.success(TopSecretCMD.builder().build())));
    }

}
