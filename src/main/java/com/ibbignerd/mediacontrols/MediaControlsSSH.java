package com.ibbignerd.mediacontrols;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaControlsSSH extends JFrame {
	private static final String version = "Version: 0.6.0";
	private static final int windowWidth = 258;
	private static final String thinChars = "[^iIlt1\\(\\)\\.,']";
	private static DeveloperConsole devConsole;
	private static Update updatePane;
	SSHManager instance;
	Timer timer;
	private String host;
	private String password = "alpine";
	private String[] songInfo;
	private int port = 22;
	private String bundleID;
	private ArrayList<String> updateText = new ArrayList();
	private JPasswordField PassVal;
	private JLabel album;
	private JLabel artist;
	private JButton buttonConnect;
	private JButton buttonDisconnect;
	private JButton buttonNext;
	private JButton buttonPanDislike;
	private JButton buttonPanLike;
	private JButton buttonPlayPause;
	private JButton buttonPrevious;
	private JButton buttonRepeat;
	private JButton buttonShuffle;
	private JButton buttonVolDown;
	private JButton buttonVolMax;
	private JButton buttonVolMute;
	private JButton buttonVolUp;
	private JButton buttoniTunesHate;
	private JButton buttoniTunesLike;
	private JButton buttoniTunesWish;
	private JTextField hostName;
	private JLabel labelAlbum;
	private JLabel labelArtist;
	private JLabel labelHostName;
	private JLabel labelPass;
	private JLabel labelTimeLeft;
	private JLabel labelTitle;
	private JLabel labelVersionNumber;
	private JMenuItem launchDownCast;
	private JMenuItem launchMusic;
	private JMenuItem launchPandora;
	private JMenuItem launchSpotify;
	private JMenuBar menuBar;
	private JMenu menuLaunch;
	private JMenu menuOptions;
	private JMenuItem optionsDebug;
	private JMenuItem optionsUpdate;
	private JPanel panelConnection;
	private JPanel panelFooter;
	private JPanel panelMain;
	private JPanel panelMediaControls;
	private JPanel panelPandora;
	private JPanel panelSongInfo;
	private JPanel panelVolume;
	private JPanel paneliTunesRadio;
	private JProgressBar progressSong;
	private JLabel title;

	public MediaControlsSSH() {
		initComponents();
		layout(true, false, false, false, false, false, false, false, 200);
		devConsole = new DeveloperConsole();
		updatePane = new Update();
		this.labelVersionNumber.setText("Version: 0.6.0");
	}

	public static void main(String[] args) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (DeveloperConsole.NIMBUS.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(MediaControlsSSH.class.getName()).log(Level.SEVERE, null, e);
		} catch (UnsupportedLookAndFeelException e) {
			Logger.getLogger(MediaControlsSSH.class.getName()).log(Level.SEVERE, null, e);
		} catch (InstantiationException e) {
			Logger.getLogger(MediaControlsSSH.class.getName()).log(Level.SEVERE, null, e);
		} catch (IllegalAccessException e) {
			Logger.getLogger(MediaControlsSSH.class.getName()).log(Level.SEVERE, null, e);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MediaControlsSSH().setVisible(true);
			}
		});
	}

	public static void updateLogString(String message) {
		updatePane.getUpdateLog().setText(updatePane.getUpdateLog().getText() + "\n" + message);
	}

	public static void debugLogString(String message) {
		devConsole.getDebugOutput().setText(devConsole.getDebugOutput().getText() + "\n\n" + message);
	}

	public static void rawToJpeg(byte[] bytes, File outputFile) {
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
			ImageIO.write(img, "jpg", outputFile);
		} catch (IOException e) {
		}
	}

	private static int textWidth(String str) {
		return str.length() - str.replaceAll("[^iIlt1\\(\\)\\.,']", "").length() / 2;
	}

	public static String ellipsis(String text, int max) {
		if (textWidth(text) < max) {
			return text;
		}
		int end = text.lastIndexOf(' ', max - 3);
		if (end == -1) {
			return text.substring(0, max - 3) + "...";
		}
		int newEnd = end;
		do {
			end = newEnd;
			newEnd = text.indexOf(' ', end + 1);
			if (newEnd == -1) {
				newEnd = text.length();
			}
		} while (textWidth(text.substring(0, newEnd) + "...") < max);
		return text.substring(0, end) + "...";
	}

	public static Document parse(InputStream is) {
		Document ret = null;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setValidating(false);
			domFactory.setNamespaceAware(false);
			DocumentBuilder builder = domFactory.newDocumentBuilder();

			ret = builder.parse(is);
		} catch (ParserConfigurationException e) {
			debugLogString("Unable to load XML: " + e);
			System.out.println("unable to load XML: " + e);
		} catch (SAXException e) {
			debugLogString("Unable to load XML: " + e);
			System.out.println("unable to load XML: " + e);
		} catch (IOException e) {
			debugLogString("Unable to load XML: " + e);
			System.out.println("unable to load XML: " + e);
		}
		return ret;
	}

	public static SSHManager createSSHManager(String userName, String password, String connectionIP,
			String knownHostsFileName) {
		return new SSHManager(userName, password, connectionIP, knownHostsFileName);
	}

	public static SSHManager createSSHManager(String userName, String password, String connectionIP,
			String knownHostsFileName, int connectionPort) {
		return new SSHManager(userName, password, connectionIP, knownHostsFileName, connectionPort);
	}

	public static SSHManager createSSHManager(String userName, String password, String connectionIP,
			String knownHostsFileName, int connectionPort, int timeOutMilliseconds) {
		return new SSHManager(userName, password, connectionIP, knownHostsFileName, connectionPort, timeOutMilliseconds);
	}

	private void initComponents() {
		this.panelMain = new JPanel();
		this.panelConnection = new JPanel();
		this.hostName = new JTextField();
		this.labelHostName = new JLabel();
		this.labelPass = new JLabel();
		this.PassVal = new JPasswordField();
		this.buttonConnect = new JButton();
		this.panelSongInfo = new JPanel();
		this.labelTitle = new JLabel();
		this.labelArtist = new JLabel();
		this.labelAlbum = new JLabel();
		this.progressSong = new JProgressBar();
		this.labelTimeLeft = new JLabel();
		this.title = new JLabel();
		this.artist = new JLabel();
		this.album = new JLabel();
		this.panelMediaControls = new JPanel();
		this.buttonNext = new JButton();
		this.buttonRepeat = new JButton();
		this.buttonPlayPause = new JButton();
		this.buttonPrevious = new JButton();
		this.buttonShuffle = new JButton();
		this.panelVolume = new JPanel();
		this.buttonVolMute = new JButton();
		this.buttonVolDown = new JButton();
		this.buttonVolUp = new JButton();
		this.buttonVolMax = new JButton();
		this.panelPandora = new JPanel();
		this.buttonPanDislike = new JButton();
		this.buttonPanLike = new JButton();
		this.paneliTunesRadio = new JPanel();
		this.buttoniTunesHate = new JButton();
		this.buttoniTunesWish = new JButton();
		this.buttoniTunesLike = new JButton();
		this.panelFooter = new JPanel();
		this.buttonDisconnect = new JButton();
		this.labelVersionNumber = new JLabel();
		this.menuBar = new JMenuBar();
		this.menuLaunch = new JMenu();
		this.launchMusic = new JMenuItem();
		this.launchPandora = new JMenuItem();
		this.launchSpotify = new JMenuItem();
		this.launchDownCast = new JMenuItem();
		this.menuOptions = new JMenu();
		this.optionsDebug = new JMenuItem();
		this.optionsUpdate = new JMenuItem();

		setDefaultCloseOperation(3);
		setTitle("RemoteMusic (OSX)");
		setResizable(false);

		this.panelMain.setCursor(new Cursor(0));

		this.panelConnection.setBorder(BorderFactory.createTitledBorder("Connection"));

		this.hostName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.hostNameActionPerformed(evt);
			}
		});
		this.labelHostName.setText("Host Name:");

		this.labelPass.setText("Password:");

		this.PassVal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.PassValActionPerformed(evt);
			}
		});
		this.PassVal.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				MediaControlsSSH.this.PassValKeyPressed(evt);
			}
		});
		this.buttonConnect.setText("Connect To Device");
		this.buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonConnectActionPerformed(evt);
			}
		});
		GroupLayout panelConnectionLayout = new GroupLayout(this.panelConnection);
		this.panelConnection.setLayout(panelConnectionLayout);
		panelConnectionLayout.setHorizontalGroup(panelConnectionLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING).addGroup(
			panelConnectionLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					panelConnectionLayout
						.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
						.addGroup(
							GroupLayout.Alignment.LEADING,
							panelConnectionLayout.createSequentialGroup().addComponent(this.labelPass)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.PassVal))
						.addGroup(
							GroupLayout.Alignment.LEADING,
							panelConnectionLayout.createSequentialGroup().addComponent(this.labelHostName)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(this.hostName, -2, 125, -2))
						.addComponent(this.buttonConnect, -1, -1, 32767)).addContainerGap(-1, 32767)));

		panelConnectionLayout.setVerticalGroup(panelConnectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelConnectionLayout
					.createSequentialGroup()
					.addGroup(
						panelConnectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(this.hostName).addComponent(this.labelHostName, -1, -1, 32767))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						panelConnectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(this.PassVal, -2, -1, -2).addComponent(this.labelPass))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.buttonConnect)));

		this.panelSongInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Song Info")));

		this.labelTitle.setText("Title:");

		this.labelArtist.setText("Artist:");

		this.labelAlbum.setText("Album:");

		this.labelTimeLeft.setText("0:00");

		this.title.setText("(null)");

		this.artist.setText("(null)");

		this.album.setText("(null)");

		GroupLayout panelSongInfoLayout = new GroupLayout(this.panelSongInfo);
		this.panelSongInfo.setLayout(panelSongInfoLayout);
		panelSongInfoLayout.setHorizontalGroup(panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				GroupLayout.Alignment.TRAILING,
				panelSongInfoLayout
					.createSequentialGroup()
					.addGroup(
						panelSongInfoLayout
							.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(
								panelSongInfoLayout
									.createSequentialGroup()
									.addGroup(
										panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
											.addComponent(this.labelAlbum).addComponent(this.labelTitle)
											.addComponent(this.labelArtist))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(
										panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
											.addComponent(this.title, -1, -1, 32767)
											.addComponent(this.artist, -1, -1, 32767)
											.addComponent(this.album, -1, -1, 32767)))
							.addGroup(
								panelSongInfoLayout.createSequentialGroup()
									.addComponent(this.progressSong, -2, 174, -2)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767)
									.addComponent(this.labelTimeLeft))).addGap(14, 14, 14)));

		panelSongInfoLayout.setVerticalGroup(panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelSongInfoLayout
					.createSequentialGroup()
					.addGroup(
						panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(this.labelTitle).addComponent(this.title, -2, 14, -2))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(this.labelArtist, -2, 14, -2).addComponent(this.artist))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(this.labelAlbum).addComponent(this.album))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767)
					.addGroup(
						panelSongInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(this.progressSong, GroupLayout.Alignment.TRAILING, -2, -1, -2)
							.addComponent(this.labelTimeLeft, GroupLayout.Alignment.TRAILING))));

		this.panelMediaControls.setBorder(BorderFactory.createTitledBorder("Media Controls"));

		this.buttonNext.setText(">>");
		this.buttonNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonNextActionPerformed(evt);
			}
		});
		this.buttonRepeat.setText("Repeat");
		this.buttonRepeat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonRepeatActionPerformed(evt);
			}
		});
		this.buttonPlayPause.setText("Play/Pause");
		this.buttonPlayPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonPlayPauseActionPerformed(evt);
			}
		});
		this.buttonPrevious.setText("<<");
		this.buttonPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonPreviousActionPerformed(evt);
			}
		});
		this.buttonShuffle.setText("Shuffle");
		this.buttonShuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonShuffleActionPerformed(evt);
			}
		});
		GroupLayout panelMediaControlsLayout = new GroupLayout(this.panelMediaControls);
		this.panelMediaControls.setLayout(panelMediaControlsLayout);
		panelMediaControlsLayout.setHorizontalGroup(panelMediaControlsLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING)
			.addGroup(
				panelMediaControlsLayout
					.createSequentialGroup()
					.addContainerGap(-1, 32767)
					.addGroup(
						panelMediaControlsLayout
							.createParallelGroup(GroupLayout.Alignment.LEADING, false)
							.addGroup(
								panelMediaControlsLayout.createSequentialGroup().addComponent(this.buttonShuffle)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767)
									.addComponent(this.buttonRepeat))
							.addGroup(
								panelMediaControlsLayout.createSequentialGroup().addComponent(this.buttonPrevious)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(this.buttonPlayPause)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(this.buttonNext)))));

		panelMediaControlsLayout.setVerticalGroup(panelMediaControlsLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING).addGroup(
			panelMediaControlsLayout
				.createSequentialGroup()
				.addGroup(
					panelMediaControlsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.buttonShuffle).addComponent(this.buttonRepeat))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					panelMediaControlsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.buttonPrevious).addComponent(this.buttonPlayPause)
						.addComponent(this.buttonNext)).addContainerGap(-1, 32767)));

		this.panelVolume.setBorder(BorderFactory.createTitledBorder("Volume"));

		this.buttonVolMute.setText("Mute");
		this.buttonVolMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonVolMuteActionPerformed(evt);
			}
		});
		this.buttonVolDown.setText("-");
		this.buttonVolDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonVolDownActionPerformed(evt);
			}
		});
		this.buttonVolUp.setText("+");
		this.buttonVolUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonVolUpActionPerformed(evt);
			}
		});
		this.buttonVolMax.setText("Max");
		this.buttonVolMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonVolMaxActionPerformed(evt);
			}
		});
		GroupLayout panelVolumeLayout = new GroupLayout(this.panelVolume);
		this.panelVolume.setLayout(panelVolumeLayout);
		panelVolumeLayout.setHorizontalGroup(panelVolumeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelVolumeLayout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.buttonVolMute)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.buttonVolDown)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.buttonVolUp)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(this.buttonVolMax, -2, 53, -2)));

		panelVolumeLayout.setVerticalGroup(panelVolumeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelVolumeLayout
					.createSequentialGroup()
					.addGroup(
						panelVolumeLayout
							.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(this.buttonVolMute, GroupLayout.Alignment.TRAILING)
							.addComponent(this.buttonVolDown, GroupLayout.Alignment.TRAILING)
							.addGroup(
								GroupLayout.Alignment.TRAILING,
								panelVolumeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(this.buttonVolUp).addComponent(this.buttonVolMax)))
					.addContainerGap(-1, 32767)));

		this.panelPandora.setBorder(BorderFactory.createTitledBorder("Pandora Controls"));

		this.buttonPanDislike.setText("Dislike");
		this.buttonPanDislike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonPanDislikeActionPerformed(evt);
			}
		});
		this.buttonPanLike.setText("Like");
		this.buttonPanLike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonPanLikeActionPerformed(evt);
			}
		});
		GroupLayout panelPandoraLayout = new GroupLayout(this.panelPandora);
		this.panelPandora.setLayout(panelPandoraLayout);
		panelPandoraLayout.setHorizontalGroup(panelPandoraLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelPandoraLayout.createSequentialGroup().addContainerGap()
					.addComponent(this.buttonPanDislike, -2, 86, -2).addGap(18, 18, 18)
					.addComponent(this.buttonPanLike, -2, 81, -2).addContainerGap(-1, 32767)));

		panelPandoraLayout.setVerticalGroup(panelPandoraLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelPandoraLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(this.buttonPanDislike).addComponent(this.buttonPanLike)));

		this.paneliTunesRadio.setBorder(BorderFactory.createTitledBorder("iTunes Radio Controls"));

		this.buttoniTunesHate.setText("Hate");
		this.buttoniTunesHate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttoniTunesHateActionPerformed(evt);
			}
		});
		this.buttoniTunesWish.setText("Wish");
		this.buttoniTunesWish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttoniTunesWishActionPerformed(evt);
			}
		});
		this.buttoniTunesLike.setText("Like");
		this.buttoniTunesLike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttoniTunesLikeActionPerformed(evt);
			}
		});
		GroupLayout paneliTunesRadioLayout = new GroupLayout(this.paneliTunesRadio);
		this.paneliTunesRadio.setLayout(paneliTunesRadioLayout);
		paneliTunesRadioLayout.setHorizontalGroup(paneliTunesRadioLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING).addGroup(
			paneliTunesRadioLayout.createSequentialGroup().addContainerGap(-1, 32767)
				.addComponent(this.buttoniTunesHate, -2, 61, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(this.buttoniTunesWish, -2, 61, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(this.buttoniTunesLike, -2, 61, -2)));

		paneliTunesRadioLayout.setVerticalGroup(paneliTunesRadioLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING).addGroup(
			paneliTunesRadioLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(this.buttoniTunesHate).addComponent(this.buttoniTunesWish)
				.addComponent(this.buttoniTunesLike)));

		this.buttonDisconnect.setText("Disconnect");
		this.buttonDisconnect.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				MediaControlsSSH.this.buttonDisconnectMouseClicked(evt);
			}
		});
		this.buttonDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.buttonDisconnectActionPerformed(evt);
			}
		});
		this.labelVersionNumber.setForeground(new Color(51, 51, 255));
		this.labelVersionNumber.setText("Version: 0.6.0");
		this.labelVersionNumber.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				MediaControlsSSH.this.labelVersionNumberMouseClicked(evt);
			}
		});
		GroupLayout panelFooterLayout = new GroupLayout(this.panelFooter);
		this.panelFooter.setLayout(panelFooterLayout);
		panelFooterLayout.setHorizontalGroup(panelFooterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				GroupLayout.Alignment.TRAILING,
				panelFooterLayout.createSequentialGroup().addContainerGap().addComponent(this.labelVersionNumber)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 38, 32767)
					.addComponent(this.buttonDisconnect).addContainerGap()));

		panelFooterLayout.setVerticalGroup(panelFooterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				panelFooterLayout
					.createSequentialGroup()
					.addGroup(
						panelFooterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(this.buttonDisconnect).addComponent(this.labelVersionNumber))
					.addGap(0, 5, 32767)));

		GroupLayout panelMainLayout = new GroupLayout(this.panelMain);
		this.panelMain.setLayout(panelMainLayout);
		panelMainLayout.setHorizontalGroup(panelMainLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			panelMainLayout
				.createSequentialGroup()
				.addContainerGap(-1, 32767)
				.addGroup(
					panelMainLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.panelFooter, -2, -1, -2)
						.addComponent(this.panelConnection, -1, -1, 32767)
						.addGroup(
							panelMainLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
								.addComponent(this.paneliTunesRadio, GroupLayout.Alignment.LEADING, -2, 0, 32767)
								.addComponent(this.panelPandora, GroupLayout.Alignment.LEADING, -1, -1, 32767)
								.addComponent(this.panelVolume, GroupLayout.Alignment.LEADING, -2, 0, 32767)
								.addComponent(this.panelMediaControls, GroupLayout.Alignment.LEADING, -1, -1, 32767)
								.addComponent(this.panelSongInfo, GroupLayout.Alignment.LEADING, -2, 0, 32767)))));

		panelMainLayout.setVerticalGroup(panelMainLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			panelMainLayout.createSequentialGroup().addContainerGap().addComponent(this.panelConnection, -2, -1, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.panelSongInfo, -2, -1, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.panelMediaControls, -2, -1, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.panelVolume, -2, -1, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.panelPandora, -2, -1, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.paneliTunesRadio, -2, -1, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.panelFooter, -2, -1, -2)
				.addContainerGap()));

		this.menuLaunch.setText("Launch");

		this.launchMusic.setText("Music");
		this.launchMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.launchMusicActionPerformed(evt);
			}
		});
		this.menuLaunch.add(this.launchMusic);

		this.launchPandora.setText("Pandora");
		this.launchPandora.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.launchPandoraActionPerformed(evt);
			}
		});
		this.menuLaunch.add(this.launchPandora);

		this.launchSpotify.setText("Spotify");
		this.launchSpotify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.launchSpotifyActionPerformed(evt);
			}
		});
		this.menuLaunch.add(this.launchSpotify);

		this.launchDownCast.setText("DownCast");
		this.launchDownCast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.launchDownCastActionPerformed(evt);
			}
		});
		this.menuLaunch.add(this.launchDownCast);

		this.menuBar.add(this.menuLaunch);

		this.menuOptions.setText("Options");

		this.optionsDebug.setText("Debug Console");
		this.optionsDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.optionsDebugActionPerformed(evt);
			}
		});
		this.menuOptions.add(this.optionsDebug);

		this.optionsUpdate.setText("Check for Update");
		this.optionsUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MediaControlsSSH.this.optionsUpdateActionPerformed(evt);
			}
		});
		this.menuOptions.add(this.optionsUpdate);

		this.menuBar.add(this.menuOptions);

		setJMenuBar(this.menuBar);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
			this.panelMain, -2, -1, -2));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.panelMain,
			-2, -1, -2));

		setBounds(0, 0, 243, 575);
	}

	private void hostNameActionPerformed(ActionEvent evt) {
	}

	private void PassValActionPerformed(ActionEvent evt) {
	}

	private void buttonDisconnectActionPerformed(ActionEvent evt) {
		close();
	}

	private void buttonDisconnectMouseClicked(MouseEvent evt) {
	}

	private void buttonPlayPauseActionPerformed(ActionEvent evt) {
		sendCommand("media p");
	}

	private void buttonShuffleActionPerformed(ActionEvent evt) {
		sendCommand("media shuffle");
	}

	private void buttonRepeatActionPerformed(ActionEvent evt) {
		sendCommand("media repeat");
	}

	private void buttonPreviousActionPerformed(ActionEvent evt) {
		sendCommand("media prev");
	}

	private void buttonNextActionPerformed(ActionEvent evt) {
		sendCommand("media next");
	}

	private void buttonVolMuteActionPerformed(ActionEvent evt) {
		sendCommand("media volmin");
	}

	private void buttonVolDownActionPerformed(ActionEvent evt) {
		sendCommand("media vol-");
	}

	private void buttonVolUpActionPerformed(ActionEvent evt) {
		sendCommand("media vol+");
	}

	private void buttonVolMaxActionPerformed(ActionEvent evt) {
		sendCommand("media volmax");
	}

	private void buttonConnectActionPerformed(ActionEvent evt) {
		startConnection();
	}

	private void buttonPanLikeActionPerformed(ActionEvent evt) {
		sendCommand("activator send Zaid.PandoraRateUp");
	}

	private void buttonPanDislikeActionPerformed(ActionEvent evt) {
		sendCommand("activator send Zaid.PandoraRateDown");
	}

	private void PassValKeyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == 10) {
			startConnection();
		}
	}

	private void labelVersionNumberMouseClicked(MouseEvent evt) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Info().setVisible(true);
			}
		});
	}

	private void buttoniTunesWishActionPerformed(ActionEvent evt) {
		sendCommand("media wish");
	}

	private void buttoniTunesHateActionPerformed(ActionEvent evt) {
		sendCommand("media hate");
	}

	private void buttoniTunesLikeActionPerformed(ActionEvent evt) {
		sendCommand("media like");
	}

	private void launchMusicActionPerformed(ActionEvent evt) {
		sendCommand("media launchMusic");
	}

	private void launchPandoraActionPerformed(ActionEvent evt) {
		sendCommand("media launchPan");
	}

	private void launchSpotifyActionPerformed(ActionEvent evt) {
		sendCommand("media launchSpot");
	}

	private void launchDownCastActionPerformed(ActionEvent evt) {
		sendCommand("media launchDC");
	}

	private void optionsDebugActionPerformed(ActionEvent evt) {
		devConsole.setVisible(!devConsole.isVisible());
	}

	private void optionsUpdateActionPerformed(ActionEvent evt) {
		updateCheck();
	}

	private void layout(boolean connection, boolean songInfo, boolean shuffle, boolean mediaControls, boolean volume,
			boolean pandora, boolean iTunes, boolean dc, int height) {
		this.panelConnection.setVisible(connection);
		this.menuBar.setVisible(!connection);
		this.menuLaunch.setVisible(!connection);
		this.panelSongInfo.setVisible(songInfo);
		this.buttonShuffle.setVisible(shuffle);
		this.buttonRepeat.setVisible(shuffle);
		this.panelMediaControls.setVisible(mediaControls);
		this.panelVolume.setVisible(volume);
		this.panelPandora.setVisible(pandora);
		this.paneliTunesRadio.setVisible(iTunes);
		this.buttonDisconnect.setVisible(dc);
		if ((pandora) || (iTunes)) {
			this.buttonPrevious.setEnabled(false);
		} else {
			this.buttonPrevious.setEnabled(true);
		}
		Rectangle oldPos = getBounds();
		setBounds(oldPos.x, oldPos.y, 258, height);
	}

	private boolean setUpConnectInfo() {
		if (this.hostName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Host name is empty!");
			return false;
		}
		if (this.hostName.getText().contains(":")) {
			String[] tempHost = this.hostName.getText().split(":");
			this.host = tempHost[0];
			try {
				this.port = Integer.parseInt(tempHost[1]);
			} catch (NumberFormatException ex) {
				debugLogString("Port must be in a number format!");
				JOptionPane.showMessageDialog(this.rootPane, "Port must be in a number format!");
				return false;
			}
		} else {
			this.host = this.hostName.getText();
		}
		this.password = new String(this.PassVal.getPassword());
		return true;
	}

	private void startConnection() {
		if (!setUpConnectInfo()) {
			debugLogString("Failed to set up connection info!");
			return;
		}
		String errorMessage = connect();
		System.out.println("Error Message: " + errorMessage);
		if (errorMessage.contains("timeout")) {
			JOptionPane.showMessageDialog(null, "Connection timed out!");
			debugLogString("Connection Error: Connection timed out!");
		} else if (errorMessage.contains("Auth fail")) {
			JOptionPane.showMessageDialog(null, "Incorrect password!");
			debugLogString("Connection Error: Incorrect password!");
		} else if (errorMessage.contains("UnknownHost")) {
			JOptionPane.showMessageDialog(null, "Unknown Host!");
			debugLogString("Connection Error: Unknown Host!");
		} else if (errorMessage.contains("Connection refused")) {
			JOptionPane.showMessageDialog(null, "Connection refused!");
			debugLogString("Connection Error: Connection refused! ");
		}
	}

	private String connect() {
		debugLogString("Starting mobile connection with hostname: " + this.host + " and port " + this.port + ".");
		this.instance = createSSHManager("mobile", this.password, this.host, "", this.port);
		String errorMessage = this.instance.connect();
		if (!errorMessage.equals("")) {
			System.out.println(errorMessage);
			debugLogString("Error: " + errorMessage);
		} else {
			startTimer();
		}
		return errorMessage;
	}

	private void updateCheck() {
		updatePane.getUpdateLog().setText("");
		debugLogString("Checking for updates...");
		try {
			URL url = new URL("http://void-technologies.com/nathan/RemoteMedia/update.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = in.readLine();
			int i = 0;
			while (line != null) {
				this.updateText.add(i, line);
				i++;
				line = in.readLine();
			}
		} catch (IOException ex) {
			debugLogString(ex.toString());
		}
		if (!((String)this.updateText.get(1)).equalsIgnoreCase("Version: 0.6.0")) {
			debugLogString("There is an update available");
			try {
				updatePane.updateURL = new URL((String)this.updateText.get(0));
				System.out.println(updatePane.updateURL);
			} catch (MalformedURLException ex) {
				debugLogString("Improper URL format!");
				Logger.getLogger(MediaControlsSSH.class.getName()).log(Level.SEVERE, null, ex);
			}
			for (int j = 1; j < this.updateText.size(); j++) {
				System.out.println((String)this.updateText.get(j));
				updateLogString((String)this.updateText.get(j));
			}
			updatePane.setVisible(!updatePane.isVisible());
		} else {
			JOptionPane.showMessageDialog(null, "RemoteMedia is up to date!");
			debugLogString("RemoteMedia is up to date!");
		}
	}

	private void startTimer() {
		System.out.println("Timer Started");
		this.timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				if (MediaControlsSSH.this.isAlive()) {
					String text = MediaControlsSSH.this.sendCommand("media info");
					if (text != null) {
						MediaControlsSSH.this.parseInfo("songinfo", text);
					} else {
						MediaControlsSSH.this.close();
					}
					MediaControlsSSH.this.title.setText(MediaControlsSSH.ellipsis(
						MediaControlsSSH.this.songInfo[3].substring(1), 27));
					MediaControlsSSH.this.album.setText(MediaControlsSSH.ellipsis(
						MediaControlsSSH.this.songInfo[2].substring(1), 27));
					MediaControlsSSH.this.artist.setText(MediaControlsSSH.ellipsis(
						MediaControlsSSH.this.songInfo[1].substring(1), 27));

					String isRadio = MediaControlsSSH.this.sendCommand("media isRadio");
					MediaControlsSSH.this.parseInfo("parsebundle", "com.apple.Music");
					if (!MediaControlsSSH.this.bundleID.contains("(null)")) {
						if (MediaControlsSSH.this.bundleID.contains("com.apple.Music")) {
							if (isRadio.contains("1")) {
								MediaControlsSSH.this.layout(false, true, false, true, true, false, true, true, 430);
							} else {
								MediaControlsSSH.this.layout(false, true, true, true, true, false, false, true, 390);
							}
						} else if (MediaControlsSSH.this.bundleID.contains("com.pandora")) {
							MediaControlsSSH.this.layout(false, true, false, true, true, true, false, true, 470);
						}
					} else {
						MediaControlsSSH.this.layout(false, true, false, true, true, false, false, true, 390);
					}
					String length = MediaControlsSSH.this.sendCommand("media length");
					String time = MediaControlsSSH.this.sendCommand("media elapsed");
					if (!time.contains("nan")) {
						double total = Double.parseDouble(length);
						double elapsed = Double.parseDouble(time);
						int percent = (int)(elapsed / total * 100.0D);
						long sec = (long)(total - elapsed);
						long seconds = TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.SECONDS.toMinutes(sec) * 60L;
						long mins = TimeUnit.SECONDS.toMinutes(sec);
						if (seconds >= 10L) {
							MediaControlsSSH.this.labelTimeLeft.setText(mins + ":" + seconds);
						} else {
							MediaControlsSSH.this.labelTimeLeft.setText(mins + ":0" + seconds);
						}
						MediaControlsSSH.this.progressSong.setValue(percent);
					}
			}
			}
		};
		this.timer.scheduleAtFixedRate(task, 0L, 1000L);
	}

	private boolean isAlive() {
		if (this.instance.isAlive()) {
			connectionPanel(false);
			return true;
		}
		connectionPanel(true);
		return false;
	}

	private void parseInfo(String type, String in) {
		if (type.equals("songinfo")) {
			in = in.replaceAll("Artist", "");
			in = in.replaceAll("Album", "");
			in = in.replaceAll("Title", "");
			in = in.replaceAll("Duration", "");
			this.songInfo = in.split(":");

		} else if (type.equals("parsebundle")) {
			if (!in.contains("(null)")) {
				try {
					this.bundleID = in;
				} catch (ArrayIndexOutOfBoundsException e) {
					debugLogString("Array Index Out of Bounds!");
					e.printStackTrace();
				}
			} else {
				this.bundleID = "(null)";
			}
		}
	}

	private String sendCommand(String command) {
		if (this.instance == null) {
			return "no session";
		}
		return this.instance.sendCommand(command);
	}

	private void connectionPanel(boolean in) {
		this.hostName.setEnabled(in);
		this.PassVal.setEnabled(in);
		this.buttonConnect.setEnabled(in);
	}

	private void close() {
		if (this.instance == null) {
			JOptionPane.showMessageDialog(null, "No current session to close!");
			return;
		}
		this.timer.cancel();
		this.instance.close();
		isAlive();
		this.title.setText("(null)");
		this.album.setText("(null)");
		this.artist.setText("(null)");
		layout(true, false, false, false, false, false, false, false, 200);
	}
}
