package com.ecomindo.onboarding.springjpawebf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Config {

	@Value("${spring.custom.sftpHost}")
	private String sftpHost;

	@Value("${spring.custom.sftpPort}")
	private Integer sftpPort;

	@Value("${spring.custom.sftpUsername}")
	private String sftpUsername;
	
	@Value("${spring.custom.sftpPassword}")
	private String sftpPassword;

	@Value("${spring.custom.sftpFolder}")
	private String sftpFolder;
	
	public String getSftpHost() {
		return sftpHost;
	}

	public void setSftpHost(String sftpHost) {
		this.sftpHost = sftpHost;
	}

	public Integer getSftpPort() {
		return sftpPort;
	}

	public void setSftpPort(Integer sftpPort) {
		this.sftpPort = sftpPort;
	}

	public String getSftpUsername() {
		return sftpUsername;
	}

	public void setSftpUsername(String sftpUsername) {
		this.sftpUsername = sftpUsername;
	}

	public String getSftpPassword() {
		return sftpPassword;
	}

	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}

	public String getSftpFolder() {
		return sftpFolder;
	}

	public void setSftpFolder(String sftpFolder) {
		this.sftpFolder = sftpFolder;
	}
	
}
