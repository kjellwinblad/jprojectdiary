package projektdiary.api;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * En klass med statiska metoder för att spara hämta från en fil
 * 
 * @author kjell
 * 
 */
public class DiaryFileFormatHelper {

	/**
	 * Sparar listan till den specifiserade filen i filformatet
	 * 
	 * @param list
	 * @param saveTo
	 */
	public static void saveToFile(List<DiaryEvent> list, File saveTo)
			throws IOException {

		FileWriter fileOut = new FileWriter(saveTo);

		fileOut.write("<?xml version=\"1.0\" encoding=\"ISO8859-1\"?>\n");
		fileOut.write("<!DOCTYPE diary SYSTEM \"" + System.getProperty("user.home") + File.separator
				+ ".projektdiary" + File.separator
				+ "diary.dtd\">\n");
		fileOut.write("<diary>\n");

		// Går igenom samtliga events och spar dem som ett event i filen
		for (DiaryEvent event : list) {
			fileOut.write("  <event>\n");
			fileOut.write("    <comment>" + event.getElementComment()
					+ "</comment>\n");
			fileOut.write("    <date>" + event.getElementDate().getTime()
					+ "</date>\n");
			fileOut.write("    <hours>" + event.getElementTimeHouers()
					+ "</hours>\n");
			fileOut.write("    <minutes>" + event.getElementTimeMinutes()
					+ "</minutes>\n");
			fileOut.write("  </event>\n");
		}

		fileOut.write("</diary>\n");

		fileOut.close();

	}

	private static String toXMLtext(String elementComment) {

		String replaced = elementComment.replace("&", "&amp");
		replaced = replaced.replace("<", "&lt");
		replaced = replaced.replace(">", "&gt");
		replaced = replaced.replace("\"", "&quot");
		replaced = replaced.replace("'", "&#39");

		return replaced;
	}

	/**
	 * Försöker öppna den angivna filen
	 * 
	 * @param saveTo
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static List<DiaryEvent> getFromFile(File toOpen) throws Exception {

		// Om events är null har vi inget i vårt schema än och skapar då ett
		// nytt

		fixDTD();

		// variabler som behövs för xml parsern
		DocumentBuilderFactory factory = null;
		DocumentBuilder parser = null;
		Document document = null;
		Element calendar = null;
		Element event = null;

		List<DiaryEvent> list = new LinkedList<DiaryEvent>();

		// Start parsing

		int i = 0;
		String comment;
		Date date;
		int hours;
		int minutes;

		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		parser = factory.newDocumentBuilder();
		parser.setErrorHandler(new ErrorHandler() {

			public void warning(SAXParseException exception)
					throws SAXException {

				System.err.println(exception.getMessage());
				throw new SAXException("Wrong file format");
			}

			public void error(SAXParseException exception) throws SAXException {
				System.err.println(exception.getMessage());
				throw new SAXException("Wrong file format");
			}

			public void fatalError(SAXParseException exception)
					throws SAXException {
				System.err.println(exception.getMessage());
				throw new SAXException("Wrong file format");
			}

		});

		// Parse with DOM
		document = parser.parse(toOpen);

		calendar = document.getDocumentElement();

		while ((event = (Element) calendar.getElementsByTagName("event").item(
				i++)) != null) {
			// Ta ut de olika elementen ur xml-filen
			comment = new String(event.getElementsByTagName("comment").item(0)
					.getTextContent());

			date = new Date(new Long(event.getElementsByTagName("date").item(0)
					.getTextContent()));
			hours = new Integer(event.getElementsByTagName("hours").item(0)
					.getTextContent());
			minutes = new Integer(event.getElementsByTagName("minutes").item(0)
					.getTextContent());

			// Lägg till ett nytt event
			DiaryEvent e = new DiaryEvent();
			e.setElementComment(comment);
			e.setElementDate(date);
			e.setElementTimeHouers(hours);
			e.setElementTimeMinutes(minutes);

			list.add(e);
		}

		return list;

	}

	/**
	 * Kollar så att vi har en dtd
	 * 
	 * @throws IOException
	 * 
	 */
	private static void fixDTD() throws IOException {


		File test = new File(System.getProperty("user.home") + File.separator
				+ ".projektdiary");
		if (!test.exists()) {

			if (!test.mkdirs())
				throw new IOException("Could not create dir: " + test);

		}

		test = new File(System.getProperty("user.home") + File.separator
				+ ".projektdiary" + File.separator + "diary.dtd");

		if (!test.exists()) {
			test.createNewFile();
			// Existerar den inte skall den skapas...
			FileWriter fileOut = new FileWriter(test);

			fileOut.write("<!ELEMENT diary    (event+)>\n");
			fileOut
					.write("<!ELEMENT event       (comment, date, hours, minutes)>\n");
			fileOut.write("\n");
			fileOut.write("<!ELEMENT comment       (#PCDATA)>\n");
			fileOut.write("<!ELEMENT date         (#PCDATA)>\n");
			fileOut.write("<!ELEMENT hours    (#PCDATA)>\n");
			fileOut.write("<!ELEMENT minutes (#PCDATA)>\n");

			fileOut.close();

		}

	}

	public static void exportToHTML(File exportFile, List<DiaryEvent> list)
			throws Exception {
		if (!exportFile.exists()) {

			if (!exportFile.createNewFile())
				throw new IOException("Could not create file: " + exportFile);

		} else {

			if (!exportFile.canWrite()) {

				throw new Exception("Could not write to file: " + exportFile);

			}

		}

		// Ska skriva till filen

		FileWriter fileOut = new FileWriter(exportFile);

		// Skriver huvudet
		fileOut
				.write("<html>\n  <head>\n    <title></title>\n  </head>\n  <body>\n  <table border=2>\n");

		// Rubriker i tabellen
		fileOut
				.write("  <tr>\n <th>Date</th>\n <th>Comment</th>\n <th>Time</th>\n </tr>\n");

		for (int n = 0; n < list.size(); n++) {
			fileOut.write("  <tr>\n");

			// Date

			fileOut.write("  <td valign=top>\n");

			fileOut.write("    "
					+ DateFormat.getDateInstance().format(
							list.get(n).getElementDate()) + "\n  </td>");

			// Comment

			fileOut.write("  <td>\n  	"
					+ fixNewLines(list.get(n).getElementComment())
					+ "\n  </td>");

			// Time

			fileOut.write("<td valign=bottom>\n");
			fileOut.write("<center>" + list.get(n).getElementTimeHouers()
					+ "&nbsp;h&nbsp;" + list.get(n).getElementTimeMinutes()
					+ "&nbsp;min\n  </td></center>");

			fileOut.write("  </tr>\n");
		}
		fileOut.write("  </table>\n");
		fileOut.write(" </body>\n");
		fileOut.write("</html>\n");

		fileOut.close();

		/*
		 * <tr> <td valign=top> 2006-jun-10 </td> <td> Back in school they never
		 * taught us what we needed to know, like how to deel with despair or
		 * someone broken your heart. twelwe years I held it all together but a
		 * night like this has begin to torn me apart. You said best friends,
		 * means best friends to me. So is that what you call a gettaway? Then
		 * tell me what you get away with. çause I´ve seen more guts in a
		 * yellofich, I'v seen more spine in an eleven year old kid. So have
		 * another drink and drive yourselfe home. Hope it ice on all the roads
		 * and you can think of me when you forget your seatbelt and your head
		 * goes through the wndshield! </td>
		 * 
		 * <td valign=bottom> 2&nbsp;h&nbsp;10&nbsp;min </td>
		 * 
		 * 
		 * </table>
		 * 
		 * 
		 * </body> </html>
		 * 
		 */

	}

	private static String fixNewLines(String elementComment) {

		return elementComment.replace("\n", "\n<br>\n");
	}

}
