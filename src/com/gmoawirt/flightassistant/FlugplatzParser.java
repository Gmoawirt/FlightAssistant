package com.gmoawirt.flightassistant;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Michael Frank
 * @version 1.0 26.02.2013
 */
public class FlugplatzParser implements Runnable {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private List<Airfield> airfields;
	
	private final String filename = "http://www.dulv.de/app/so.asp?o=/_obj/529EFE4B-44ED-4E77-8EEE-067BF347C61B/outline/Flugplaetze_Google_DULV.kml";

	public FlugplatzParser(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void startParsing(){		
		airfields = getAirportsFromXML(filename);
		
		for (Airfield airfield : airfields) {
			createAirfield(airfield);			
		}
		System.out.println("Finished Writing");
		
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createAirfield(Airfield airfield) {
		System.out.println("LogManager: Writing Airfield " + airfield.getName() + " with Longitude " + airfield.getLongitude());

		ContentValues values = new ContentValues();

		values.put(MySQLiteHelper.COLUMN_AIRFIELD, airfield.getName());
		values.put(MySQLiteHelper.COLUMN_ICAO, airfield.getICAO());
		values.put(MySQLiteHelper.COLUMN_DESCRIPTION, airfield.getInfo());
		values.put(MySQLiteHelper.COLUMN_LONGITUDE, airfield.getLongitude());
		values.put(MySQLiteHelper.COLUMN_LATITUDE, airfield.getLatitude());
		values.put(MySQLiteHelper.COLUMN_ALTITUDE, airfield.getHeight());

		database.insert(MySQLiteHelper.TABLE_WAYPOINTS, null, values);
	}


	public List<Airfield> getAirportsFromXML(String filename) {
		// new URI(filename).toURL().openStream();
		Document d = null;
		try {
			d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList list = d.getElementsByTagName("Folder");

		for (int i = 0; i < list.getLength(); i++) {

			// there are 3 folder
			// we only need the "ul" type, skip all other
			Node n = getChildNode(list.item(i), "name");

			if (n != null && n.getTextContent().contains(" mit UL-Zulassung")) {
				System.out.println("found");
				List<Airfield> airFields = getAirFieldsFromFolder(list.item(i));
				return airFields;
			}

		}

		return null;
	}

	private List<Airfield> getAirFieldsFromFolder(Node folder) {
		if (!folder.getNodeName().equalsIgnoreCase("Folder")) {
			return null;
		}
		List<Airfield> airFields = new ArrayList<Airfield>();
		NodeList folderChilds = folder.getChildNodes();
		for (int i = 0; i < folderChilds.getLength(); i++) {
			Node currentNode = folderChilds.item(i);
			String currNodeName = currentNode.getNodeName();
			// System.out.println("cur: " + currNodeName);
			if (currNodeName.equalsIgnoreCase("name")) {
				// System.out.println("Name: " + currentNode.getTextContent());
			} else if (currNodeName.equalsIgnoreCase("description")) {
				System.out.println("description: " + currentNode.getTextContent());
			} else if (currNodeName.equalsIgnoreCase("Placemark")) {
				Airfield f = parsePlaceMarkNode(currentNode);
				if (f != null)
					airFields.add(f);

			}
		}
		return airFields;
	}

	private Node getChildNode(Node node, String childName) {
		NodeList nodeChilds = node.getChildNodes();
		for (int i = 0; i < nodeChilds.getLength(); i++) {

			// System.out.println("cur: " + currNodeName);
			if (nodeChilds.item(i).getNodeName().equals(childName)) {
				return nodeChilds.item(i);

			}
		}
		return null;
	}

	private Airfield parsePlaceMarkNode(Node placeMark) {
		if (!placeMark.getNodeName().equalsIgnoreCase("Placemark")) {
			return null;
		}
		// example
		// <Placemark>
		// <name>EDBI Zwickau</name>
		// <description><![CDATA[<a
		// href="http://www.dulv.de/fmi/xsl/Flugplatzdatenbank/browserecord.xsl?-lay=ULPlatz_Detail&-find=-find&KurzName=Zwicka">Flugplatzdetails</a>.
		// aus www.DULV.de<br> oder direkt zur <a
		// href="http://www.acz.de/">Flugplatzhomepage</a><br><br>Änderungen
		// senden an <br><a
		// href="mailto:vwyklicky@dulv.de?subject=UL-Flugplaetze%20via%20Google%20Earth">©
		// UL-Flugplätze@DULV.de</a>]]></description>
		// <styleUrl>#sn_ltblu-pushpin_copy3</styleUrl>
		// <Point>
		// <coordinates>12.4611111111111,50.70416666666671,319.9999999999999</coordinates>
		// </Point>
		// </Placemark>
		// getXmlNodeTreeAsStream(placeMark, System.out);
		String airportName = null;
		String airportDesc = null;
		String icao = null;
		double longitude = Double.NaN, latitude = Double.NaN, height = Double.NaN;
		NodeList folderChilds = placeMark.getChildNodes();
		for (int j = 0; j < folderChilds.getLength(); j++) {
			Node currentNode = folderChilds.item(j);
			String currNodeName = currentNode.getNodeName();

			// System.out.println(currNodeName);

			if (currNodeName.equalsIgnoreCase("name")) {
				String text = currentNode.getTextContent();
				int pos = text.indexOf(' ');
				if (pos <= 0 || pos >= text.length() - 1) {

					System.out.print("ERROR malformated name or missing icao code:\n");
					getXmlNodeTreeAsStream(placeMark, System.out);
					System.out.println("");
					return null;
				}
				icao = text.substring(0, pos);
				airportName = text.substring(pos + 1, text.length());

			} else if (currNodeName.equalsIgnoreCase("description")) {
				airportDesc = currentNode.getTextContent();
			} else if (currNodeName.equalsIgnoreCase("Point")) {
				// getXmlNodeTreeAsStream(currentNode, System.out);

				NodeList pointChilds = currentNode.getChildNodes();
				for (int k = 0; k < pointChilds.getLength(); k++) {

					if (pointChilds.item(k).getNodeName().equalsIgnoreCase("coordinates")) {

						String[] coordinates = pointChilds.item(k).getTextContent().split(",");

						if (coordinates == null || coordinates.length != 3) {
							System.out.print("ERROR wrong coordinate format\n");
							getXmlNodeTreeAsStream(placeMark, System.out);
							System.out.println("");
							return null;
						} else {
							longitude = Double.parseDouble(coordinates[0]);
							latitude = Double.parseDouble(coordinates[1]);
							height = Double.parseDouble(coordinates[2]);
						}

					}
				}
			}
		}
		Airfield f = new Airfield(latitude, longitude, height, icao, airportName, airportDesc);
		if (icao != null && airportName != null && airportDesc != null && latitude != Double.NaN && longitude != Double.NaN && height != Double.NaN) {
			return f;
		}
		System.out.print("ERROR missing values:" + f.toString() + " \n");
		getXmlNodeTreeAsStream(placeMark, System.out);
		System.out.println("");
		return null;
	}

	/**
	 * For debug purpose. Returns a graphical representation of a given DOM Tree
	 * starting at node.
	 * 
	 * @param node
	 *            starting Node
	 * @param out
	 *            where to write the result, for example System.out
	 */
	public static void getXmlNodeTreeAsStream(Node node, PrintStream out) {
		int type = node.getNodeType();
		switch (type) {
		// print the document element
		case Node.DOCUMENT_NODE:
			getXmlNodeTreeAsStream(((Document) node).getDocumentElement(), out);
			break;

		// print element with attributes
		case Node.ELEMENT_NODE:
			out.print("<");
			out.print(node.getNodeName());
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				out.print(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
			}
			out.print(">");

			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++)
					getXmlNodeTreeAsStream(children.item(i), out);
			}

			break;

		// handle entity reference nodes
		case Node.ENTITY_REFERENCE_NODE:
			out.print("&");
			out.print(node.getNodeName());
			out.print(";");
			break;

		// print cdata sections
		case Node.CDATA_SECTION_NODE:
			out.print("<![CDATA[");
			out.print(node.getNodeValue());
			out.print("]]>");
			break;

		// print text
		case Node.TEXT_NODE:
			out.print(node.getNodeValue());
			break;

		// print processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE:
			out.print("<?");
			out.print(node.getNodeName());
			String data = node.getNodeValue();
			{
				out.print(" ");
				out.print(data);
			}
			out.print("?>");
			break;

		default:
			System.out.println("NODE UNKNOWN: " + node);
		}

		if (type == Node.ELEMENT_NODE) {
			out.print("</");
			out.print(node.getNodeName());
			out.print('>');
		}
	} // printDOMTree(Node, PrintWriter)

	public static String toString(Object o) {
		ArrayList list = new ArrayList();
		toString(o, o.getClass(), list);
		return o.getClass().getName() + (list.toString());
	}

	private static void toString(Object o, Class clazz, List list) {
		Field f[] = clazz.getDeclaredFields();
		AccessibleObject.setAccessible(f, true);
		for (int i = 0; i < f.length; i++) {
			try {
				list.add("\n\t" + f[i].getName() + "=" + f[i].get(o) + "");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if (clazz.getSuperclass().getSuperclass() != null) {
			toString(o, clazz.getSuperclass(), list);
		}
	}

	public static class Airfield {
		private double height;
		private String icao;
		private String name;
		private String info;
		private double latitude;
		private double longitude;

		public Airfield(double latitude, double longitude, double height, String icao, String name, String info) {
			super();
			this.latitude = latitude;
			this.longitude = longitude;
			this.height = height;
			this.info = info;
			this.name = name;
			this.icao = icao;
		}

		public String getICAO() {
			return icao;
		}

		public String getName() {
			return name;
		}

		public double getHeight() {
			return height;
		}

		public String getInfo() {
			return info;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		@Override
		public String toString() {

			return FlugplatzParser.toString(this);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		open();
		startParsing();
		close();
	}

}
