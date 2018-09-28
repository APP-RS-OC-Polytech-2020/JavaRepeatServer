/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;*/

public class mainRobotinoTest {

	public mainRobotinoTest() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		int port=50007;
		String ipServer="193.48.125.70";
		SocketRobotino socketRobotino = new SocketRobotino(ipServer, port);
		new Thread(socketRobotino).start();
		/*PrintWriter out;
		BufferedReader in;
		Socket clientSocket;
		String ipServer="192.168.56.1";//iplocal
		//ipServer="193.48.125.70";
		//ipServer="193.48.125.219";
		//new Thread(new Client(ipServer,port,"C1")).start();
		//Min + (Math.random() * (Max - Min))
		//ClientUtilisateur c1 = new ClientUtilisateur(ipServer,port,"C1_A"+(int)(Math.random() * (100000)));
		//new Thread(c1).start();
		try {
			clientSocket = new Socket(ipServer, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out.println("{\"type\":\"init\",\"infoInit\":\"Client-->Server  demande de connexion\", \"clientName\": \""+""+"\", \"clientType\":\"Robotino\"}");
			out.println("{\"type\":\"message\",\"message\":\"testRobotino\"}");
			//try {TimeUnit.MILLISECONDS.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
			String inLine="";
			while(inLine!=null){//&&inLine!=null){//lecture des nouveau message
				//try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
				inLine = in.readLine();
				System.out.println("client\tgetIntputStreamServer: "+inLine);
			}
			//clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(""+this.nom+"\tgetOutputStream: "+clientSocket.getOutputStream());
		 */
	}
}