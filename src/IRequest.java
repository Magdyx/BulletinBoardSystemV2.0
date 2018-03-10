import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IRequest  extends Remote {

	public String readData() throws InterruptedException, RemoteException;
	public void writeData(String data) throws InterruptedException, RemoteException;

}
