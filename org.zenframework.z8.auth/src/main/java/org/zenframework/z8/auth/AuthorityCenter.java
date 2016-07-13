package org.zenframework.z8.auth;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;

import org.zenframework.z8.server.base.file.Folders;
import org.zenframework.z8.server.config.ServerConfig;
import org.zenframework.z8.server.engine.HubServer;
import org.zenframework.z8.server.engine.IApplicationServer;
import org.zenframework.z8.server.engine.IAuthorityCenter;
import org.zenframework.z8.server.engine.IInterconnectionCenter;
import org.zenframework.z8.server.engine.IServerInfo;
import org.zenframework.z8.server.engine.ISession;
import org.zenframework.z8.server.engine.ServerInfo;
import org.zenframework.z8.server.engine.Session;
import org.zenframework.z8.server.exceptions.AccessDeniedException;
import org.zenframework.z8.server.logs.Trace;
import org.zenframework.z8.server.request.RequestDispatcher;
import org.zenframework.z8.server.security.IUser;
import org.zenframework.z8.server.types.guid;

public class AuthorityCenter extends HubServer implements IAuthorityCenter {
	private static final String serversCache = "authority.center.cache";

	static public String id = guid.create().toString();

	static private AuthorityCenter instance = null;

	private UserManager userManager;
	private SessionManager sessionManager;

	private boolean interconnectionReset = true;

	public static IAuthorityCenter launch(ServerConfig config) throws RemoteException {
		if(instance == null) {
			instance = new AuthorityCenter();
			instance.start();
		}
		return instance;
	}

	private AuthorityCenter() throws RemoteException {
		super(ServerConfig.authorityCenterPort());
	}

	@Override
	public String id() throws RemoteException {
		return id;
	}

	@Override
	public void start() throws RemoteException {
		super.start();

		userManager = new UserManager();

		sessionManager = new SessionManager();
		sessionManager.start();

		enableTimeoutChecking();

		Trace.logEvent("JVM startup options: " + ManagementFactory.getRuntimeMXBean().getInputArguments().toString() + "\n\t" + RequestDispatcher.getMemoryUsage());
	}

	@Override
	public synchronized void register(IApplicationServer server) throws RemoteException {
		addServer(new ServerInfo(server, server.id()));
		registerInterconnection(server);
	}

	@Override
	public synchronized void unregister(IApplicationServer server) throws RemoteException {
		removeServer(server);
		unregisterInterconnection(server);
	}

	@Override
	public IServerInfo[] servers() throws RemoteException {
		return getServers().toArray(new IServerInfo[0]);
	}
	
	private void registerInterconnection(IApplicationServer server) throws RemoteException {
		try {
			interconnectionCenter().register(server);
		} catch(Throwable e) {
			resetInterconnectionCenter();
		}
	}

	private void unregisterInterconnection(IApplicationServer server) throws RemoteException {
		try {
			interconnectionCenter().unregister(server);
		} catch(Throwable e) {
			resetInterconnectionCenter();
		}
	}

	@Override
	public ISession login(String login) throws RemoteException {
		return doLogin(login, null);
	}

	@Override
	public ISession login(String login, String password) throws RemoteException {
		if(password == null)
			throw new AccessDeniedException();

		return doLogin(login, password);
	}

	@Override
	public ISession server(String sessionId, String serverId) throws RemoteException {
		Session session = sessionManager.get(sessionId);
		IServerInfo server = findServer(serverId);

		if(server == null)
			return null;

		session.setServerInfo(server);
		return session;
	}

	private ISession doLogin(String login, String password) throws RemoteException {
		IServerInfo serverInfo = findServer((String)null);

		if(serverInfo == null)
			throw new AccessDeniedException();

		IApplicationServer loginServer = serverInfo.getServer();
		IUser user = password != null ? loginServer.login(login, password) : loginServer.login(login);
		userManager.add(user);

		Session session = sessionManager.create(user);
		session.setServerInfo(serverInfo);
		return session;
	}

	private IServerInfo findServer(String serverId) throws RemoteException {
		IServerInfo[] servers = this.getServers().toArray(new IServerInfo[0]);

		for(IServerInfo server : servers) {
			if(serverId != null && !server.getId().equals(serverId))
				continue;

			if(!server.isAlive()) {
				if(server.isDead())
					unregister(server.getServer());
				continue;
			}

			// меняем порядок, чтобы распределять запросы
			if(serverId == null && this.getServers().size() > 1) {
				this.getServers().remove(server);
				this.getServers().add(server);
			}

			return server;
		}

		return null;
	}

	@Override
	protected File cacheFile() {
		return new File(Folders.Base, serversCache);
	}

	@Override
	protected void timeoutCheck() {
		instance.checkSessions();
		instance.checkConnections();
	}

	private void checkSessions() {
		sessionManager.check();
	}

	private void checkConnections() {
		if(!interconnectionReset)
			return;

		try {
			IInterconnectionCenter interconnectionCenter = interconnectionCenter();

			for(IServerInfo server : getServers()) {
				if(server.isAlive())
					interconnectionCenter.register(server.getServer());
			}

		} catch(Throwable e) {
		}
	}

	private IInterconnectionCenter interconnectionCenter() throws RemoteException {
		IInterconnectionCenter interconnectionCenter = ServerConfig.interconnectionCenter();
		interconnectionReset = false;
		return interconnectionCenter; 
	}

	private void resetInterconnectionCenter() throws RemoteException {
		ServerConfig.resetInterconnectionCenter();
		interconnectionReset = true;
	}
}
