package projektdiary.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import javax.swing.JFrame;

public class ProjektDiaryMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// B�rjar med att f�rs�ka �ppna fr�n nersparat till fil

		JFrame frame = new MainFrame("Projekt Diary: Malin's Edision");

		frame.setVisible(true);

	}

}
