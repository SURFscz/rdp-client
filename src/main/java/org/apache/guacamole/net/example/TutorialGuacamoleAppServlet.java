package org.apache.guacamole.net.example;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Random;

import java.security.MessageDigest;
import java.util.UUID;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple TutorialGuacamoleAppServlet servlet.
 */

public final class TutorialGuacamoleAppServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(TutorialGuacamoleAppServlet.class);
    private final Random rand = new Random();

    /**
     * Respond to a GET request for the content produced by
     * this servlet.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {
      String pwd = "P@ssw0rd1234!";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(UUID.randomUUID().toString().getBytes("UTF-8"));
	    byte[] digest = md.digest();
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0;i<digest.length;i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            pwd = hexString.toString() + "!";
	} catch (NoSuchAlgorithmException e) {
	    logger.info("NoSuchAlgorithmException");
        }
	//String pwd = "P@ssw0rd" + String.format("%04d", rand.nextInt(10000)) + "!";
	//String pwd = "test1234!";
	String user = request.getParameter("user");

	logger.info("AppServlet request for: " + user + ", using pwd: " + pwd );

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("pwd", pwd);

	request.setAttribute("pwd", pwd);
	request.setAttribute("user", user);
	request.getRequestDispatcher("/WEB-INF/app.jsp").forward(request, response);
	
    }
} 
