package com.gradel.parent.common.db_upgrade.service.impl;

import com.gradel.parent.common.db_upgrade.configuration.DBUpgradeItem;
import com.gradel.parent.common.db_upgrade.configuration.DBUpgradeProperties;
import com.gradel.parent.common.db_upgrade.service.DBUpgradeService;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DBUpgradeServiceImpl
 *
 * @Date 2020/2/18 上午9:55
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Slf4j
public class DBUpgradeServiceImpl implements DBUpgradeService {
    private List<DBUpgradeItem> upgradeItems;

    private ResourceLoader resourceLoader;

    public DBUpgradeServiceImpl(DBUpgradeProperties properties) {
        upgradeItems = properties.getItems();
        resourceLoader = new DefaultResourceLoader();
    }

    private Boolean checkNeedInit(DBUpgradeItem item) {
        Connection conn = this.getConnection(item, true);
        try {
            SqlRunner runner = new SqlRunner(conn);
            Map<String, Object> dbExist = runner.selectOne(
                    "select count(1) from information_schema.SCHEMATA where `SCHEMA_NAME` = ?", item.getDbName());
            if (dbExist.values().iterator().next().equals(0L)) {
                log.info("checkNeedInit:true");
                return true;
            }
        }
        catch (SQLException e) {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        log.info("checkNeedInit:false");
        return false;
    }

    private void checkAndAutoInit(DBUpgradeItem item) {
        Resource init = this.getResource(this.getInitResourcePath(item));
        if (init.exists()) {
            if (this.checkNeedInit(item)) {
                this.exec(item, init, true);
                this.createUpgradeLogTable(item);
            }
        }
    }
    private void createUpgradeLogTable(DBUpgradeItem item) {
        Connection conn = this.getConnection(item, false);
        if (null != conn) {
            Reader reader;
            try {
                reader = new StringReader("" +
                        "drop table if exists db_upgrade_log;\n" +
                        "create table if not exists db_upgrade_log (\n" +
                        "    id bigint auto_increment primary key,\n" +
                        "    version varchar(32) not null,\n" +
                        "    status int default 0 not null comment '0:failed 1:success',\n" +
                        "    constraint db_upgrade_log_version_uindex unique (version)\n" +
                        ");");

                conn.setAutoCommit(false);
                ScriptRunner runner = new ScriptRunner(conn);
                runner.setLogWriter(null);//设置是否输出日志
                runner.setStopOnError(true);
                runner.runScript(reader);

                conn.commit();
                log.info("createUpgradeLogTable:true");
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                log.info("createUpgradeLogTable:false");
            } finally {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            log.info("createUpgradeLogTable:false");
        }
    }

    private Boolean checkCanUpgrade(DBUpgradeItem item) {
        Connection conn = this.getConnection(item, false);
        if (null == conn) {
            log.info("checkCanUpgrade:false");
            return false;
        }
        try {
            SqlRunner runner = new SqlRunner(conn);
            Map<String, Object> targetExist = runner.selectOne(
                    "select count(1) from db_upgrade_log where `version` = ?", item.getTargetVersion());
            if (targetExist.values().iterator().next().equals(0L)) {
                if (null != item.getCurrentVersion()) {
                    Map<String, Object> currentExist = runner.selectOne(
                            "select count(1) from db_upgrade_log where `status` = 1 and `version` = ?", item.getCurrentVersion());
                    if (currentExist.values().iterator().next().equals(1L)) {
                        log.info("checkCanUpgrade:true");
                        return true;
                    }
                    else {
                        log.info("checkCanUpgrade:false");
                        return false;
                    }
                }
                else {
                    log.info("checkCanUpgrade:true");
                    return true;
                }
            }
        }
        catch (SQLException e) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        log.info("checkCanUpgrade:false");
        return false;
    }

    private void saveUpgradeResult(DBUpgradeItem item, Boolean success) {
        Connection conn = this.getConnection(item, false);
        if (null == conn) {
            log.info("checkCanUpgrade:false");
            return;
        }
        try {
            SqlRunner runner = new SqlRunner(conn);
            runner.insert("insert into db_upgrade_log (`version`,`status`) values ( ?, ?)",
                    item.getTargetVersion(), success?1:0);
            log.info("saveUpgradeResult:true");
        }
        catch (SQLException e) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            log.info("saveUpgradeResult:false");
        }
    }
    private void checkAndAutoUpgrade(DBUpgradeItem item) {
        Resource upgrade = this.getResource(this.getUpgradeResourcePath(item));
        if (upgrade.exists()) {
            if (this.checkCanUpgrade(item)) {
                Boolean success = this.exec(item, upgrade, false);
                this.saveUpgradeResult(item, success);
            }
        }
    }


    @Override
    public void checkAndAutoUpgrade() {
        if (null != upgradeItems) {
            upgradeItems.forEach(o -> {
                checkAndAutoInit(o);
                checkAndAutoUpgrade(o);
            });
        }
    }

    @Override
    public Boolean isInit(String name) {
        Optional<DBUpgradeItem> item = upgradeItems.stream().filter(o -> o.getDbName().equals(name)).findFirst();
        return item.map(this::checkNeedInit).orElse(false);
    }

    @Override
    public List<Map<String, Object>> queryUpgradeInfo(String name) {
        Optional<DBUpgradeItem> item = upgradeItems.stream().filter(o -> o.getDbName().equals(name)).findFirst();
        if (item.isPresent()) {
            Connection conn = this.getConnection(item.get(), false);
            try {
                SqlRunner runner = new SqlRunner(conn);
                return runner.selectAll(
                        "select * from db_upgrade_log order by version desc");
            }
            catch (SQLException e) {
                if (null != conn) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private Connection getConnection(DBUpgradeItem item, Boolean root) {
        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setUseSSL(false);
            ds.setAllowPublicKeyRetrieval(true);
            if (root) {
                ds.setUrl(item.getAdminUrl());
                ds.setUser(item.getAdminName());
                ds.setPassword(item.getAdminPassword());
            }
            else {
                ds.setUrl(item.getUserUrl());
                ds.setUser(item.getUserName());
                ds.setPassword(item.getUserPassword());
                ds.setDatabaseName(item.getDbName());
            }
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Boolean exec(DBUpgradeItem item, Resource file, Boolean root) {
        boolean result = false;
        Connection conn = this.getConnection(item, root);
        if (null != conn) {
            Reader reader;
            try {
                reader = new InputStreamReader(file.getInputStream());

                conn.setAutoCommit(false);
                ScriptRunner runner = new ScriptRunner(conn);
                runner.setLogWriter(null);//设置是否输出日志
                runner.setStopOnError(true);
                runner.runScript(reader);

                conn.commit();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("exec[{}][{}]:{}", item.getDbName(), result, file.getFilename());
        return result;
    }


    private Resource getResource(String path) {
        return resourceLoader.getResource(path);
    }
    private String getUpgradeResourcePath(DBUpgradeItem item) {
        return String.format("classpath:db/%s/upgrade-%s.sql", item.getDbName(), item.getTargetVersion());
    }
    private String getInitResourcePath(DBUpgradeItem item) {
        return String.format("classpath:db/%s/init.sql", item.getDbName());
    }
}
