import java.io.IOException;
import java.rmi.RemoteException;

public class ReaderImp implements IReader {

	@SuppressWarnings("finally")
	@Override
	public String run(String readerID) throws RemoteException {
		int seqNum = ++Server.seqNumber;
		Server.numberOfReader++;

		StringBuilder log = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		try {

			try {
				long time = (long) (Math.random() * 10000);
				System.out.println("time sleep reader with id " + readerID
						+ " " + time);
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String readedNews = readData();

			int rSeq = ++Server.rSeq;
			temp.append(readedNews.replaceAll("\n", ""));
			temp.append("\n");
			temp.append(Integer.toString(seqNum));
			temp.append("\n");
			temp.append(Integer.toString(rSeq));
			// System.out.println("run seq" + seqNum);
			// out.println(new String(temp));

			log.append(Integer.toString(rSeq));
			log.append("\t");
			log.append(readedNews.replaceAll("\n", ""));
			log.append("\t");
			log.append(readerID);
			log.append("\t");
			log.append(Integer.toString(Server.numberOfReader));
			log.append("\n");


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Server.numberOfReader--;
			while (true) {
				if (!Server.readerLog) {

					Server.readerLog = true;
					// System.out.println(Server.readerLog);
					Server.updateLogReader(new String(log));
					// System.out.println(Server.readerLog);
					break;
				} else {
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
	public String readData() throws RemoteException {
		 return Server.news;
	}
}
