
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Reader extends Thread implements IRequest{
	private Socket socket;
	private String readerID;
	private int seqNum;

	public Reader(Socket socket, String readerID , int seqNum) {
		this.socket = socket;
		this.readerID= readerID;
		this.seqNum = seqNum;
	}

	public void run() {
		 StringBuilder log = new StringBuilder();

		try {

			try {
				long time = (long) (Math.random() * 10000);
				System.out.println("time sleep reader with id " + readerID + " " + time);
				 Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			String readedNews = readData();

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			int rSeq = ++Server.rSeq;
			StringBuilder temp = new StringBuilder();
			temp.append(readedNews.replaceAll("\n",""));
			temp.append("\n");
			temp.append(Integer.toString(seqNum));
			temp.append("\n");
			temp.append(Integer.toString(rSeq));
			System.out.println("run seq" + seqNum);
			out.println(new String(temp));

			log.append(Integer.toString(rSeq));
			log.append("\t");
			log.append(readedNews.replaceAll("\n",""));
			log.append("\t");
			log.append(readerID);
			log.append("\t");
			log.append(Integer.toString(Server.numberOfReader));
			log.append("\n");


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
                socket.close();
                Server.numberOfReader--;
				while (true){
                	if (!Server.readerLog){

                		Server.readerLog = true;
						System.out.println(Server.readerLog);
                    	Server.updateLogReader(new String(log));
						System.out.println(Server.readerLog);
						break;
                	}else {
                		try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                }

            } catch (IOException e) {
               // error happen
            }
            // finish
        }

	}

	public String readData() throws InterruptedException {
		// TODO Auto-generated method stub
		// while (true){
			// if (!Server.write){
				return Server.news;
			// }else {
				// Thread.sleep(1000);
			// }
		// }
	}

	public void writeData(String data) {
		// TODO Auto-generated method stub
		// not required here
	}



}
