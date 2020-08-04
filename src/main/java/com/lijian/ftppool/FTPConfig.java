package com.lijian.ftppool;

import lombok.Data;

/**
 * @Auther: lijian
 * @Description:
 * @Date: Created in 2020-08-04 15:42
 * @Modified By:
 * @Version:
 * @TaskId:
 */
@Data
public class FTPConfig {
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String remotePath;
}