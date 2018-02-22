package com.eric.nuttybirds.config;

import com.badlogic.gdx.utils.Array;

import java.util.Set;

/**
 * Created by erica_000 on 2/20/2018.
 */

public class LevelManager {
    private Array<LevelData> levelData;
    private EnemyManager enemyManager;
    private Set<String> externalAssets;

    /**
     * The level data contains only the assets for that level. This allows
     * adding extra assets that are considered part of the level.
     * @param externalAssets
     */
    public void setExternalAssets(Set<String> externalAssets) {
        this.externalAssets = externalAssets;
    }

    public void setEnemyManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
    }

    public LevelData getLevel(int index) {
        LevelData ld = levelData.get(index);
        ld.setEnemeyManager(this.enemyManager);
        ld.setExternalAssets(this.externalAssets);
        return ld;
    }
}
