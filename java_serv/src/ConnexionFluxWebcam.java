import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;


public class ConnexionFluxWebcam implements Runnable {
	private ServerRobotino serverRobotino;
	PrintWriter out;
	BufferedReader in;
	Socket clientSocket;
	String ip="";//iplocal
	int port=50009;//iplocal
	public ConnexionFluxWebcam(ServerRobotino serverRobotino, String ip, String port){
		try {
			this.serverRobotino=serverRobotino;
			this.ip = ip;
			this.port = Integer.parseInt(port);
			System.out.println("CoFlWebcam\tdebut");
			//try{TimeUnit.MILLISECONDS.sleep(1000);}catch (InterruptedException e1) {}
			clientSocket = new Socket(this.ip, this.port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void run() {
		String inLine = "";
		System.out.println("CoFlWebcam\tdebut");
		try {
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoFlWebcam\tgetIntputStreamServer2: "+inLine);
				//this.decodeurJson(inLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
