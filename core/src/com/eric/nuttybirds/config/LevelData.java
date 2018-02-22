package com.eric.nuttybirds.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by erica_000 on 2/17/2018.
 */

public class LevelData {
    private String title;
    private String [] waveArray;
    private EnemyManager enemyManager;
    private Set<String> externalAssets;

    public void setExternalAssets(Set<String> externalAssets) {
        this.externalAssets = externalAssets;
    }

    public void setEnemeyManager(EnemyManager enemeyManager) {
        this.enemyManager = enemeyManager;
    }

    public Set<String> getAssets() {
        Set<String> assets = new HashSet<String>();
        for (String wave : waveArray) {
            String [] enemies = wave.split(",");
            for (String enemy : enemies) {
                EnemyData curEnemy = this.enemyManager.get(enemy);
                assets.addAll(curEnemy.getTexturesAssets());
            }
        }

        if (this.externalAssets != null) {
            assets.addAll(this.externalAssets);
        }

        return assets;
    }

    public int waveSize() {
        return waveArray.length;
    }

    public Set<EnemyData> getEnemiesOnLevel(int level) {
        String [] enemies = this.waveArray[level].split(",");
        Set<EnemyData> enemySet = new HashSet<EnemyData>();
        for (String enemy : enemies) {
            EnemyData curEnemy = this.enemyManager.get(enemy);
            enemySet.add(curEnemy);
        }

        return enemySet;
    }
}
