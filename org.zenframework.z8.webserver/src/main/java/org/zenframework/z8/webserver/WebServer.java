package org.zenframework.z8.webserver;

import java.rmi.RemoteException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.gzip.GzipHandler;
import org.zenframework.z8.server.config.ServerConfig;
import org.zenframework.z8.server.engine.IWebServer;
import org.zenframework.z8.server.engine.RmiServer;
import org.zenframework.z8.server.logs.Trace;
import org.zenframework.z8.server.types.guid;

/**
 * {@link WebServer#launch(org.zenframework.z8.server.config.ServerConfig) method is the entrypoint,
 * do not forget to override it in descendants }
 */
public class WebServer extends RmiServer implements IWebServer {
	private static final String ID = guid.create().toString();
	private Server server;
	private ContextHandler context;

	public WebServer() throws RemoteException {
		super(ServerConfig.webServerPort());
		configureServer();
	}

	/**
	 * The method is an extension point to configure jetty server
	 */
	protected void configureServer() {
		context = new ContextHandler("/");
		context.setResourceBase(ServerConfig.webServerWebapp().getAbsolutePath());
		context.getServletContext();

		server = new Server(ServerConfig.webServerHttpPort());
		server.setHandler(context);

		// Specify the Session ID Manager
		SessionIdManager idmanager = new HashSessionIdManager();
		server.setSessionIdManager(idmanager);

		// Create the SessionHandler (wrapper) to handle the sessions
		SessionHandler sessions = new SessionHandler(new HashSessionManager());
		context.setHandler(sessions);

		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(new Z8Handler(context));
		gzipHandler.addIncludedMimeTypes(ServerConfig.webServerGzipMimeTypes());
		gzipHandler.addIncludedMethods(ServerConfig.webServerGzipMethods());
		gzipHandler.addIncludedPaths(ServerConfig.webServerGzipPaths());

		// Put handler inside of SessionHandler
		sessions.setHandler(gzipHandler);
	}

	@Override
	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException("WebServer start failed", e);
		}
	}

	@Override
	public void stop() throws RemoteException {
		super.stop();
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				Trace.logError("Couldn't stop web server", e);
			}
		}
	}

	@Override
	public String id() throws RemoteException {
		return ID;
	}

	@Override
	public void probe() throws RemoteException {}

	public static void launch(ServerConfig config) throws Exception {
		new WebServer().start();
	}
}
