/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dls102
 */
public class JDBCServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

            // load database driver class
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // connect to database
            Connection con
                    = DriverManager.getConnection("jdbc:ucanaccess://E:/dev/Repos/HW4/StudentRegistrationApp/database/StudentRegistration.accdb");

            // send XHTML page to client
            // start XHTML document
            out.println("<?xml version = \"1.0\"?>");

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                    + "XHTML 1.0 Strict//EN\" \"http://www.w3.org"
                    + "/TR/xhtml1/DTD/xhtml1-strict.dtd\">");

            out.println(
                    "<html xmlns = \"http://www.w3.org/1999/xhtml\">");

            // head section of document
            out.println("<head>");
            out.println("<title>A JDBC Servlet Example</title>");
            out.println("</head>");

            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String degreeStatus = request.getParameter("degreeStatus");
            String major = request.getParameter("major");

            // body section of document
            out.println("<body>");
            out.println("<h1>Student Record Added</h1>");
            out.println("</body>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>First Name:</td>");
            out.println("<td>" + firstName + "</td>");
            out.println("</tr>");
            
            out.println("<tr>");
            out.println("<td>Last Name:</td>");
            out.println("<td>" + lastName + "</td>");
            out.println("</tr>");
            
            out.println("<tr>");
            out.println("<td>Degree Status:</td>");
            out.println("<td>" + degreeStatus + "</td>");
            out.println("</tr>");
            
            out.println("<tr>");
            out.println("<td>Major:</td>");
            out.println("<td>" + major + "</td>");
            out.println("</tr>");
            out.println("</table>");

            // adds record to database
            PreparedStatement preparedStatement = null;
            String sql = null;

            // prepared statement creation and assignment
            sql = "insert into Students values(?, ?, ?, ?)";
            preparedStatement = con.prepareStatement(sql);

            System.out.println("asdfasdfasdfasdfasdfasdf " + request.getParameter("firstName"));

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, degreeStatus);
            preparedStatement.setString(4, major);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();

        } // end try
        // detect problems interacting with the database
        catch (SQLException sqlException) {
            out.println("Database Error - SQL Exception");

        } // detect problems loading database driver
        catch (ClassNotFoundException classNotFound) {
            out.println("ClassNotFoundException - Driver Not Found");

        } finally {
            // end XHTML document
            out.println("</html>");
            out.close();  // close stream to complete the page
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
