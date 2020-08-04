package com.lijian.ftppool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: lijian
 * @Description:
 * @Date: Created in 2020-08-04 15:38
 * @Modified By:
 * @Version:
 * @TaskId:
 */
@Slf4j
@Component
public class FTPDataSource {

    // 使用map保存对象池
    private final Map<String, GenericObjectPool<FTPClient>> poolMap = new ConcurrentHashMap<>();

    private GenericObjectPool<FTPClient> getObjectPool(FTPConfig config) {
        String key = getKey(config);
        if (poolMap.get(key) == null) {
            createNewPool(config);
        }
        return poolMap.get(key);
    }

    public FTPClient getFtpClient(FTPConfig config){
        try {
            return getObjectPool(config).borrowObject();
        } catch (Exception e) {
            log.error("获取ftpclient连接异常", e);
        }
        return null;
    }

    public void returnObject(FTPClient ftpClient, FTPConfig ftpConfig){
        GenericObjectPool<FTPClient> objectPool = getObjectPool(ftpConfig);
        if(objectPool != null && ftpClient != null){
            objectPool.returnObject(ftpClient);
        }
    }

    private void createNewPool(FTPConfig config) {
        String key = getKey(config);
        if (poolMap.get(key) == null) {
            GenericObjectPool<FTPClient> objectPool = new GenericObjectPool(new FTPClientFactory(config));
            AbandonedConfig abandonedConfig = new AbandonedConfig();
            abandonedConfig.setLogAbandoned(true);
            abandonedConfig.setRemoveAbandonedTimeout(300); // 5分钟还未归还，则视为连接泄漏，直接移除
            abandonedConfig.setRemoveAbandonedOnBorrow(true); // 借出对象时移除废弃的连接
            abandonedConfig.setRemoveAbandonedOnMaintenance(true); // 在维护的时候检查是否有泄漏
            objectPool.setAbandonedConfig(abandonedConfig);
            objectPool.setTestOnBorrow(true);
            objectPool.setTestOnCreate(true);
            objectPool.setTestOnReturn(true);
            objectPool.setTimeBetweenEvictionRunsMillis(120000); // 120秒运行一次维护任务
            poolMap.put(key, objectPool);
        }
    }

    /**
     * @Description: 使用ip 端口 用户名拼接作为对象池map的key
     * @Auther: lijian
     * @Date:  2020-08-04 22:23
     * @param config
     * @Return: java.lang.String
     * @Version: V1.0.0
     * @TaskId: YJ-
     */
    private String getKey(FTPConfig config) {
        return config.getHost() + "," + config.getPort() + "," + config.getUsername();
    }
}