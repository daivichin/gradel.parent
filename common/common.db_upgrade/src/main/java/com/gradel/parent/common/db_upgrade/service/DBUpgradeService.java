package com.gradel.parent.common.db_upgrade.service;

import java.util.List;
import java.util.Map;

/**
 * DBUpgradeService
 *
 * @Date 2020/2/18 上午9:25
 * @Author  sdeven.chen.dongwei@gmail.com
 */
public interface DBUpgradeService {
    void checkAndAutoUpgrade();
    Boolean isInit(String name);
    List<Map<String, Object>> queryUpgradeInfo(String name);
}
