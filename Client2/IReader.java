import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IReader extends Remote{
	public String run(String clientID) throws RemoteException;
	public String readData() throws RemoteException;
}
