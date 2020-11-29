package com.challenge.meli.controller;

import com.challenge.meli.controller.api.dto.*;
import fj.P;
import fj.P2;
import fj.data.Array;
import fj.data.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.challenge.meli.service.QuasarService;
import com.challenge.meli.utils.errors.Vali;

import java.util.Arrays;

import static fj.P.p;

@RestController
public class QuasarController {

    private QuasarService quasarService;

    @Autowired
    public QuasarController(QuasarService quasarService){
        this.quasarService = quasarService;
    }

    @PostMapping(value = "/topsecret")
    public ResponseEntity<?> topsecret(@RequestBody TopSecretReqDto topSecretReqDto){

        Vali<TopSecretRespDto> vTSResp = valiRequestTopSecret(topSecretReqDto).bind(topSecretCMD -> {
            Array<Float> location = quasarService.getLocation(topSecretCMD.getDistances());
            String message = quasarService.getMessage(topSecretCMD.getMessages());
            return Vali.iff(location.isNotEmpty() && !message.isEmpty(), () -> p(location,message),
                            () -> CodedErrorQUAS.INTERPRETATION_FAILURE.toCodedError() )
                       .map(p_LocMes ->  TopSecretRespDto.builder()
                                                          .position(PositionDto.builder()
                                                                               .x(p_LocMes._1().get(0))
                                                                               .y(p_LocMes._1().get(1))
                                                                               .build())
                                                          .message(p_LocMes._2())
                                                          .build());
        });

        return vTSResp.toResponseEntity(HttpStatus.NOT_FOUND, CodedErrorQUAS.INTERPRETATION_FAILURE.toCodedError());
    }

    @Builder(toBuilder = true)
    @Getter
    static class TopSecretCMD{
        @Builder.Default
        private Array<Array<String>> messages=Array.empty();
        @Builder.Default
        private Array<Float> distances=Array.empty();
    }

    private Vali<TopSecretCMD> valiRequestTopSecret(TopSecretReqDto tsrdto) {
        Vali<List<SatelliteDto>> v1 = Vali.fromNonEmptyJavaList(tsrdto.getSatellites(), CodedErrorQUAS.INFO_SATELLITES_REQUIRED
                                                                                                      .toCodedError());

        return v1.bind(satelliteDtos ->
                fj.data.List.iterableList(satelliteDtos)
                            .foldRight((sat,val) -> valiInfoSatellite(sat, val),
                                        Vali.success(TopSecretCMD.builder().build())));
    }

    private Vali<TopSecretCMD> valiInfoSatellite(SatelliteDto sat, Vali<TopSecretCMD> val) {
        return Vali.fromNotNullOrBlank(sat.getName(), CodedErrorQUAS.NAME_SATELLITE_REQUIRED.toCodedError())
                .bind(name -> Vali.fromNonEmptyJavaList(Arrays.asList(sat.getMessage()),
                        CodedErrorQUAS.MESSAGE_SATELLITE_REQUIRED
                                .toCodedError()))
                .map(message -> message.toArray())
                .bind(message ->
                        val.bind(tsCMD -> {
                            Float distance = sat.getDistance();
                            Array<Float> distances = tsCMD.getDistances();
                            Array<Array<String>> messages = tsCMD.getMessages();
                            return Vali.fromNotNullOrBlank(distance, CodedErrorQUAS.DISTANCE_SATELLITE_REQUIRED
                                    .toCodedError())
                                    .map(d -> tsCMD.toBuilder()
                                            .distances(Array.array(d).append(distances))
                                            .messages(Array.array(message).append(messages))
                                            .build());
                        }));
    }

    @PostMapping(value = "/topsecret_split/{satellite_name}")
    public ResponseEntity<?> topsecret_split(@PathVariable(name = "satellite_name") String satelliteName,
                                             @RequestBody SatelliteDto satelliteDto){

        SatelliteDto infoSatellite = satelliteDto.toBuilder().name(satelliteName).build();
        Vali<TopSecretCMD> topSecretCMDVali = valiInfoSatellite(infoSatellite, Vali.success(TopSecretCMD.builder().build()));

        Vali<TopSecretRespDto> vTSResp = topSecretCMDVali.bind(topSecretCMD -> {
            P2<Array<Float>, String> p_Location_Message = quasarService.getLocationAndMessage(satelliteName,
                                                                                              topSecretCMD.getDistances(),
                                                                                              topSecretCMD.getMessages()
                                                                                                          .get(0));
            return Vali.iff(p_Location_Message._1().isNotEmpty() && !p_Location_Message._2().isEmpty(),
                            () -> p_Location_Message, () -> CodedErrorQUAS.INTERPRETATION_FAILURE.toCodedError() )
                       .map(p_LocMes ->  TopSecretRespDto.builder()
                                                         .position(PositionDto.builder()
                                                                              .x(p_LocMes._1().get(0))
                                                                              .y(p_LocMes._1().get(1))
                                                                              .build())
                                                         .message(p_LocMes._2())
                                                         .build());
        });

        return vTSResp.toResponseEntity(HttpStatus.NOT_FOUND, CodedErrorQUAS.INTERPRETATION_FAILURE.toCodedError());
    }

}
