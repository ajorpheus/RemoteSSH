package com.ibbignerd.MediaControlsSSH;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeveloperConsole_UI extends JFrame {
	private JLabel labelDebugTitle;
	private JScrollPane scrollPane;
	private JTextArea textAreaDebug;

	public DeveloperConsole_UI() {
		initComponents();
	}

	public static void main(String[] args) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException e) {
		} catch (UnsupportedLookAndFeelException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new DeveloperConsole_UI().setVisible(true);
			}
		});
	}

	private void initComponents() {
		this.labelDebugTitle = new JLabel();
		this.scrollPane = new JScrollPane();
		this.textAreaDebug = new JTextArea();

		setDefaultCloseOperation(2);
		setTitle("RemoteMedia Dev Console");
		setResizable(false);

		this.labelDebugTitle.setText("Debug Output:");

		this.scrollPane.setHorizontalScrollBarPolicy(31);
		this.scrollPane.setToolTipText("");
		this.scrollPane.setAutoscrolls(true);
		this.scrollPane.setName("");
		this.scrollPane.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				DeveloperConsole_UI.this.scrollPaneMouseReleased(evt);
			}

			public void mousePressed(MouseEvent evt) {
				DeveloperConsole_UI.this.scrollPaneMousePressed(evt);
			}

		});
		this.textAreaDebug.setEditable(false);
		this.textAreaDebug.setColumns(20);
		this.textAreaDebug.setLineWrap(true);
		this.textAreaDebug.setRows(5);
		this.textAreaDebug.setToolTipText("");
		this.scrollPane.setViewportView(this.textAreaDebug);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			layout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelDebugTitle, -1, 414, 32767).addComponent(this.scrollPane))
				.addContainerGap()));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			layout.createSequentialGroup().addContainerGap().addComponent(this.labelDebugTitle, -2, 14, -2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.scrollPane, -1, 184, 32767)
				.addContainerGap()));

		pack();
	}

	private void scrollPaneMousePressed(MouseEvent evt) {
	}

	private void scrollPaneMouseReleased(MouseEvent evt) {
	}

	public JTextArea getDebugOutput() {
		return this.textAreaDebug;
	}

}
