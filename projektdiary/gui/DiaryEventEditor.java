package projektdiary.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import de.wannawork.jcalendar.JCalendarComboBox;

import projektdiary.api.DiaryEvent;

public class DiaryEventEditor extends JPanel {

	// Komponenter som behövs till editorn
	transient private JTextArea commentEditor;

	transient private JCalendarComboBox dateSelector;

	transient private JSpinner houresSelector;

	transient private JSpinner minutesSelector;

	transient private ActionListener change;

	// Annat

	transient private DiaryEvent event;

	/**
	 * Tar en händelse som skall editeras
	 * 
	 * @param event
	 * @parm change om något händer skall den anropas
	 */
	public DiaryEventEditor(DiaryEvent event, ActionListener change) {
		super();
		this.event = event;
		this.change = change;

		addComponents();
	}

	private void addComponents() {
		// TODO Auto-generated method stub
		setLayout(new BorderLayout());

		setBorder(new TitledBorder("Event editor:"));
		// Kommentar delen

		commentEditor = new JTextArea();
		commentEditor.setText(event.getElementComment());

		commentEditor.setLineWrap(true);
		commentEditor.setWrapStyleWord(true);
		JScrollPane commentScroll = new JScrollPane(commentEditor);

		JPanel commentPanel = new JPanel();

		commentPanel.setBorder(new TitledBorder("Comment:"));

		commentPanel.setLayout(new BorderLayout());

		commentPanel.add(commentScroll, BorderLayout.CENTER);

		add(commentPanel, BorderLayout.CENTER);

		// Skapar restemn

		JPanel datePanel = new JPanel();

		datePanel.setBorder(new TitledBorder("Date of event:"));

		dateSelector = new JCalendarComboBox();
		Calendar cal = Calendar.getInstance();
		cal.setTime(event.getElementDate());
		dateSelector.setCalendar(cal);

		datePanel.add(dateSelector);

		JPanel timePanel = new JPanel();

		// timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));

		timePanel.setBorder(new TitledBorder("Length of the event:"));

		timePanel.add(new JLabel("H: "));

		houresSelector = new JSpinner(new SpinnerNumberModel(1, 0, 99, 1));

		houresSelector.setValue(event.getElementTimeHouers());

		houresSelector.setMinimumSize(new Dimension(50, (int) houresSelector
				.getMinimumSize().getHeight()));

		timePanel.add(houresSelector);

		timePanel.add(new JLabel("M: "));

		minutesSelector = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

		minutesSelector.setValue(event.getElementTimeMinutes());
		// minutesSelector.setText("00");

		timePanel.add(minutesSelector);

		JPanel savePanel = new JPanel();

		savePanel.setBorder(new TitledBorder("Save:"));

		JButton saveButton = new JButton("Save");

		saveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				save();
			}

		});

		savePanel.add(saveButton);

		// En panel att stoppa resten i.

		JPanel restPanel = new JPanel();

		restPanel.setLayout(new BoxLayout(restPanel, BoxLayout.X_AXIS));

		restPanel.add(datePanel);

		restPanel.add(timePanel);

		restPanel.add(savePanel);

		add(restPanel, BorderLayout.PAGE_END);

	}

	/**
	 * Sparar ändringar som har gjorts i den aktuella händelsen
	 * 
	 */

	public void save() {

		event.setElementComment(commentEditor.getText());

		event.setElementDate(dateSelector.getModel().getDate());

		event.setElementTimeHouers((Integer) houresSelector.getValue());

		event.setElementTimeMinutes((Integer) minutesSelector.getValue());

		change.actionPerformed(null);

	}

	public void setEvent(DiaryEvent event) {

		save();

		this.event = event;

		commentEditor.setText(event.getElementComment());

		Calendar cal = Calendar.getInstance();
		cal.setTime(event.getElementDate());
		dateSelector.setCalendar(cal);

		houresSelector.setValue((new Integer(event.getElementTimeHouers())));

		minutesSelector.setValue((new Integer(event.getElementTimeMinutes())));

	}

}
