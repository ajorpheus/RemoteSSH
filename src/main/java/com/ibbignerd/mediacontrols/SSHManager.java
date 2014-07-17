package com.ibbignerd.mediacontrols;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSHManager {
	private static final Logger LOGGER = Logger.getLogger(SSHManager.class.getName());
	private static final String FAILED_SENDING_COMMAND = "Failed sending command: ";
	private final int intConnectionPort;
	private final int intTimeOut;
	private JSch jschSSHChannel;
	private String strUserName;
	private String strConnectionIP;
	private String strPassword;
	private Session sessionConnection;

	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = 22;
		intTimeOut = 60000;
	}

	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
			int connectionPort) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = connectionPort;
		intTimeOut = 10000;
	}

	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
			int connectionPort, int timeOutMilliseconds) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);

		intConnectionPort = connectionPort;
		intTimeOut = timeOutMilliseconds;
	}

	private static boolean isAValidCommand(String command) {
		if (!command.contains("info") && (!command.contains("isRadio")) && (!command.contains("app"))
				&& (!command.contains("length")) && (!command.contains("elapsed"))) {
			return true;
		} else {
			return false;
		}
	}

	private void doCommonConstructorActions(String userName, String password, String connectionIP,
			String knownHostsFileName) {
		jschSSHChannel = new JSch();
		try {
			jschSSHChannel.setKnownHosts(knownHostsFileName);
		} catch (JSchException jschX) {
			logError(jschX.getMessage());
		}
		strUserName = userName;
		strPassword = password;
		strConnectionIP = connectionIP;
	}

	public String connect() {
		try {
			sessionConnection = jschSSHChannel.getSession(strUserName, strConnectionIP, intConnectionPort);

			sessionConnection.setPassword(strPassword);
			sessionConnection.setConfig("StrictHostKeyChecking", "no");
			sessionConnection.connect(intTimeOut);
			MediaControlsSSH.debugLogString("Successful connection!");
		} catch (JSchException jschX) {
			String jschXMessage = jschX.getMessage();
			MediaControlsSSH.debugLogString("Failed to connect with error: " + jschXMessage);
		}
		return "";
	}

	private String logError(String errorMessage) {
		if (errorMessage != null) {
			Integer connectionPort = Integer.valueOf(intConnectionPort);
			LOGGER.log(Level.SEVERE, "{0}:{1} - {2}", new Object[] { strConnectionIP, connectionPort, errorMessage });
		}
		return errorMessage;
	}

	public boolean isAlive() {
		return sessionConnection.isConnected();
	}

	public String sendCommand(String command) {
		if (isAValidCommand(command)) {
			MediaControlsSSH.debugLogString("Sending command: " + command);
		}
		StringBuilder outputBuffer = new StringBuilder();
		try {
			Channel channel = sessionConnection.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			channel.setOutputStream(System.out);
			channel.connect();
			InputStream commandOutput = channel.getInputStream();
			int readByte = commandOutput.read();
			while (readByte != -1) {
				outputBuffer.append((char)readByte);
				readByte = commandOutput.read();
			}
			channel.disconnect();
		} catch (JSchException ioX) {
			ioX.printStackTrace();
			MediaControlsSSH.debugLogString(FAILED_SENDING_COMMAND + command + ".\n With error: " + ioX.getMessage());
			return null;
		} catch (IOException ioX) {
			ioX.printStackTrace();
			MediaControlsSSH.debugLogString(FAILED_SENDING_COMMAND + command + ".\n With error: " + ioX.getMessage());
			return null;
		}
		return outputBuffer.toString();
	}

	public void close() {
		sessionConnection.disconnect();
		MediaControlsSSH.debugLogString("Disconnecting from host.");
	}
}
