package org.apache.guacamole.net.example;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.servlet.GuacamoleHTTPTunnelServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Properties;
import java.net.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialGuacamoleTunnelServlet
    extends GuacamoleHTTPTunnelServlet {

    private final Logger logger = LoggerFactory.getLogger(TutorialGuacamoleTunnelServlet.class);
    private Properties properties = new Properties();

    private String adc_url;
    private String rdp_host;
    private String rdp_port;
    private String guacd_host;
    private Integer guacd_port;

    /**
     * Constructor to load properties file
     */
    public TutorialGuacamoleTunnelServlet()
      throws IOException, FileNotFoundException {
        String base = System.getProperty("catalina.base");
        logger.info("TutorialGuacamoleTunnelServlet constructor");

        InputStream input = new FileInputStream(base + "/conf/guacamole-test.properties");
        properties.load(input);
        input.close();

        adc_url = properties.getProperty("adc_url", "http://dc.sram.lan:5001/ps");
        rdp_host = properties.getProperty("rdp_host", "127.0.0.1");
        rdp_port = properties.getProperty("rdp_port", "3389");
        guacd_host = properties.getProperty("guacd_host", "127.0.0.1");
        guacd_port = Integer.parseInt(properties.getProperty("guacd_port", "4822"));
  
        logger.info("adc_url: " + adc_url);
        logger.info("rdp_host: " + rdp_host);
        logger.info("rdp_port: " + rdp_port);
        logger.info("guacd_host: " + guacd_host);
        logger.info("guacd_port: " + guacd_port);
    }

    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request)
        throws GuacamoleException {

	HttpSession session = request.getSession();
	String user = (String) session.getAttribute("user");
	String pwd = (String) session.getAttribute("pwd");
	String txt;

	logger.info("Provisioning AD user "  + user + "/" + pwd);
/* The old socket way
        try (Socket ADsocket = new Socket(adc_host, adc_port)) {
		BufferedReader in = new BufferedReader(new InputStreamReader(ADsocket.getInputStream()));
		PrintWriter out = new PrintWriter(ADsocket.getOutputStream(), true);
		out.println(user + ":" + pwd);
		while ((txt = in.readLine()) != null) {
			logger.info("rcv: " + txt);
		}
		in.close();
		out.close();
		ADsocket.close();
	} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
/* The new HTTPRequest way */
	String input = "{ \"user\":\"" + user + "\", \"password\":\"" + pwd + "\" }";
	HttpRequest reqst = HttpRequest.newBuilder()
            .uri(URI.create(adc_url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(input))
            .build();

	try {
	    HttpClient client = HttpClient.newHttpClient();
	    HttpResponse<String> response = client.send(reqst, HttpResponse.BodyHandlers.ofString());
	    logger.info("Status: " + response.statusCode());
	    logger.info("Body: " + response.body());
	} catch (IOException | InterruptedException e) {
	    logger.info("Error");
	}

	logger.info("Creating the RDP socket for "  + user + "/" + pwd);

        // Create our configuration
        GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol("rdp");
        config.setParameter("ignore-cert", "true");
        config.setParameter("hostname", rdp_host);
        config.setParameter("port", rdp_port);
        config.setParameter("username", user);
	config.setParameter("password", pwd);
        config.setParameter("domain", "SRAM");
        config.setParameter("security", "nla");
        config.setParameter("width", "1400");
        config.setParameter("height", "800");

        // Connect to guacd - everything is hard-coded here.
        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
                new InetGuacamoleSocket(guacd_host, guacd_port), config);

        // Return a new tunnel which uses the connected socket
        return new SimpleGuacamoleTunnel(socket);

    }
}

