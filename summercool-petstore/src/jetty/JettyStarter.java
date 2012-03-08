package jetty;

import java.io.IOException;

import javax.naming.NamingException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyStarter {

	private static final int JETTY_SERVER_PORT = 8080;

	// private static final int NETTY_SERVER_PORT = 8888;

	public static void main(String[] args) throws IOException, NamingException {
		startJetty();
	}

	public static void startJetty() throws NamingException {

		Server server = new Server();
		//
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(10);
		threadPool.setMaxThreads(200);
		server.setThreadPool(threadPool);

		//
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(JETTY_SERVER_PORT);
		connector.setMaxIdleTime(3000);
		connector.setAcceptors(Runtime.getRuntime().availableProcessors() + 1);
		connector.setStatsOn(false);
		connector.setLowResourcesConnections(32000);
		connector.setLowResourcesMaxIdleTime(60000 * 10);
		server.setConnectors(new Connector[] { connector });

		//
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		webAppContext.setResourceBase("./webapp");
		webAppContext.setContextPath("/");
		//
		server.setHandler(webAppContext);
		server.setStopAtShutdown(true);
		server.setSendServerVersion(false);
		server.setSendDateHeader(false);
		server.setGracefulShutdown(1000);

		//
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
