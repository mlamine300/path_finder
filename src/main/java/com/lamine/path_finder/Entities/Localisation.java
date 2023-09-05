package com.lamine.path_finder.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Localisation {
    private float latitude,longitude;

    @Override
    public String toString() {
        return "Localisation{" +
                "latitude:" + this.latitude +
                ", longitude:=" + this.longitude +
                '}';
    }
}
