package com.eric.nuttybirds.config;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.Map;

/**
 * Created by erica_000 on 2/18/2018.
 */

public class EnemyManager {
    private ObjectMap<String, EnemyData> enemyData;

    public EnemyData get(String key) {
        return enemyData.get(key);
    }

    public void setData(ObjectMap<String, EnemyData> data) {
        this.enemyData = data;
    }
}
