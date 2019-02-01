import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ConnexionFluxWebcam implements Runnable {
	private ServerRobotino serverRobotino;
	WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam;
	PrintWriter out;
	BufferedReader in;
	Socket clientSocket;
	String ip="";//iplocal
	int port=50009;//iplocal
	String name = "";
	public ConnexionFluxWebcam(ServerRobotino serverRobotino, String ip, String port, String name, WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam){
		try {
			//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			this.serverRobotino=serverRobotino;
			this.waitNewConnexionSendFluxWebcam=waitNewConnexionSendFluxWebcam;
			this.ip = ip;//"193.48.125.71";//ip;
			this.port = Integer.parseInt(port);
			this.name = name;
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
		//this.run();
		
	}
	public void run() {
		serverRobotino.addConnexionFluxWebcam(this);
		String inLine = "";
		/*try {
			inLine = in.readLine();
			System.out.println(""+inLine);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		//CharBuffer inLineCharBuffer = null;
		byte[] inLineByte;
		byte b;
		InputStream buf = null;
		try {
			buf = clientSocket.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("CoFlWebcam\tStream en cour");
		try {
			//System.out.println("waitNewConnexionSendFluxWebcam"+waitNewConnexionSendFluxWebcam);
			//System.out.println("waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam"+waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam);
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				//clientSocket.getInputStream().read();
				//inLine = in.readLine();
				inLineByte = new byte[250];
				//inLineByte=null;
				for (int i = 0; i < 250; i++) {
			        b = (byte) buf.read();
			        //System.out.println(i);
					inLineByte[i]=b;
				}
		        //b = (byte) buf.read();
				//in.read(inLineCharBuffer);
				//inLineByte = inLine.getBytes("UTF-8");
				//inLineByte = inLine.getBytes();
				/*char[] inLineByteChar = new char[inLineByte.length];
				for (int i = 0; i < inLineByte.length; i++) {
					inLineByteChar[i]= (char)inLineByte[i];
					//System.out.println("CoFlWebcam\t"+i+" : "+inLineByteChar[i]);
				}*/
				//System.out.write(inLineByte);
				//System.out.println("CoFlWebcam\tgetIntputStreamServer2: "+inLine);
				//try{TimeUnit.MILLISECONDS.sleep(1000);}catch (InterruptedException e1) {}
				//for (int i = 0; i < serverRobotino.connexionsSendFluxWebcam.size(); i++) {
				//System.out.println("waitNewConnexionSendFluxWebcam"+waitNewConnexionSendFluxWebcam);
				//System.out.println("waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam"+waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam);
				try {
					for (int i = 0; i < waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam.size(); i++) {
						//System.out.write(inLineByte);
						//serverRobotino.connexionsSendFluxWebcam.get(i).socketClient.getOutputStream().write(inLineByte, 0, inLineByte.length);
						waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam.get(i).socketClient.getOutputStream().write(inLineByte, 0, inLineByte.length);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					System.out.print("i");
				}
				//this.decodeurJson(inLine);
			}
			System.out.println("Stop ConnexionFluxWebcam");
		} catch (IOException e) {
			serverRobotino.removeConnexionFluxWebcam(this);
			System.out.println("Crash ConnexionFluxWebcam");
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				clientSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
