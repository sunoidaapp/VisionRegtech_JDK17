package com.vision.util;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class SFTPFileTransfer {
	private static final String REMOTE_HOST = "10.164.205.92";
	private static final String USERNAME = "KumarA";
	private static final String PASSWORD = "Kdic2021@123";
	private static final int REMOTE_PORT = 22;
	private static final int SESSION_TIMEOUT = 10000;
	private static final int CHANNEL_TIMEOUT = 5000;

	public static void main(String args[]) {
		String localFile = "/home/javatpoint/local/random.txt";
		String remoteFile = "/home/javatpoint/remote/afile.txt";
		Session jschSession = null;
		try {/*
			JSch jsch = new JSch();
//			jsch.setKnownHosts("c\\:known_hosts"); 
			jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
			jschSession.setPassword(PASSWORD);
			jschSession.connect();
			Channel channel = jschSession.openChannel( "sftp" );
			System.out.println("Connecting....");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			String downloadDir = "/C:/Vision/buildlogs/";
			sftpChannel.cd(downloadDir);
			Vector<ChannelSftp.LsEntry> vtc=  sftpChannel.ls("*.*");
			System.out.println("File Size :"+vtc.size());
			Channel sftp = jschSession.openChannel("sftp");
			sftp.connect(CHANNEL_TIMEOUT);
			ChannelSftp channelSftp = (ChannelSftp) sftp;
			channelSftp.put(localFile, remoteFile);
			channelSftp.exit();*/
			
			
			
			

        	JSch jsch = new JSch();  
			//jsch.setKnownHosts("D:\\Vision_HOME\\");
			jsch.setKnownHosts("D\\:\\\\Vision_HOME\\\\");
			Session session = jsch.getSession( USERNAME, REMOTE_HOST, REMOTE_PORT);
			/*{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword( PASSWORD);
			}*/
			session.setPassword( PASSWORD);
			System.out.println("Checking Connection ......................");
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel( "sftp" ); 
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			String downloadDir = "/C:/Vision/buildlogs/";
			sftpChannel.cd(downloadDir);
			Vector<ChannelSftp.LsEntry> vtc=  sftpChannel.ls("*.*");
			System.out.println("File Size : "+vtc.size());
			sftpChannel.disconnect();
			session.disconnect();
        
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
		} finally {
			if (jschSession != null) {
				jschSession.disconnect();
			}
		}
		System.out.println("Done");
	}
	public void sftptConn() {
		String localFile = "/home/javatpoint/local/random.txt";
		String remoteFile = "/home/javatpoint/remote/afile.txt";
		Session jschSession = null;
		try {/*
			JSch jsch = new JSch();
//			jsch.setKnownHosts("c\\:known_hosts"); 
			jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
			jschSession.setPassword(PASSWORD);
			jschSession.connect();
			Channel channel = jschSession.openChannel( "sftp" );
			System.out.println("Connecting....");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			String downloadDir = "/C:/Vision/buildlogs/";
			sftpChannel.cd(downloadDir);
			Vector<ChannelSftp.LsEntry> vtc=  sftpChannel.ls("*.*");
			System.out.println("File Size :"+vtc.size());
			Channel sftp = jschSession.openChannel("sftp");
			sftp.connect(CHANNEL_TIMEOUT);
			ChannelSftp channelSftp = (ChannelSftp) sftp;
			channelSftp.put(localFile, remoteFile);
			channelSftp.exit();*/
			
			
			
			

        	JSch jsch = new JSch();  
			//jsch.setKnownHosts("D:\\Vision_HOME\\");
			//jsch.setKnownHosts("D\\:\\\\Vision_HOME\\\\");
			Session session = jsch.getSession( USERNAME, REMOTE_HOST, REMOTE_PORT);
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword( PASSWORD);
			}
			System.out.println("Checking Connection ......................");
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel( "sftp" ); 
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			String downloadDir = "/C:/Vision/buildlogs/";
			sftpChannel.cd(downloadDir);
			Vector<ChannelSftp.LsEntry> vtc=  sftpChannel.ls("*.*");
			System.out.println("File Size : "+vtc.size());
			sftpChannel.disconnect();
			session.disconnect();
        
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
		} finally {
			if (jschSession != null) {
				jschSession.disconnect();
			}
		}
		System.out.println("Done");
	
	}
	 public static class MyUserInfo implements UserInfo{
		    public String getPassword(){ return PASSWORD; }
		    public boolean promptYesNo(String str){
		      return false;
		    }
		    public String getPassphrase(){ return null; }
		    public boolean promptPassphrase(String message){ return true; }
		    public boolean promptPassword(String message){ return false; }
		    
		    public void showMessage(String message){
		      return;
		    }
		}
}