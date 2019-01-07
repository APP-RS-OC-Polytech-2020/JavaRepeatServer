import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ConnexionSendFluxWebcam  implements Runnable {
	private ServerRobotino serverRobotino;
	public Socket socketClient;
	public PrintWriter out;
	private BufferedReader in;
	public String ipWeb;
	public ConnexionSendFluxWebcam(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			this.serverRobotino=serverRobotino;
			this.socketClient=socketClient;
			System.out.println("firstLine: "+firstLine);
			//out.println("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println("HTTP/1.0 200 OK");
		out.println("Connection: close");
		out.println("Max-Age: 0");
		out.println("Expires: 0");
		out.println("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
		out.println("Content-Type: multipart/x-mixed-replace; boundary=stream");
		out.println("");

	}
	public void run() {
		serverRobotino.addConnexionSendFluxWebcam(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoRobo\tgetIntputStreamServer2: "+inLine);
				//this.decodeurJson(inLine);
			}
		} catch (IOException e) {/*e.printStackTrace();*/}//connexion fermé
		System.out.println("CoRobo\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionSendFluxWebcam(this);
	}
	public void envoyerBytes(byte[] bytes){
		try {
			socketClient.getOutputStream().write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
