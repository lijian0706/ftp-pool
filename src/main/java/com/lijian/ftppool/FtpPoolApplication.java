package com.lijian.ftppool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
@RestController
@Slf4j
public class FtpPoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpPoolApplication.class, args);
	}

	@Autowired
	private FTPDataSource ftpDataSource;

	@GetMapping("/test")
	public void test(){
		FTPConfig ftpConfig = new FTPConfig();
		ftpConfig.setHost("localhost");
		ftpConfig.setPort(21);
		ftpConfig.setUsername("lijian");
		ftpConfig.setPassword("lijian");
		ftpConfig.setRemotePath("/test/");
		FTPClient ftpClient = ftpDataSource.getFtpClient(ftpConfig);
		try {
			FTPFile[] ftpFiles = ftpClient.listFiles(ftpConfig.getRemotePath());
			for (FTPFile ftpFile : ftpFiles) {
				log.info(ftpFile.getName());
			}
			ftpDataSource.returnObject(ftpClient, ftpConfig); // 归还ftpclient到对象池
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
