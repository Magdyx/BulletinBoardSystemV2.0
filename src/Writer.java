
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Writer extends Thread implements IRequest {

	private Socket socket;
	private String writerID;
	private String value;
	private int seqNum;


	public Writer(Socket socket, String readerID, String value , int seqNum) {
		this.socket = socket;
		this.writerID = readerID;
		this.value = value;
		this.seqNum = seqNum;
	}


	public void run() {
		// TODO Auto-generated method stub

		StringBuilder log = new StringBuilder();



		try {
			PrintWriter out;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				writeData(value);
				int rSeq = ++Server.rSeq;
				log.append(Integer.toString(rSeq));
				log.append("\t");
				log.append(value);
				log.append("\t");
				log.append(writerID);
				log.append("\n");

				StringBuilder temp = new StringBuilder();
				temp.append(Integer.toString(seqNum));
				temp.append("\n");
				temp.append(Integer.toString(rSeq));
				out.println(new String(temp));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
                socket.close();
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

            } catch (IOException e) {
               // error happen
            }
            // finish
        }


	}

	public String readData() throws InterruptedException{
		// TODO Auto-generated method stub
		return null;
	}

	public void writeData(String data) throws InterruptedException {
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
				Thread.sleep(1000);
			}
		}
	}



}
