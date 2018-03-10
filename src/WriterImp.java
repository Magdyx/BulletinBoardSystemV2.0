import java.io.IOException;
import java.rmi.RemoteException;


public class WriterImp implements IWriter {
	
	private String writerID;
	private String value;
	private int seqNum;

	@SuppressWarnings("finally")
	@Override
	public String run() throws RemoteException {
		// TODO Auto-generated method stub
		StringBuilder log = new StringBuilder();
		StringBuilder temp = new StringBuilder();


		try {
			
			try {
				
				writeData(value);
				int rSeq = ++Server.rSeq;
				log.append(Integer.toString(rSeq));
				log.append("\t");
				log.append(value);
				log.append("\t");
				log.append(writerID);
				log.append("\n");

				
				temp.append(Integer.toString(seqNum));
				temp.append("\n");
				temp.append(Integer.toString(rSeq));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}finally {
            Server.write = false;
			Server.numberOfWriter--;

			while(true){
				if (!Server.writerLog){
					Server.writerLog = true;
					Server.updateLogWriter(new String(log));
					break;
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			return new String(temp); 
        }
	}

	@Override
	public void writeData(String data) throws RemoteException {
		// TODO Auto-generated method stub
		
		while(true){
			if (!Server.write){
				Server.write = true;
				try {
					long time = (long) (Math.random() * 10000);
				   System.out.println("time sleep writer with id " + writerID + " " + time);
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Server.news = value;
				Server.writeNews(value);
				return;
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void initialize(String writerID, String value)
			throws RemoteException {
		// TODO Auto-generated method stub
		Server.seqNumber++;
		Server.numberOfWriter++;
		this.writerID = writerID;
		this.value = value;
		this.seqNum = Server.seqNumber;
		
	}
	

}
