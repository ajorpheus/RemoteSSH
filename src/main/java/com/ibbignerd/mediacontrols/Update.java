package com.ibbignerd.mediacontrols;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Update extends JFrame {
	public URL updateURL;
	private JButton buttonCancel;
	private JButton buttonUpdate;
	private JScrollPane jScrollPane1;
	private JLabel labelUpdateMessage;
	private JLabel labelUpdateQuestion;
	private JTextArea updateLog;

	public Update() {
		initComponents();
	}

	public static void main(String[] args) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (DeveloperConsole.NIMBUS.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Update().setVisible(true);
			}
		});
	}

	private void initComponents() {
		this.labelUpdateMessage = new JLabel();
		this.jScrollPane1 = new JScrollPane();
		this.updateLog = new JTextArea();
		this.labelUpdateQuestion = new JLabel();
		this.buttonCancel = new JButton();
		this.buttonUpdate = new JButton();

		setDefaultCloseOperation(2);
		setTitle("RemoteMedia Update!");
		setResizable(false);

		this.labelUpdateMessage.setText("Looks like there is an update to RemoteMedia!");

		this.jScrollPane1.setHorizontalScrollBarPolicy(31);
		this.jScrollPane1.setToolTipText("");
		this.jScrollPane1.setAutoscrolls(true);

		this.updateLog.setEditable(false);
		this.updateLog.setColumns(20);
		this.updateLog.setLineWrap(true);
		this.updateLog.setRows(5);
		this.updateLog.setToolTipText("");
		this.jScrollPane1.setViewportView(this.updateLog);

		this.labelUpdateQuestion.setText("Would you like to update?");

		this.buttonCancel.setText("Cancel");
		this.buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Update.this.buttonCancelActionPerformed(evt);
			}
		});
		this.buttonUpdate.setText("Update!");
		this.buttonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Update.this.buttonUpdateActionPerformed(evt);
			}
		});
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			layout
				.createSequentialGroup()
				.addGroup(
					layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
							GroupLayout.Alignment.TRAILING,
							layout.createSequentialGroup().addContainerGap(156, 32767)
								.addComponent(this.labelUpdateQuestion).addGap(18, 18, 18)
								.addComponent(this.buttonUpdate)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(this.buttonCancel))
						.addGroup(
							layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.labelUpdateMessage)
								.addGap(0, 0, 32767))
						.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1)))
				.addContainerGap()));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			layout
				.createSequentialGroup()
				.addContainerGap()
				.addComponent(this.labelUpdateMessage)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.jScrollPane1, -2, 105, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.buttonCancel)
						.addComponent(this.buttonUpdate).addComponent(this.labelUpdateQuestion))
				.addContainerGap(-1, 32767)));

		pack();
		setLocationRelativeTo(null);
	}

	private void buttonUpdateActionPerformed(ActionEvent evt) {
		try {
			System.out.println("Update media.updateURL: " + this.updateURL);
			openWebpage(this.updateURL.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		dispose();
	}

	private void buttonCancelActionPerformed(ActionEvent evt) {
		dispose();
	}

	public JTextArea getUpdateLog() {
		return this.updateLog;
	}

	public void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if ((desktop != null) && (desktop.isSupported(Desktop.Action.BROWSE))) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

/*
 * Location: C:\Users\jindala\Desktop\main.jar Qualified Name: com.ibbignerd.mediacontrols.Update JD-Core Version:
 * 0.7.0.1
 */
