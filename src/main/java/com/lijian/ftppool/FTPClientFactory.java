package com.lijian.ftppool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

/**
 * @Author: Lijian
 * @Date: 2019-09-11 15:06
 */
@Slf4j
public class FTPClientFactory extends BasePooledObjectFactory<FTPClient> {

    private FTPConfig config;
    private static final Integer CONNECT_TIMEOUT = 10000;

    public FTPClientFactory(FTPConfig config) {
        this.config = config;
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        try {
            return p.getObject().sendNoOp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public FTPClient create() {
        log.info("create FTPClient");
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(CONNECT_TIMEOUT);
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setRemoteVerificationEnabled(false);
        try {
            ftpClient.connect(config.getHost(), config.getPort());
            Boolean loginResult = ftpClient.login(config.getUsername(), config.getPassword());
            if (!loginResult) {
                log.error("登录FTP失败，账号或密码错误");
                throw new IllegalArgumentException("登录FTP失败，账号或密码错误");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            log.error("IOException:", e);
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient obj) {
        return new DefaultPooledObject<>(obj);
    }
}
