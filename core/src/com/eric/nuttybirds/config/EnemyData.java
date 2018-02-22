package com.eric.nuttybirds.config;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by erica_000 on 2/18/2018.
 */

public class EnemyData {
    private String type;
    private String collisionType;
    private Integer collisionRadius;
    private Integer collisionWidth;
    private Integer collisionHeight;
    private ObjectMap<String,String> textures;
    private ObjectMap<String,Integer> offsets;

    public ObjectMap<String,String> getTextues() {
        return textures;
    }

    public Set<String> getTexturesAssets() {
        Set<String> assets = new HashSet<String>();
        for (String texture : textures.values()) {
            assets.add(texture);
        }

        return assets;
    }
}
