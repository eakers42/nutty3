package com.eric.nuttybirds.config;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by erica_000 on 2/20/2018.
 */

public class LevelManagerSerializer implements Json.Serializer<LevelManager> {
    @Override
    public void write(Json json, LevelManager object, Class knownType) {

    }

    @Override
    public LevelManager read(Json json, JsonValue jsonData, Class type) {
        return null;
    }
}
