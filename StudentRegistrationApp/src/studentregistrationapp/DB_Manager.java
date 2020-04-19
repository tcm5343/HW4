/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package studentregistrationapp;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author miller
 */
public class DB_Manager {

    private final String myConnectString;
    private final String backupFile = "StudentRegistration.xml";

    // custon constructor
    public DB_Manager() {
        // dynamically fetches path for database
        String url = System.getProperty("user.dir");
        myConnectString = "jdbc:ucanaccess://" + url.replace("\\", "/")
                + "/database/StudentRegistration.accdb";
    }

    /**
     * This method returns an int which tells me how big to make my array to
     * hold the records
     */
    
    public static int countAllRecords() {
        DB_Manager db = new DB_Manager();
        int count = 0;

        try {
            // load database driver class
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // loops through the tables array changing the query
            // connect to database
            Connection con = DriverManager.getConnection(db.myConnectString);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM Students");

            while (rs.next()) {
                count += rs.getInt("total");
            }

            stmt.close();
            con.close();
        } // detect problems interacting with the database
        catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null,
                    sqlException.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } // detect problems loading database driver
        catch (ClassNotFoundException classNotFound) {
            JOptionPane.showMessageDialog(null,
                    classNotFound.getMessage(), "Driver Not Found",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return count;
    } // end of countAllRecords()

    /*
    Queries the database for all records and returns an Array of 
    StudentRecord which will then be displayed using a JList.
     */
    public StudentRecord[] queryRecords() {

        // creates an array to hold records
        StudentRecord[] Records = new StudentRecord[countAllRecords()];

        try {
            // load database driver class
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String sql = "select * from Students";

            // connect to database
            Connection con = DriverManager.getConnection(myConnectString);

            // prepared statement creation and assignment
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            // i am proud of this for loop
            for (int i = 0; rs.next(); i++) {
                String rsFirstName = rs.getString("FirstName");
                String rsLastName = rs.getString("LastName");
                String rsDegreeStatus = rs.getString("DegreeStatus");
                String rsMajor = rs.getString("Major");

                // StudentRecord(String fName, String lname, String degreeStatus, String major)
                StudentRecord studentRecord = new StudentRecord(rsFirstName, rsLastName, rsDegreeStatus, rsMajor);
                Records[i] = studentRecord;
            }
            preparedStatement.close();
            con.close();
        } // detect problems interacting with the database
        catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null,
                    sqlException.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } // detect problems loading database driver
        catch (ClassNotFoundException classNotFound) {
            JOptionPane.showMessageDialog(null,
                    classNotFound.getMessage(), "Driver Not Found",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return Records;
    } // end of queryRecords()

    public void insertRecord(final StudentRecord studentRecord) {
        try {
            // load database driver class
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // connect to database
            Connection con = DriverManager.getConnection(myConnectString);
            PreparedStatement preparedStatement = null;
            String sql = null;

            // prepared statement creation and assignment
            sql = "insert into Students values(?, ?, ?, ?)";
            preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, studentRecord.getFirstName());
            preparedStatement.setString(2, studentRecord.getLastName());
            preparedStatement.setString(3, studentRecord.getDegreeStatus());
            preparedStatement.setString(4, studentRecord.getMajor());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();
        } // detect problems interacting with the database
        catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null,
                    sqlException.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } // detect problems loading database driver
        catch (ClassNotFoundException classNotFound) {
            JOptionPane.showMessageDialog(null,
                    classNotFound.getMessage(), "Driver Not Found",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } finally {
            // JOptionPane.showMessageDialog(null, "New Item Added", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println("Record inserted succesfully");
    } //end of insertRecord()

    public void deleteRecord(final StudentRecord studentRecord) {
        try {
            // load database driver class
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String sql = null;
            // connect to database
            Connection con = DriverManager.getConnection(myConnectString);
            PreparedStatement preparedStatement = null;

            sql = "Delete * from Students where FirstName = ? and LastName = ? and DegreeStatus = ? and Major = ?";

            // prepared statement creation and assignment
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, studentRecord.getFirstName());
            preparedStatement.setString(2, studentRecord.getLastName());
            preparedStatement.setString(3, studentRecord.getDegreeStatus());
            preparedStatement.setString(4, studentRecord.getMajor());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();
        } // detect problems interacting with the database
        catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null,
                    sqlException.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } // detect problems loading database driver
        catch (ClassNotFoundException classNotFound) {
            JOptionPane.showMessageDialog(null,
                    classNotFound.getMessage(), "Driver Not Found",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        System.out.println("Record deleted");
    } //end of deleteRecord()

    // https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
    // this whole method is primarily based off of this post
    public void writeXMLFile() {

        // query all records which are currently stored in the database
        StudentRecord[] studentRecordArray = queryRecords();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("studentrecords");
            doc.appendChild(rootElement);

            for (StudentRecord record : studentRecordArray) {
                // supercars element
                Element studentrecord = doc.createElement("student");
                rootElement.appendChild(studentrecord);

                Element firstName = doc.createElement("FirstName");
                firstName.appendChild(doc.createTextNode(record.getFirstName()));
                studentrecord.appendChild(firstName);

                Element lastName = doc.createElement("LastName");
                lastName.appendChild(doc.createTextNode(record.getLastName()));
                studentrecord.appendChild(lastName);

                Element degreeStatus = doc.createElement("DegreeStatus");
                degreeStatus.appendChild(doc.createTextNode(record.getDegreeStatus()));
                studentrecord.appendChild(degreeStatus);

                Element major = doc.createElement("Major");
                major.appendChild(doc.createTextNode(record.getMajor()));
                studentrecord.appendChild(major);
            }

            // this is wild but it works.. :/
            String filepath = System.getProperty("user.dir").substring(0, 3)
                    + "\\" + System.getProperty("user.dir").substring(3, System.getProperty("user.dir").length())
                    + "\\" + backupFile;
            System.out.println(filepath);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // https://stackoverflow.com/questions/13553614/how-to-add-doctype-in-xml-document-using-dom-java
            // I was close to emailing you until I found this post ^^
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMImplementation domImpl = doc.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("doctype",
                    "-//Oberon//YOUR PUBLIC DOCTYPE//EN", "Students.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Database succesfully backed up. Remember, if you want to use the " + 
                "default XML file you must delete " + backupFile , "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void readXMLFile() {
        // variable declaration
        String filename = "Students.xml";
        String firstNameXML;
        String lastNameXML;
        String degreeStatusXML;
        String majorXML;
        
        // determine if there is a backup file to be used
        File f = new File(backupFile);
        if (f.exists() && !f.isDirectory() && f.isFile()) {
            System.out.println("backup.xml file used");
            filename = backupFile;
        } else {
            System.out.println("No " + backupFile + " found, Students.xml file used");
        }
        
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setValidating(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new File(filename));
            NodeList list = document.getElementsByTagName("student");
            createStudentsTable();

            //This for loop gathers all the student attributes, puts them in a 
            //StudentRecord object then stores that student in the StudentArray
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                firstNameXML = getFirstNameXML(element);
                lastNameXML = getLastNameXML(element);
                degreeStatusXML = getDegreeStatusXML(element);
                majorXML = getMajorXML(element);

                StudentRecord studentRecord = new StudentRecord(firstNameXML, lastNameXML, degreeStatusXML, majorXML);
                insertRecord(studentRecord);
            }//end for loop loading the studentArray[] with full student records

            System.out.println("Completed reading from XML");

        }//end try block//end try block
        catch (ParserConfigurationException parserException) {
            parserException.printStackTrace();
        }//end catch block
        catch (SAXException saxException) {
            saxException.printStackTrace();
        }//end catch block
        catch (IOException ioException) {
            ioException.printStackTrace();
        }//end catch block

    }//end readFile()

    public String getFirstNameXML(Element parent) {
        NodeList child = parent.getElementsByTagName("FirstName");
        Node childTextNode = child.item(0).getFirstChild();
        return childTextNode.getNodeValue();
    }//end getFirstName

    public String getLastNameXML(Element parent) {
        NodeList child = parent.getElementsByTagName("LastName");
        Node childTextNode = child.item(0).getFirstChild();
        return childTextNode.getNodeValue();
    }

    public String getDegreeStatusXML(Element parent) {
        NodeList child = parent.getElementsByTagName("DegreeStatus");
        Node childTextNode = child.item(0).getFirstChild();
        return childTextNode.getNodeValue();
    }

    public String getMajorXML(Element parent) {
        NodeList child = parent.getElementsByTagName("Major");
        Node childTextNode = child.item(0).getFirstChild();
        return childTextNode.getNodeValue();
    }

    public void createStudentsTable() {

        try {
            // load database driver class
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // connect to database
            Connection con = DriverManager.getConnection(myConnectString);
            Statement stmt = con.createStatement();

            //this code may need to be commented out because an exception will be thrown
            //if this table doesn't exist in the database
            stmt.execute("DROP TABLE Students");

            stmt.execute("CREATE TABLE Students"
                    + "(FirstName varchar(255), LastName varchar(255), "
                    + "DegreeStatus varchar(255), Major varchar(255))");

            System.out.println("Created new Students table");

            stmt.close();
            con.close();
        } // detect problems interacting with the database
        catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null,
                    sqlException.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }//end catch block
        // detect problems loading database driver
        catch (ClassNotFoundException classNotFound) {
            JOptionPane.showMessageDialog(null,
                    classNotFound.getMessage(), "Driver Not Found",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }//end catch block
    }//end createTable()

}
