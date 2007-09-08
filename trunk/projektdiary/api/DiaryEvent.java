package projektdiary.api;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class DiaryEvent implements Comparable<DiaryEvent>, Serializable {

	private Date elementDate;

	private String elementComment;

	private int elementTimeHouers;

	private int elementTimeMinutes;

	public DiaryEvent() {
		elementComment = "";
		elementDate = new Date();
	}

	public String getElementComment() {
		return elementComment;
	}

	public void setElementComment(String elementComment) {
		this.elementComment = elementComment;
	}

	public Date getElementDate() {
		return elementDate;
	}

	public void setElementDate(Date elementDate) {
		this.elementDate = elementDate;
	}

	public int getElementTimeHouers() {
		return elementTimeHouers;
	}

	/**
	 * Sätter tiden i timmar... Klarar även av delar av en timme... 1.30 sätter
	 * 1 h 30 min
	 * 
	 * @param elementTimeHouers
	 */
	public void setElementTimeHouers(double elementTimeHouers) {

		if (Math.abs(elementTimeHouers)
				- Math.floor(Math.abs(elementTimeHouers)) != 0)
			elementTimeMinutes = (int) Math.floor((60 * (Math
					.abs(elementTimeHouers) - Math.floor(Math
					.abs(elementTimeHouers)))));

		this.elementTimeHouers = (int) Math.floor(Math.abs(elementTimeHouers));
	}

	public int getElementTimeMinutes() {
		return elementTimeMinutes;
	}

	public void setElementTimeMinutes(int elementTimeMinutes) {
		this.elementTimeMinutes = elementTimeMinutes;
	}

	public String toString() {

		return (DateFormat.getDateInstance().format(elementDate) + " - ("
				+ elementTimeHouers + " h " + elementTimeMinutes + " m)");

	}

	/**
	 * Gämför viilken som händelse som är före den andra....
	 * 
	 * @param o
	 * @return
	 */
	public int compareTo(DiaryEvent o) {
		return elementDate.compareTo(o.getElementDate());
	}

}
