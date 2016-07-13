package org.zenframework.z8.server.engine;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import org.zenframework.z8.server.config.ServerConfig;
import org.zenframework.z8.server.logs.Trace;

public abstract class RmiServer implements IServer, Remote {
	private TimeoutChecker timeoutChecker;

	static public String serverName(Class<?> cls) {
		Class<?> subinterface = IServer.class;

		if(cls.isInterface() && subinterface.isAssignableFrom(cls))
			return cls.getSimpleName();
			
		Class<?>[] interfaces = cls.getInterfaces();
		
		for(Class<?> i : interfaces) {
			if(subinterface.isAssignableFrom(i))
				subinterface = i;
		}
		
		if(subinterface == IServer.class)
			throw new RuntimeException("Class '" + cls.getCanonicalName() + "' does not implement subinterface of '" + IServer.class.getCanonicalName() + "'");

		return subinterface.getSimpleName();
	}

	protected RmiServer(int port) throws RemoteException {
		if(ServerConfig.rmiEnabled())
			export(port);
	}

	private void export(int port) throws RemoteException {
		while(!safeExport(port))
			port++;
	}
	
	private void unexport() {
		try {
			UnicastRemoteObject.unexportObject(this, true);
		} catch(NoSuchObjectException e) {
			Trace.logError(e);
		}
	}

	private boolean safeExport(int port) throws RemoteException {
		try {
			UnicastRemoteObject.exportObject(this, port);
			return true;
		} catch(ExportException e) {
			return false;
		}
	}
	
	protected void enableTimeoutChecking() {
		timeoutChecker = new TimeoutChecker(this, getClass().getSimpleName() + " Timeout Thread");
		timeoutChecker.start();
	}
	
	protected void timeoutCheck() {
	}
	
	@Override
	public String name() throws RemoteException {
		return serverName(getClass());
	}

	@Override
	public void probe() throws RemoteException {
	}

	@Override
	public void start() throws RemoteException {
		Rmi.register(this);
	}

	@Override
	public void stop() throws RemoteException {
		if(timeoutChecker != null)
			timeoutChecker.destroy();

		unexport();
		Rmi.unregister(this);
	}
}


class TimeoutChecker extends Thread {
	
	private RmiServer server;
	
	public TimeoutChecker(RmiServer server, String name) {
		super(name);
		
		this.server = server;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				server.timeoutCheck();
	
				if(Thread.interrupted())
					return;
			
				Thread.sleep(30000);
			} catch(InterruptedException e) {
				return;
			}
		}
	}

	public void destroy() {
		interrupt();
	}
}
