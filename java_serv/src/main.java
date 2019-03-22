/**
 * Execute le server
 * @author lalandef
 *
 */
public class main {

	public static void main(String[] args) {
		int port= 50008; //configIO.getJavaPort() ;
		ServerRobotino server = new ServerRobotino();
		server.setPortServeur(port);
		server.run();
	}

}
