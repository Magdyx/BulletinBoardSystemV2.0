

import com.jcraft.jsch.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class Start {

	public static void main(String[] arg) {
		/*
		 * 1-read configuration file and fill hashMap with values 2-using
		 * hashmap start server thread on local machine by change directory to
		 * server folder on desktop, compiling and running using make file send
		 * to server number of readers and number of writers 3-use ssh to log in
		 * other hosts as specified in config file, change directory to client
		 * folder on desktop, compile and run using make file
		 */

		String srvHost, srvUser, srvPass;
		int rdCount, wrCount;

		// read configuration
		ConfigFileHandler fh = new ConfigFileHandler();
		HashMap<String, String> props = fh.readConfiguration();
		printConfigFile(props);

		String temp = props.get("srvIp");
		srvUser = temp.substring(0, temp.indexOf("@"));
		srvHost = temp.substring(temp.indexOf("@") + 1);
		srvPass = props.get("srvPass");
		System.out.println("server parameters " + srvUser + " " + srvHost + " "
				+ srvPass);
		ServerThread srvTh = new ServerThread(srvHost, srvUser, srvPass, props);
		Thread myThread = new Thread(srvTh);
		myThread.start();

		// execute clients
		rdCount = Integer.parseInt(props.get("rdCount"));
		wrCount = Integer.parseInt(props.get("wrCount"));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ReadersFactory rf = new ReadersFactory(rdCount, props);
		WritersFactory wf = new WritersFactory(wrCount, rdCount, props);

		Thread readersThread = new Thread(rf);
		Thread writersThread = new Thread(wf);

		readersThread.start();
		writersThread.start();

	}

	private static void createClient(String user, String host, String password,
			String commands[]) {
		Session session = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			for (int it = 0; it < commands.length; it++) {
				Channel channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(commands[it]);

				channel.setInputStream(null);

				((ChannelExec) channel).setErrStream(System.err);

				InputStream in = channel.getInputStream();

				channel.connect();

				byte[] tmp = new byte[1024];
				while (true) {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						if (i < 0)
							break;
						System.out.print(new String(tmp, 0, i));
					}
					if (channel.isClosed()) {
						if (in.available() > 0)
							continue;
						System.out.println("exit-status: "
								+ channel.getExitStatus());
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (Exception ee) {
					}
				}
				channel.disconnect();
			}
			session.disconnect();

		} catch (Exception e) {

		}

	}

	private static void printConfigFile(HashMap<String, String> props) {
		for (String key : props.keySet()) {
			System.out.println(key + " " + props.get(key));
		}
	}

	static class ServerThread implements Runnable {

		private String host;
		private String user;
		private String password;
		private String[] commands;
		private HashMap<String, String> props;

		public ServerThread(String host, String user, String password,
				HashMap<String, String> props) {
			this.host = host;
			this.user = user;
			this.password = password;
			this.props = props;
		}

		public void run() {
			makeServerCommand();
			createFactory();
		}

		private void makeServerCommand() {
			commands = new String[1];
			commands[0] = "cd $HOME/Desktop/server; javac *.java; java MyServer";
			String args = commands[0];
			args += " ";
			args += props.get("srvPort");
			args += " ";
			args += props.get("rdCount");
			args += " ";
			args += props.get("wrCount");
			commands[0] = args;
		}

		private void createFactory() {
			createClient(user, host, password, commands);
		}
	}

	static class ReadersFactory implements Runnable {

		private int rdCount;
		HashMap<String, String> props;

		public ReadersFactory(int rdCount, HashMap<String, String> props) {
			this.props = props;
			this.rdCount = rdCount;
		}

		@Override
		public void run() {
			for (int i = 0; i < rdCount; i++) {
				String temp, user, host, password, args;

				temp = props.get("rd" + i);
				user = temp.substring(0, temp.indexOf('@'));
				host = temp.substring(temp.indexOf('@') + 1);
				password = props.get("rdPass" + i);
				temp = props.get("srvIp");

				String commands[] = { "cd $HOME/Desktop/client;pwd; javac *.java;java MyClient" };

				args = commands[0];
				args += " ";
				args += temp.substring(temp.indexOf("@") + 1); // TODO fix
																// reading srvIp
				args += " ";
				args += props.get("srvPort");
				args += " ";
				args += i; // TODO: fix id
				args += " ";
				args += props.get("acCount");
				args += " ";
				args += "r";
				commands[0] = args;

				createReader(user, host, password, commands);

			}

		}

		private void createReader(String user, String host, String password,
				String[] commands) {
			ClientFactory cf = new ClientFactory(user, host, password, commands);
			Thread myThread = new Thread(cf);
			myThread.start();
		}

	}

	static class WritersFactory implements Runnable {

		private int wrCount, rdCount;
		HashMap<String, String> props;

		public WritersFactory(int wrCount, int rdCount,
				HashMap<String, String> props) {
			this.props = props;
			this.wrCount = wrCount;
			this.rdCount = rdCount;
		}

		@Override
		public void run() {
			for (int i = 0; i < wrCount; i++) {
				String temp, user, host, password, args;
				int id = rdCount + i;
				temp = props.get("wr" + i);
				user = temp.substring(0, temp.indexOf('@'));
				host = temp.substring(temp.indexOf('@') + 1);
				password = props.get("wrPass" + i);

				temp = props.get("srvIp");
				// append args

				String commands[] = { "cd $HOME/Desktop/client; pwd; javac *.java;java MyClient" };
				args = commands[0];
				args += " ";
				args += temp.substring(temp.indexOf("@") + 1);
				args += " ";
				args += props.get("srvPort");
				args += " ";
				args += id; // TODO: fix id
				args += " ";
				args += props.get("acCount");
				args += " ";
				args += "w";
				commands[0] = args;

				createWriter(user, host, password, commands);

			}

		}

		private void createWriter(String user, String host, String password,
				String[] commands) {
			ClientFactory cf = new ClientFactory(user, host, password, commands);
			Thread myThread = new Thread(cf);
			myThread.start();
		}

	}

	private static class ClientFactory implements Runnable {

		private String user, host, password, commands[];

		public ClientFactory(String user, String host, String password,
				String commands[]) {
			this.user = user;
			this.host = host;
			this.password = password;
			this.commands = commands;
		}

		@Override
		public void run() {
			createClient(user, host, password, commands);
		}

	}

	private static class ConfigFileHandler {
		private static final String CONFIG_FILE = "system.properities";

		/**
		 * read configuration file system.properities and sets global variables
		 */
		public HashMap<String, String> readConfiguration() {
			HashMap<String, String> props = new HashMap<String, String>();
			BufferedReader bf = null;

			try {
				bf = new BufferedReader(new FileReader(CONFIG_FILE));
				String sCurrentLine;

				while ((sCurrentLine = bf.readLine()) != null) {
					System.out.println(sCurrentLine);
					int splitIndex = sCurrentLine.indexOf('=');
					String key = sCurrentLine.substring(0, splitIndex);
					String value = sCurrentLine.substring(splitIndex + 1);
					key = reformulateKeyString(key);
					props.put(key, value);
				}
				bf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return props;
		}

		private String reformulateKeyString(String key) {
			char num;
			String res;
			boolean hasNum;
			HashMap<String, String> mappingKeys;

			mappingKeys = getMappingKeys();
			hasNum = checkStrHasNo(key);

			if (hasNum) {
				num = key.charAt(key.length() - 1);
				res = mappingKeys.get(key.substring(0, key.length() - 1));
				res += num;
			} else {
				res = mappingKeys.get(key);
			}
			return res;
		}

		private HashMap<String, String> getMappingKeys() {
			HashMap<String, String> mappingKeys = new HashMap<String, String>();
			mappingKeys.put("RW.server", "srvIp");
			mappingKeys.put("RW.server.port", "srvPort");
			mappingKeys.put("RW.server.password", "srvPass");
			mappingKeys.put("RW.numberOfReaders", "rdCount");
			mappingKeys.put("RW.numberOfWriters", "wrCount");
			mappingKeys.put("RW.numberOfAccesses", "acCount");
			mappingKeys.put("RW.reader", "rd");
			mappingKeys.put("RW.writer", "wr");
			mappingKeys.put("RW.password.reader", "rdPass");
			mappingKeys.put("RW.password.writer", "wrPass");

			return mappingKeys;
		}

		private boolean checkStrHasNo(String txt) {
			return txt.matches(".*\\d+.*");
		}
	}

}
