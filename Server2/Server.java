
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import sun.rmi.server.UnicastRef;

public class Server implements IServer {

	public volatile static int seqNumber = 0;
	public volatile static int numberOfReader = 0;
	public volatile static int numberOfWriter = 0;
	public volatile static String news;
	public volatile static boolean write = false;
	public volatile static boolean writerLog = false;
	public volatile static boolean readerLog = false;
	public volatile static	int rSeq = 0;
	private static BufferedWriter bwReaderServer;
	private static BufferedWriter bwWriterServer;
	private FileWriter fwReaderServer;
	private FileWriter fwWriterServer;
	private int portNumber;
	private int totalNumberOfReader;
	private int totalNumberOfWriter;
	private int rmiPort;

	public void run(String[] args) throws Exception {
		// TODO Auto-generated method stub

		portNumber = Integer.parseInt(args[0]);
		totalNumberOfReader = Integer.parseInt(args[1]);
		totalNumberOfWriter = Integer.parseInt(args[2]);
		rmiPort = Integer.parseInt(args[3]);
		System.out.println("dfsd" + args[0] + args[1] + args[2] + " " + rmiPort);
		/**
		 * read news from file
		 * */
		readNews();

		/**
		 * open log reader and writer file
		 * */

		openLogsFile();
		
		try{
			ReaderImp readerRemoteObj = new ReaderImp();
			WriterImp writerRemoteObj = new WriterImp();
			
			LocateRegistry.createRegistry(rmiPort);
			
			IReader stubRd = (IReader) UnicastRemoteObject.exportObject(readerRemoteObj, portNumber);
			IWriter stubWr = (IWriter) UnicastRemoteObject.exportObject(writerRemoteObj, portNumber);
			
			Registry reg = LocateRegistry.getRegistry(rmiPort);
			
			reg.rebind("IReader", stubRd);
			reg.rebind("IWriter", stubWr);
			
			System.err.println("Server Ready");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		

	}

	public static void updateLogReader(String newData) {
		System.out.println(newData);
		try {
		    Files.write(Paths.get("ServerReaderLog.txt"), newData.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    e.printStackTrace();
		}
		finally {
			 Server.readerLog = false;
		}
	}

	public static void updateLogWriter(String newData) {
		try {
			Files.write(Paths.get("ServerWriterLog.txt"), newData.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("before");
			Server.writerLog = false;
			System.out.println("done");
		}
	}

	public void readNews() {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(
					"news.txt"));

			byte[] datainBytes = new byte[dis.available()];
			dis.readFully(datainBytes);
			dis.close();

			String content = new String(datainBytes, 0, datainBytes.length);

			System.out.println(content);
			news = content;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void writeNews(String news2) {
		final String FILENAME = "news.txt";

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			String content = news2;

			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);
			bw.write(content);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void openLogsFile() {
		final String FILENAMEREADER = "ServerReaderLog.txt";
		final String FILENAMEWRITER = "ServerWriterLog.txt";

		try {
			fwReaderServer = new FileWriter(FILENAMEREADER);
			bwReaderServer = new BufferedWriter(fwReaderServer);
			bwReaderServer.write("sSeq\toVal\trID\trNum\n");
			fwWriterServer = new FileWriter(FILENAMEWRITER);
			bwWriterServer = new BufferedWriter(fwWriterServer);
			bwWriterServer.write("sSeq\toVal\twID\n");
			System.out.println("samir");

		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			try{
			if (bwReaderServer != null)
				bwReaderServer.close();
			if (fwReaderServer != null)
				fwReaderServer.close();
			if (bwWriterServer != null)
				bwWriterServer.close();
			if (fwWriterServer != null)
				fwWriterServer.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
