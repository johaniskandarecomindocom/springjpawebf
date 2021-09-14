package com.ecomindo.onboarding.springjpawebf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpUtil {
	private static int _conecctionTimeout = 60000;

	private String host;
	private Integer port;
	private String user;
	private String password;
	private byte[] certificateFile;
	private String homePath;
	private int idleTime;
	

	// ### Constructor ###
	public SftpUtil(String host, String user) {
		this.host = host;
		this.port = 22;
		this.user = user;
		this.password = "";
		this.certificateFile = null;
		this.homePath = "";
		this.idleTime = _conecctionTimeout;
	}

	public SftpUtil(String host, String user, String password) {
		this.host = host;
		this.port = 22;
		this.user = user;
		this.password = password;
		this.certificateFile = null;
		this.homePath = "";
		this.idleTime = _conecctionTimeout;
	}

	public SftpUtil(String host, Integer port, String user, String password, String homePath) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.certificateFile = null;
		this.homePath = homePath;
		this.idleTime = _conecctionTimeout;
	}

	public SftpUtil(String host, Integer port, String user, String password, byte[] certificateFile, String homePath) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.certificateFile = certificateFile;
		this.homePath = homePath;
		this.idleTime = _conecctionTimeout;
	}

	public SftpUtil(String host, Integer port, String user, String password, byte[] certificateFile, String homePath,
			int idleTime) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.certificateFile = certificateFile;
		this.homePath = homePath;
		this.idleTime = idleTime;
	}

	public void sftpCreateDirectory(String folderPath) throws Exception {
		ChannelSftp channelSftp = null;
		channelSftp = getSftpClient();

		String[] folders = folderPath.split("/");

		for (String folder : folders) {
			if (folder.length() > 0) {
				try {
					channelSftp.cd(folder);
				} catch (SftpException e) {
					channelSftp.mkdir(folder);
					channelSftp.cd(folder);
				}
			}
		}
	}

	public void sftpPutFromStream(InputStream fileStream, String outputFileName) throws Exception {
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();

			// Upload or put file to server folder IN
			channelSftp.put(fileStream, outputFileName);
		} catch (Exception e) {
			throw e;
		} finally {
			closeSession(channelSftp);
			// channelSftp.disconnect();
		}
	}

	public void sftpPutFromFile(String filePath, String outputFileName) throws Exception {
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();

			File f = new File(filePath);

			// Upload or put file to server folder IN
			channelSftp.put(new FileInputStream(f), outputFileName);
		} catch (Exception e) {
			throw e;
		} finally {
			closeSession(channelSftp);
			// channelSftp.disconnect();
		}
	}

	public void sftpGetToFile(String remoteFileName, String outputFile) throws Exception {
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();

			File file = new File(outputFile);
			OutputStream output = new FileOutputStream(file);

			// Get File From Folder Server
			channelSftp.get(remoteFileName, output);
		} catch (Exception e) {
			throw e;
		} finally {
			closeSession(channelSftp);
			// channelSftp.disconnect();
		}
	}

	public Map<String, String> sftpGetToDirFile(String remoteFileName, String outputFile, Map<String, String> lsStatus,
			String bankCode) throws Exception {
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();
			Vector<LsEntry> entries = channelSftp.ls("*.*");

			if (entries.size() > 0 && entries != null) {

				for (LsEntry lsEntry : entries) {

					if (lsEntry.getFilename().length() > 12) {
						if (lsEntry.getFilename().substring(0, 12).equals(bankCode)) {
							boolean isPutFile = false;
							File localFile = new File(outputFile + "/" + lsEntry.getFilename());
							String remote = remoteFileName + "/" + lsEntry.getFilename();

							try {
								FileOutputStream fos = new FileOutputStream(localFile);
								channelSftp.cd(remoteFileName);
								channelSftp.get(remote, fos);
								fos.close();
								isPutFile = true;
							} catch (Exception e) {
								localFile.delete();
								isPutFile = false;
								lsStatus.put(lsEntry.getFilename(), "A001");
							}

							try {
								if (isPutFile) {
									// Remove File From Folder Server
									channelSftp.rm(remote);
								}
							} catch (Exception e) {
								lsStatus.put(lsEntry.getFilename(), "A002");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			closeSession(channelSftp);
			/*
			 * if (channelSftp != null) { channelSftp.disconnect(); }
			 */
		}

		return lsStatus;
	}

	public void sftpDeleteFile(String remoteFileName) throws Exception {
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();

			// Remove File From Folder Server
			channelSftp.rm(remoteFileName);

		} catch (Exception e) {
			throw e;
		} finally {
			closeSession(channelSftp);
		}
	}

	// ### Helper ###
	public ChannelSftp getSftpClient() throws Exception {
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			JSch jsch = new JSch();
			
			if(null == port) {
				session = jsch.getSession(user, host);
			} else {
				session = jsch.getSession(user, host, port);				
			}

			if (password != "") {
				session.setPassword(password);
			}
			Properties config = new Properties();

			// try connect to Host
			try {
				config.put("StrictHostKeyChecking", "no");
				if (password != "") {
					config.put("PreferredAuthentications", "password");
				}
				session.setConfig(config);
				session.connect(idleTime);
			} catch (JSchException e) {
				throw new Exception(String.format("Unknown host %s:%s", host, port, e.getStackTrace()));
			} catch (Exception e) {
				throw e;
			}

			// Open sftp and connect
			try {
				channel = session.openChannel("sftp");
				channel.connect(idleTime);
			} catch (JSchException e) {
				throw new Exception(String.format("Can not connect to channel ", host, port, e.getStackTrace()));
			} catch (Exception e) {
				throw e;
			}

			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(homePath);

			// cek koneksi ke sftp
			if (!channelSftp.isConnected()) {
				throw new Exception("Channel SFTP is disconnected");
			}

		} catch (Exception e) {
			throw e;
		}

		return channelSftp;
	}

	public void closeSession(ChannelSftp channel) {
		try {
			if (channel != null) {
				channel.getSession().disconnect();
				channel.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// inner class, generic extension filter
	public static class GenericExtFilter implements FilenameFilter {

		private String ext;

		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return (name.contains(ext));
		}
	}

	public int sftpGetCountDir() throws Exception {
		int result = 0;
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();
			Vector<LsEntry> entries = channelSftp.ls("*.*");
			result = entries.size();
		} catch (Exception e) {
			throw e;
		} finally {
			closeSession(channelSftp);
		}
		return result;
	}
	
	public Vector<LsEntry> getAllDir(String path) throws Exception {
		Vector<LsEntry> entries = null;
		ChannelSftp channelSftp = null;
		try {
			channelSftp = getSftpClient();
			if (path != "") {
				channelSftp.cd(path);
			}
			entries = channelSftp.ls("*");
		} catch (Exception e) {
			throw e;
		} finally {
			closeSession(channelSftp);
		}
		return entries;
	}

}
