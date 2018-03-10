import java.io.IOException;
import java.rmi.RemoteException;


public class WriterImp implements IWriter {

	@SuppressWarnings("finally")
	@Override
	public String run(String writerID, String value) throws RemoteException {
		int seqNum = ++Server.seqNumber;

		StringBuilder log = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		try {

			try {

				writeData(value, value, writerID);
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
	public void writeData(String data, String value, String writerID
	) throws RemoteException {
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

}
