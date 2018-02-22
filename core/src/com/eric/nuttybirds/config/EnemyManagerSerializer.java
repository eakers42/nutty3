package com.eric.nuttybirds.config;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by erica_000 on 2/18/2018.
 */

public class EnemyManagerSerializer implements Json.Serializer {
    @Override
    public void write(Json json, Object object, Class knownType) {

    }

    @Override
    public EnemyManager read(Json json, JsonValue jsonData, Class type) {
        EnemyManager enemies = new EnemyManager();
        ObjectMap<String, EnemyData> data = new ObjectMap<String, EnemyData>();
        for(JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
            EnemyData curData = json.readValue(entry.name, EnemyData.class, jsonData);
            data.put(entry.name, curData);
        }

        enemies.setData(data);
        return enemies;
    }
}
