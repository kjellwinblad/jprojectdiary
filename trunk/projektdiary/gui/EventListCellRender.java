package projektdiary.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import projektdiary.api.DiaryEvent;

public class EventListCellRender extends JPanel implements ListCellRenderer {

	JLabel dateLabel;

	JLabel timeLabel;

	JPanel timePanel;

	JPanel datePanel;

	public EventListCellRender() {
		super();

		addComponents();
	}

	private void addComponents() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		setBorder(new LineBorder(Color.BLACK));

		JSplitPane split = new JSplitPane();
		dateLabel = new JLabel();
		dateLabel.setAlignmentX(0);

		datePanel = new JPanel();
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
		// datePanel.setPreferredSize(this.getParent().getSize());

		datePanel.add(dateLabel);
		datePanel.add(Box.createHorizontalGlue());
		add(datePanel);

		timeLabel = new JLabel();
		timeLabel.setAlignmentX(100);

		timePanel = new JPanel();
		timePanel.setAlignmentX(100);
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
		// timePanel.setBorder(new LineBorder(Color.BLACK));
		timePanel.add(timeLabel);

		// this.add(Box.createHorizontalGlue());
		add(timePanel);

	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		dateLabel.setText(DateFormat.getDateInstance().format(
				((DiaryEvent) value).getElementDate()));

		timeLabel.setText(((DiaryEvent) value).getElementTimeHouers() + " h "
				+ ((DiaryEvent) value).getElementTimeMinutes() + " m");

		this.setToolTipText(((DiaryEvent) value).getElementComment());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			dateLabel.setBackground(list.getSelectionBackground());
			dateLabel.setForeground(list.getSelectionForeground());
			timeLabel.setBackground(list.getSelectionBackground());
			timeLabel.setForeground(list.getSelectionForeground());

			timePanel.setBackground(list.getSelectionBackground());
			timePanel.setForeground(list.getSelectionForeground());
			datePanel.setBackground(list.getSelectionBackground());
			datePanel.setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			dateLabel.setBackground(list.getBackground());
			dateLabel.setForeground(list.getForeground());
			timeLabel.setBackground(list.getBackground());
			timeLabel.setForeground(list.getForeground());

			timePanel.setBackground(list.getBackground());
			timePanel.setForeground(list.getForeground());
			datePanel.setBackground(list.getBackground());
			datePanel.setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		return this;
	}

}
