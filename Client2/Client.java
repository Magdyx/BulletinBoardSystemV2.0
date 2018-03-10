
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client implements IClient {

	private String serverIP;
	private String clientID;
	private int numberOfAccess;
	private boolean readOrWriter; // false if read
	private static BufferedWriter bwLogClient;
	private FileWriter fwLogClient;
	private int rmiPort;

	public void doOperation(Registry reg )  throws RemoteException, NotBoundException{

		if (!readOrWriter){ // reader
			IReader stubRd = (IReader) reg.lookup("IReader");

			stubRd.initialize(clientID);
			String temp = stubRd.run();
			System.out.println("This is temp Reader" + temp);
			String args[] = temp.split("\n");

			String value  = args[0];
			String seqNum = args[1];
			String rSeq = args[2];
			System.out.println("hehere" + value + "\nsamir " + seqNum + "\nrSeq " + rSeq);
			writeLog(value  , rSeq ,  seqNum );

		}else { // writer

			IWriter stubRd = (IWriter) reg.lookup("IWriter");

			stubRd.initialize(clientID, clientID);
			String temp = stubRd.run();
			System.out.println("This is temp Writer" + temp);
			String args[] = temp.split("\n");

			String seqNum = args[0];
			String rSeq = args[1];
			writeLog("", rSeq ,  seqNum);
		}
	}

	public void run(String[] args) {
		// TODO Auto-generated method stub
		serverIP = args[0];
//		serverPort = Integer.parseInt(args[1]);
		clientID = args[2];
		numberOfAccess = Integer.parseInt(args[3]);
	    String temp = args[4];
		rmiPort = Integer.parseInt(args[5]);


	    if (temp.equals("r")){
	    	readOrWriter = false;
	    }else {
	    	readOrWriter = true;
	    }

		try {
			openLogsFile();
			Registry reg = null;
			try {
				reg = LocateRegistry.getRegistry(serverIP, rmiPort);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while (numberOfAccess != 0){

				try{

					doOperation(reg);
				} catch( Exception e) {
					e.printStackTrace();
				}

				numberOfAccess--;

				// sleep
				try {
//					socket.close();
					if (numberOfAccess != 0){
						long time = (long) (Math.random() * 10000);
						System.out.println("time sleep client type " + temp + " id " + clientID + " " + time);
						 Thread.sleep(time);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} finally{
			try {
//				if (numberOfAccess != 0)socket.close();
				if (bwLogClient != null)
					bwLogClient.close();
				if (fwLogClient != null)
					fwLogClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	public void writeLog(String value , String seqNum , String rSeq) {
		// TODO Auto-generated method stub
		if (!readOrWriter){//reader
			String newData = rSeq+"\t"+seqNum+"\t"+value+"\n";
			try {
				Files.write(Paths.get("log"+clientID+".txt"), newData.getBytes(), StandardOpenOption.APPEND);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			String newData = rSeq+"\t"+seqNum+"\n";
			try {
				Files.write(Paths.get("log"+clientID+".txt"), newData.getBytes(), StandardOpenOption.APPEND);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void openLogsFile() {
		// TODO Auto-generated method stub

		final String FILENAMEREADER = "log" + clientID +".txt";

		try {
			fwLogClient = new FileWriter(FILENAMEREADER);
			bwLogClient = new BufferedWriter(fwLogClient);
			if (!readOrWriter){ // reader
				bwLogClient.write("rSeq\tsSeq\toVal\n");
			}else{
				bwLogClient.write("rSeq\tsSeq\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if (bwLogClient != null)
					bwLogClient.close();
				if (fwLogClient != null)
					fwLogClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
