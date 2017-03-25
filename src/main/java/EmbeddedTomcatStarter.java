import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;

public class EmbeddedTomcatStarter {

    public static void main(String[] args)
            throws LifecycleException, InterruptedException, ServletException {
        Tomcat tomcat = new Tomcat();
        int port = Integer.parseInt(getPort());
        tomcat.setPort(port);

        Context context = tomcat.addContext("/", new File(".").getAbsolutePath());

        addServletToTomcat(port, context);
        context.addServletMapping("/*", "embeddedTomcat");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static String getPort() {
        String port = null;
        InputStream input = null;
        try {
            Properties properties = new Properties();
            input = new FileInputStream("src/main/resources/application.properties");
            properties.load(input);
            //read server.port from application properties
            port = properties.getProperty("server.port");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //if port is not configured then it will defaulted to 8080
        return port != null ? port : "8080";
    }

    private static void addServletToTomcat(final int port, Context context) {
        Tomcat.addServlet(context, "embeddedTomcat", new HttpServlet() {
            protected void service(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                Writer writer = response.getWriter();
                writer.write("Embedded Tomcat Server is Running at:" + port);
                writer.flush();
            }
        });
    }

}
