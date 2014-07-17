package com.ibbignerd.mediacontrols;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeveloperConsole extends JFrame {
	protected static final String NIMBUS = "Nimbus";
	private static final long serialVersionUID = -2454152123021063278L;
	private JTextArea textAreaDebug;

	public DeveloperConsole() {
		initComponents();
	}

	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (NIMBUS.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ignored) {
		} catch (UnsupportedLookAndFeelException ignored) {
		} catch (InstantiationException ignored) {
		} catch (IllegalAccessException ignored) {
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new DeveloperConsole().setVisible(true);
			}
		});
	}

	private void initComponents() {
		JLabel labelDebugTitle = new JLabel();
		JScrollPane scrollPane = new JScrollPane();
		textAreaDebug = new JTextArea();

		setDefaultCloseOperation(2);
		setTitle("RemoteMedia Dev Console");
		setResizable(false);

		labelDebugTitle.setText("Debug Output:");

		scrollPane.setHorizontalScrollBarPolicy(31);
		scrollPane.setToolTipText("");
		scrollPane.setAutoscrolls(true);
		scrollPane.setName("");
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				scrollPaneMouseReleased();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				scrollPaneMousePressed();
			}

		});
		textAreaDebug.setEditable(false);
		textAreaDebug.setColumns(20);
		textAreaDebug.setLineWrap(true);
		textAreaDebug.setRows(5);
		textAreaDebug.setToolTipText("");
		scrollPane.setViewportView(textAreaDebug);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(
			layout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					layout.createParallelGroup(Alignment.LEADING).addComponent(labelDebugTitle, -1, 414, 32767)
						.addComponent(scrollPane)).addContainerGap()));

		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(labelDebugTitle, -2, 14, -2)
					.addPreferredGap(ComponentPlacement.RELATED).addComponent(scrollPane, -1, 184, 32767)
					.addContainerGap()));

		pack();
	}

	void scrollPaneMousePressed() {
	}

	void scrollPaneMouseReleased() {
	}

	public JTextArea getDebugOutput() {
		return textAreaDebug;
	}

}
