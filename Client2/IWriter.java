import java.rmi.Remote;
import java.rmi.RemoteException;

import sun.security.util.PropertyExpander.ExpandException;


public interface IWriter extends Remote{
	public String run(String writerID, String value) throws RemoteException;
	public void writeData(String data, String value, String writerID) throws RemoteException;

}
