
public class main {

	public static void main(String[] args) {
		int port= configIO.getJavaPort() ;
		ServerRobotino server = new ServerRobotino(port);
	}

}
