
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface IClient {

	public void doOperation(Registry reg) throws RemoteException, NotBoundException;
	public void run(String[] args);
	public void writeLog(String value , String seqNum , String rSeq);
	public void openLogsFile();
}
