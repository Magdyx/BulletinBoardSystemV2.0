import java.rmi.Remote;
import java.rmi.RemoteException;

import sun.security.util.PropertyExpander.ExpandException;


public interface IWriter extends Remote{
	public String run() throws RemoteException;
	public void writeData(String data) throws RemoteException;
	public void initialize(String writerID, String value) throws RemoteException;

	
}
