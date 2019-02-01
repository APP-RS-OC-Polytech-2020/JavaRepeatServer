import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONObject;


public class WaitNewConnexionSendFluxWebcam implements Runnable {
	private int portServeur;
	public String webcamName="";
	public ServerRobotino serverRobotino;
	private ServerSocket socketServer = null;
	public ArrayList<ConnexionSendFluxWebcam> connexionsSendFluxWebcam = new ArrayList<ConnexionSendFluxWebcam>();

	//private Thread t1;
	private boolean serverRunning = true;
	public WaitNewConnexionSendFluxWebcam(int port,ServerRobotino serverRobotino,String webcamName,String ipWebcam,String portWebcam) {
		//new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, webcamName, this);
		serverRobotino.addWaitNewConnexionSendFluxWebcam(this);
		//System.out.println("WNCSFW\ttest1");
		new Thread(new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, webcamName, this)).start();
		//System.out.println("WNCSFW\ttest2");
		this.serverRobotino = serverRobotino;
		this.webcamName=webcamName;
		try {
			this.portServeur=port;
			//ip = InetAddress.getLocalHost ().getHostAddress ();
			//nom = "Server Robotion v1";
			socketServer = new ServerSocket(this.portServeur);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//this.waitNewConnexion();
		//System.out.println("WNCSFW\ttest3");
	}
	/**
	 * Attend les connexions qui veulent recevoir le flux d'une webcam
	 */
	public void run() {
		//System.out.println("WNCSFW\tEcoute de nouvelle connexion à "+socketServer.getInetAddress()+":"+portServeur+" pour la webcam:"+webcamName);
		while(serverRobotino.isServerRunning()){
			try {
				//System.out.println("WNCSFW\twaitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam.size()"+this.connexionsSendFluxWebcam.size());

				Socket socketClient = socketServer.accept();//Quelque chose essai de se connecter
				//System.out.println("WNCSFW\tNouvelle connexion");
				//new Thread(new ConnexionSendFluxWebcam(this,socketClient)).start();
				new Thread(new ConnexionSendFluxWebcam(this,socketClient)).start();
				//System.out.println("WNCSFW\t222waitNewConnexionSendFluxWebcam.connexionsSendFluxWebcam.size()"+this.connexionsSendFluxWebcam.size());

			} catch (IOException e) {
				System.out.println("Arrêt d'écoute de nouvelle connexion");
				//e.printStackTrace();//affiche erreur en cas d'arrêt forcé
			}
		}
	}
	public synchronized void addConnexionSendFluxWebcam(ConnexionSendFluxWebcam connexionsSendFluxWebcam) {
		System.out.println("Test addConnexion");

		this.connexionsSendFluxWebcam.add(connexionsSendFluxWebcam);
	}
	public synchronized void removeConnexionSendFluxWebcam(ConnexionSendFluxWebcam connexionsSendFluxWebcam) {
		this.connexionsSendFluxWebcam.remove(connexionsSendFluxWebcam);
	}
}