package projektdiary.gui;
 
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ThemeChangeListener implements ActionListener {

	private String className;

	private JFrame frame;

	public ThemeChangeListener(String className, MainFrame frame) {
		this.frame = frame;
		this.className = className;

	}

	public void actionPerformed(ActionEvent e) {
		try {
			UIManager.setLookAndFeel(className);

			SwingUtilities.updateComponentTreeUI(frame);

			// frame.pack();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

}

