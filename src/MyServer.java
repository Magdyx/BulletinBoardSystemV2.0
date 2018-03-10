

public class MyServer {
	public static void main(String[] args) throws Exception {
		Server s = new Server();
		try{
			s.run(args);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
}
