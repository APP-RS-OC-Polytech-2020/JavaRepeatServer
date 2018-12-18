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
			this.ip = ip;//"193.48.125.71";//ip;
			this.port = Integer.parseInt(port);
			System.out.println("CoFlWebcam\tdebut");
			try{TimeUnit.MILLISECONDS.sleep(500);}catch (InterruptedException e1) {}
			System.out.println("ip:"+this.ip+" port:"+this.port);
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
		this.run();
		
	}
	public void run() {
		serverRobotino.addConnexionFluxWebcam(this);
		String inLine = "";
		byte[] inLineByte;
		System.out.println("CoFlWebcam\tdebut");
		try {
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				//clientSocket.getInputStream().read();
				inLine = in.readLine();
				inLineByte = inLine.getBytes("UTF-8");
				System.out.println("CoFlWebcam\tgetIntputStreamServer2: "+inLine);
				//try{TimeUnit.MILLISECONDS.sleep(1000);}catch (InterruptedException e1) {}
				for (int i = 0; i < serverRobotino.connexionsSendFluxWebcam.size(); i++) {
					System.out.println("CoFlWebcam\tgetIntputStreamServerfor: "+inLine);
					serverRobotino.connexionsSendFluxWebcam.get(i).socketClient.getOutputStream().write(inLineByte, 0, inLineByte.length);
				}
				//this.decodeurJson(inLine);
			}
		} catch (IOException e) {
			serverRobotino.removeConnexionFluxWebcam(this);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
