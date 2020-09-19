package com.gradel.parent.common.db_upgrade.configuration;

import lombok.Data;

import java.util.List;

/**
 * DBUpgradeProperty
 *
 * @Date 2020/2/18 上午10:21
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Data
public class DBUpgradeProperties {
    private List<DBUpgradeItem> items;
}
