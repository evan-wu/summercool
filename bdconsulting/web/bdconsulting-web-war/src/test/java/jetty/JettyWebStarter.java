package jetty;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyWebStarter {

	/** port */
	private int port = 8080;

	protected String charset = "UTF-8";

	public JettyWebStarter() {
	}

	public JettyWebStarter(int port) {
		this.port = port;
	}

	public JettyWebStarter(int port, String charset) {
		super();
		this.port = port;
		this.charset = charset;
	}

	/**
	 * 服务器启动。
	 */
	public void start() {

		Server server = new Server();
		Connector connector = new SelectChannelConnector();

		connector.setPort(this.port);
		server.setConnectors(new Connector[] { connector });

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setResourceBase("./webapp");
		webAppContext.setDefaultsDescriptor("/jetty/webdefault.xml");
		
		server.setHandler(webAppContext); 

		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) {

		JettyWebStarter jettyWebStarter = new JettyWebStarter();
		jettyWebStarter.start();

	}
}
