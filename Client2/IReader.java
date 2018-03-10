import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IReader extends Remote{
	public String run() throws RemoteException;
	public String readData() throws RemoteException;
	public void initialize(String clientID) throws RemoteException;
}
