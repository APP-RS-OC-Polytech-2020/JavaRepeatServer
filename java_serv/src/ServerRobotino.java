import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;

/**
 * Classe qui represente le serveur principal.
 * Tout le monde s'y connecte, il redistribue les messages, 
 * ouvre les connexions qui vont bien toussa.
 * 
 *
 */
public class ServerRobotino {
	private int portServeur;
	private ServerSocket socketServer = null;
	private ArrayList<ConnexionJava> connexionsJava = new ArrayList<ConnexionJava>();
	private ArrayList<ConnexionRobotino> connexionsRobotino = new ArrayList<ConnexionRobotino>();
	public ArrayList<ConnexionWeb> connexionsWeb = new ArrayList<ConnexionWeb>();
	public ArrayList<ConnexionFluxWebcam> connexionsFluxWebcam = new ArrayList<ConnexionFluxWebcam>();
	public ArrayList<ConnexionSendFluxWebcam> connexionsSendFluxWebcam = new ArrayList<ConnexionSendFluxWebcam>();
	//private ArrayList<Connexion> connexionsRobotino = new ArrayList<Connexion>();

	//private Thread t1;
	private boolean serverRunning = true;
	public ServerRobotino(int port) {
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
		this.waitNewConnexion();
	}
	/**
	 * Attend les connexions puis les analyse pour savoir si c'est une connexion web, robotino où autre
	 */
	private void waitNewConnexion() {
		try {
			System.out.println("Server lancé");
			while(serverRunning){
				Socket socketClient = socketServer.accept();//Quelque chose essai de se connecter
				//System.out.println("CoSR\ttest: ");
				BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));;
				String firstLine = in.readLine();
				System.out.println("CoSR\tfirstLine: "+firstLine);
				if(serverRunning){
					if(firstLine.startsWith("{")){//connexion classique avec reception d'un JSON
						try{
							JSONObject JSON = new JSONObject(firstLine);
							String type = JSON.getString("type");
							
							if(type.equals("init")){
								String clientType = JSON.getString("clientType");
								if(clientType.equals("Java")){//connexion d'un client Java
									new Thread(new ConnexionJava(this,socketClient,firstLine,in)).start();
								}else if(clientType.equals("Robotino")){//connexion d'un Robotino
									new Thread(new ConnexionRobotino(this,socketClient,firstLine,in)).start();
								}else if(clientType.equals("Webcam")){//connexion d'une webcam donnant du contenu
									new Thread(new ConnexionWebcam(this,socketClient,firstLine,in)).start();
								}else if(clientType.equals("GetFluxWebcam")){//connexion d'une webcam donnant du contenu
									new Thread(new ConnexionSendFluxWebcam(this,socketClient,firstLine,in)).start();
								}else{//type de client non reconu
									PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
									out.println("L( Â¨Â° 3Â¨Â° )J----#:`* no socket for u");
									socketClient.close();
								}
							}else if(type.equals("capteurs")){//connexion d'une webcam donnant du contenu
								System.out.println("CoSR\tCapteurs: ");
								System.out.println("CoSR\tJSON: "+firstLine);
								sendToAllWeb(firstLine);
							}else{//JSON invalide
								PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
								out.println("L( Â¨Â° 3Â¨Â° )J----#:`* no socket for u");
								socketClient.close();
							}
						}catch(org.json.JSONException e){//JSON non valide
							System.out.println("CoSR\tConexion non valide: ");
							System.out.println("CoSR\tJSON: "+firstLine);
							PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
							out.println("L( Â¨Â° 3Â¨Â° )J----#:`* no socket for u");
							socketClient.close();
						}
					}else if(firstLine.startsWith("GET")){ //Ca commence par GET, c'est une Websocket
						firstLine = in.readLine();
						System.out.println("CoSR\tfirstLine: "+firstLine);
						firstLine = in.readLine();
						System.out.println("CoSR\tfirstLine: "+firstLine);;
						if(firstLine.startsWith("Connection: Upgrade")){
							new Thread(new ConnexionWeb(this,socketClient,firstLine,in)).start();
						}else if(firstLine.startsWith("Connection: keep-alive")){
							new Thread(new ConnexionSendFluxWebcam(this,socketClient,firstLine,in)).start();
						}
					}
					else{
						PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
						System.out.println("SR\tErreur message d'entrée: "+firstLine);
						out.println("L( Â¨Â° 3Â¨Â° )J----#:`* no socket for u");
						socketClient.close();
					}
				}else{
					new PrintWriter(socketClient.getOutputStream(), true).println("Connexion canceled cause server is stopping");
					socketClient.close();
				}// End if serverIsRunning
			}// End while
		} catch (IOException e) {
			System.out.println("Arrêt d'éoute de nouvelle connexion");
			//e.printStackTrace();//affiche erreur en cas d'arrêt forcé
		}
	}
	public boolean isServerRunning() {
		return serverRunning;
	}
	public void setServerRunning(boolean serverRunning) {
		this.serverRunning = serverRunning;
	}
	public synchronized void addConnexionJava(ConnexionJava connexion) {
		this.connexionsJava.add(connexion);
	}
	public synchronized void removeConnexionJava(ConnexionJava connexion) {
		this.connexionsJava.remove(connexion);
	}
	public synchronized void addConnexionRobotino(ConnexionRobotino connexion) {
		this.connexionsRobotino.add(connexion);
	}
	public synchronized void removeConnexionRobotino(ConnexionRobotino connexion) {
		this.connexionsRobotino.remove(connexion);
	}
	public synchronized void addConnexionWeb(ConnexionWeb connexion) {
		this.connexionsWeb.add(connexion);
	}
	public synchronized void removeConnexionWeb(ConnexionWeb connexion) {
		this.connexionsWeb.remove(connexion);
	}
	public synchronized void addConnexionFluxWebcam(ConnexionFluxWebcam connexionsFluxWebcam) {
		this.connexionsFluxWebcam.add(connexionsFluxWebcam);
	}
	public synchronized void removeConnexionFluxWebcam(ConnexionFluxWebcam connexionsFluxWebcam) {
		this.connexionsFluxWebcam.remove(connexionsFluxWebcam);
	}
	public synchronized void addConnexionSendFluxWebcam(ConnexionSendFluxWebcam connexionsSendFluxWebcam) {
		this.connexionsSendFluxWebcam.add(connexionsSendFluxWebcam);
	}
	public synchronized void removeConnexionSendFluxWebcam(ConnexionSendFluxWebcam connexionsSendFluxWebcam) {
		this.connexionsSendFluxWebcam.remove(connexionsSendFluxWebcam);
	}
	
	/**
	 * Envoie un message à tout les robotino
	 * @param m message JSON
	 */
	public synchronized void sendToAllRobotino(String m) {
		for (int i = 0; i < connexionsRobotino.size(); i++) {
			connexionsRobotino.get(i).envoyerMessage(m);
		}
	}
	
	/**
	 * Envoie un message à un robotino
	 * @param m message JSON
	 * @param ip ip du robotino
	 */
	public synchronized void sendToOneRobotino(String m, String ip) {
		boolean found=false;
		for (int i = 0; i < connexionsRobotino.size(); i++) {
			if(connexionsRobotino.get(i).ipRobot.equals(ip)){
				connexionsRobotino.get(i).envoyerMessage(m);
				System.out.println("Envoyé à "+ip);
				found=true;
			}
		}
		if(!found){
			System.out.println("Aucun robot trouvé");
		}
	}
	
	/**
	 * Envoie un message à toute les connexions web
	 * @param m message JSON
	 */
	public synchronized void sendToAllWeb(String m) {
		for (int i = 0; i < connexionsWeb.size(); i++) {
			connexionsWeb.get(i).envoyerMessage(m);
		}
	}
	
	/**
	 * Renvoie le lien de la caméra du robot
	 * @param ipRobot
	 * @return
	 */
	//Imcomplête
	public synchronized String getLinkVideoRobot(String ipRobot) {
		for (int i = 0; i < connexionsRobotino.size(); i++) {
			//if(connexionsRobotino.get(i).ipRobot.equals(ipRobot)){
			if("test".equals(ipRobot)){
				return connexionsRobotino.get(i).linkVideo;
			}
		}
		//return "http://tp-epu.univ-savoie.fr/~prospere/img.png";
		return "193.48.125.71:1337";
	}
}
