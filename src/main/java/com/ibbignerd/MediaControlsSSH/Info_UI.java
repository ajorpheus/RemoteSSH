package com.ibbignerd.MediaControlsSSH;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Info_UI extends JFrame {
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private JPanel panelMain;

	public Info_UI() {
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
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Info_UI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(Info_UI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Info_UI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Info_UI.class.getName()).log(Level.SEVERE, null, ex);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Info_UI().setVisible(true);
			}
		});
	}

	private void initComponents() {
		this.panelMain = new JPanel();
		this.label1 = new JLabel();
		this.label2 = new JLabel();
		this.label3 = new JLabel();
		this.label4 = new JLabel();

		setDefaultCloseOperation(2);
		setTitle("RemoteMedia Credits");
		setResizable(false);

		this.label1.setText("Coded by: ibbignerd (Nathan Diddle)");

		this.label2.setText("Concept: Reeiiko");

		this.label3.setText("OpenSSH Dev: J. Anrugas");

		this.label4.setText("SSHMediaControls Dev: rob311");

		GroupLayout panelMainLayout = new GroupLayout(this.panelMain);
		this.panelMain.setLayout(panelMainLayout);
		panelMainLayout.setHorizontalGroup(panelMainLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			panelMainLayout
				.createSequentialGroup()
				.addContainerGap(-1, 32767)
				.addGroup(
					panelMainLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.label1)
						.addComponent(this.label2).addComponent(this.label3).addComponent(this.label4))
				.addContainerGap(-1, 32767)));

		panelMainLayout.setVerticalGroup(panelMainLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
			panelMainLayout.createSequentialGroup().addContainerGap().addComponent(this.label1)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.label2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.label3)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.label4)
				.addContainerGap(-1, 32767)));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
			this.panelMain, GroupLayout.Alignment.TRAILING, -1, -1, 32767));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.panelMain,
			GroupLayout.Alignment.TRAILING, -1, -1, 32767));

		pack();
		setLocationRelativeTo(null);
	}
}

/*
 * Location: C:\Users\jindala\Desktop\main.jar Qualified Name: com.ibbignerd.MediaControlsSSH.Info_UI JD-Core Version:
 * 0.7.0.1
 */
