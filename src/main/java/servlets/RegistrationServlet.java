package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * 
 * demo example how to login. if login credentials are valid, use requestDispatcher to forward req and resp to WelcomeServlet
 * 
 * 
 * */

/**
 * Servlet implementation class RegistrationServlet
 */
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public RegistrationServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		PrintWriter out = response.getWriter(); 
		
		//load the driver class into memory
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//after port, set the name of the DB to which you want to connect
		final String url = "jdbc:mysql://localhost:3306/org?serverTimezone=Europe/Rome";
		final String user = "root";
		final String pwDB = "dunpeal87$$";
		
		//connect to DB. try with resource to autoclose the connection
		try (Connection conn = DriverManager.getConnection(url,user,pwDB)){
			System.out.println("connection established");
			String query = "SELECT * FROM users WHERE email='"+email+"'";
			
			
			//better to use PreparedStatement. Its protection (not fully) against sql injection by javascript
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(query);
			
			String realPW = null;
			
			while(result.next()) {
				realPW = result.getString("password");
			}
			
			if(realPW.equals(password)) {
				RequestDispatcher rd=request.getRequestDispatcher("WelcomeServlet");  
		        rd.forward(request, response);  
			}else {
				//if pw is wrong, we have to go back to index.jsp so the user can retry
				//but we also have to indicate that the pw was wrong. for this we print a message with response.getWriter ...
				//then, instead of forward, we use include to include a jsp / html ... file into the response
				
				/*
				 * java docu include:
				 * Includes the content of a resource (servlet, JSP page, HTML file) in the RESPONSE.
				 * comment by me: it will show a static resource, so the URL remains same
				 * 
				 * 
				 * java docu forward: 
				 * Forwards a REQUEST from a servlet to another resource (servlet, JSP file, or HTML file) on the server.
				 * comment by me: URL will change. so its good practice to use forward when e.g. user has logged in successfully and then will be redirected to next servlet with new URL
				 * */
				response.getWriter().print("<h1>wrong pw</h1>");
				RequestDispatcher rd=request.getRequestDispatcher("/index.jsp");  
		        rd.include(request, response);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
