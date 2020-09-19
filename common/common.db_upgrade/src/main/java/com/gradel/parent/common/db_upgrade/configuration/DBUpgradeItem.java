package com.gradel.parent.common.db_upgrade.configuration;

import lombok.Data;

/**
 * DBUpgradeItem
 *
 * @Date 2020/2/18 上午10:23
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Data
public class DBUpgradeItem {
    private String dbName;
    private String adminName;
    private String adminPassword;
    private String adminUrl;
    private String userName;
    private String userPassword;
    private String userUrl;
    private String driverClassName;
    private String targetVersion;
    private String currentVersion;
}
