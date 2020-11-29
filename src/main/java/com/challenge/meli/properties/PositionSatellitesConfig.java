package com.challenge.meli.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PositionSatellitesConfig {

    @Value("#{'${position.satelliteKenovi}'.split(',')}")
    private String[] positionSatelliteKenovi;
    @Value("#{'${position.satelliteSkywalker}'.split(',')}")
    private String[] positionSatelliteSkywalker;
    @Value("#{'${position.satelliteSato}'.split(',')}")
    private String[] positionSatelliteSato;

    public Float[] getPositionSatelliteKenovi() {
        return fj.data.Array.array(positionSatelliteKenovi)
                            .map(pos -> Float.parseFloat(pos))
                            .array(Float[].class);
    }

    public Float[] getPositionSatelliteSkywalker() {
        return fj.data.Array.array(positionSatelliteSkywalker)
                            .map(pos -> Float.parseFloat(pos))
                            .array(Float[].class);
    }

    public Float[] getPositionSatelliteSato() {
        return fj.data.Array.array(positionSatelliteSato)
                            .map(pos -> Float.parseFloat(pos))
                            .array(Float[].class);
    }

}
