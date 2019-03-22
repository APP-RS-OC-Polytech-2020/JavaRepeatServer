import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import picocli.CommandLine.Option;

/**
 * Classe qui represente le serveur principal.
 * Tout le monde s'y connecte, il redistribue les messages, 
 * ouvre les connexions qui vont bien toussa.
 */
public class ServerRobotino implements Runnable {
	private ServerSocket socketServer = null;
	private ArrayList<ConnexionJava> connexionsJava = new ArrayList<ConnexionJava>();
	private ArrayList<ConnexionRobotino> connexionsRobotino = new ArrayList<ConnexionRobotino>();
	private ArrayList<ConnexionArduinoRobotino> connexionsArduinoRobotino = new ArrayList<ConnexionArduinoRobotino>();
	public ArrayList<ConnexionWeb> connexionsWeb = new ArrayList<ConnexionWeb>();
	public ArrayList<ConnexionFluxWebcam> connexionsFluxWebcam = new ArrayList<ConnexionFluxWebcam>();
	public ArrayList<WaitNewConnexionSendFluxWebcam> waitNewConnexionSendFluxWebcams = new ArrayList<WaitNewConnexionSendFluxWebcam>();
	public ArrayList<ConnexionSensorsDatabase> connexionsSensorsDatabase = new ArrayList<ConnexionSensorsDatabase>();
	HashMap<String,Integer> mapNameWebcam_Port = new HashMap<String, Integer>();
	private boolean serverRunning;
	
	//Config stuff
	@Option(names = {"--portDisp","-pd"}, description = "Sets which port is available for camera restreaming. Needs to have the following port open as well for multiple cameras (default: ${DEFAULT-VALUE}).")
	public int portDispo = 50010;
	@Option(names = {"--port","-p"}, description = "Sets on which port the server will listen for incoming connexions (default: ${DEFAULT-VALUE}). ")
	private int portServeur = 50008;
	
	public ServerRobotino() {}
	
	public void run(){
		try {
			//ip = InetAddress.getLocalHost ().getHostAddress ();
			//nom = "Server Robotion v1";
			socketServer = new ServerSocket(this.portServeur);
			serverRunning = true;
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
				System.out.println("CoSR\tWaitNewConnexion");
				Socket socketClient = socketServer.accept();//Quelque chose essai de se connecter
				//System.out.println("CoSR\ttest: ");
				BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
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
								}else if(clientType.equals("ArduinoRobotino")){//connexion d'un Robotino
									new Thread(new ConnexionArduinoRobotino(this,socketClient,firstLine,in)).start();
								}else if(clientType.equals("Webcam")){//connexion d'une webcam donnant du contenu
									new Thread(new ConnexionWebcam(this,socketClient,firstLine,in)).start();
								/*}else if(clientType.equals("GetFluxWebcam")){//connexion d'une webcam donnant du contenu
									new Thread(new ConnexionSendFluxWebcam(this,socketClient,firstLine,in)).start();*/
								}else if(clientType.equals("SensorsDatabase")){//connexion d'une webcam donnant du contenu
									new Thread(new ConnexionSensorsDatabase(this,socketClient,firstLine,in)).start();
								}else if(clientType.equals("sensors")){//connexion d'une webcam donnant du contenu
									System.out.println("CoSR\tsensors: ");
									System.out.println("CoSR\tJSON: "+firstLine);
									sendToAllWeb(firstLine);
									sendToAllSensorsDatabase(firstLine);
									//socketClient.close();
								}else{//type de client non reconu
									PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
									out.println("L( Â¨Â° 3Â¨Â° )J----#:`* no socket for u");
									socketClient.close();
								}
							}else if(type.equals("sensors")){//connexion d'une webcam donnant du contenu
								System.out.println("CoSR\tsensors: ");
								System.out.println("CoSR\tJSON: "+firstLine);
								sendToAllWeb(firstLine);
								sendToAllSensorsDatabase(firstLine);
								//socketClient.close();
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
						/*firstLine = in.readLine();
						System.out.println("CoSR\tfirstLine: "+firstLine);
						firstLine = in.readLine();
						System.out.println("CoSR\tfirstLine: "+firstLine);;
						if(firstLine.startsWith("Connection: Upgrade")){*/
							new Thread(new ConnexionWeb(this,socketClient,firstLine,in)).start();
						/*}/*else if(firstLine.startsWith("Connection: keep-alive")){
							new Thread(new ConnexionSendFluxWebcam(this,socketClient,firstLine,in)).start();
						}*/
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
		System.out.println("CRsize"+connexionsRobotino.size());
	}
	public synchronized void removeConnexionRobotino(ConnexionRobotino connexion) {
		this.connexionsRobotino.remove(connexion);
	}
	public synchronized void addConnexionArduinoRobotino(ConnexionArduinoRobotino connexion) {
		this.connexionsArduinoRobotino.add(connexion);
		System.out.println("CRsize"+connexionsArduinoRobotino.size());
	}
	public synchronized void removeConnexionArduinoRobotino(ConnexionArduinoRobotino connexion) {
		this.connexionsArduinoRobotino.remove(connexion);
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
	public synchronized void addWaitNewConnexionSendFluxWebcam(WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam) {
		this.waitNewConnexionSendFluxWebcams.add(waitNewConnexionSendFluxWebcam);
	}
	public synchronized void removeWaitNewConnexionSendFluxWebcam(WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam) {
		this.waitNewConnexionSendFluxWebcams.remove(waitNewConnexionSendFluxWebcam);
	}
	/**
	 * Methode permettant de récuperer la classe gérant une webcam particulière.
	 * @param nameWebcam
	 * @return le WaitNewConnexionSendFluxWebcam qui géère la webcam qui nous interesse
	 */
	public synchronized WaitNewConnexionSendFluxWebcam getWaitNewConnexionSendFluxWebcam(String nameWebcam) {
		WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam = null;
		for (int i = 0; i < waitNewConnexionSendFluxWebcams.size(); i++) {
			if(nameWebcam.equals(waitNewConnexionSendFluxWebcams.get(i).webcamName)){
				waitNewConnexionSendFluxWebcam = waitNewConnexionSendFluxWebcams.get(i);
			}
		}
		return waitNewConnexionSendFluxWebcam;
	}
	public synchronized void addConnexionSensorsDatabase(ConnexionSensorsDatabase connexion) {
		this.connexionsSensorsDatabase.add(connexion);
	}
	public synchronized void removeConnexionSensorsDatabase(ConnexionSensorsDatabase connexion) {
		this.connexionsSensorsDatabase.remove(connexion);
	}
	/*public synchronized void removeWaitNewConnexionSendFluxWebcam(WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam, String nameWebcam) {
		for (int i = 0; i < connexionsFluxWebcam.size(); i++) {
			if(nameWebcam.equals(connexionsFluxWebcam.get(i).name)){
				connexionsFluxWebcam.get(i).removeConnexionSendFluxWebcam(connexionsSendFluxWebcam);
			}
		}
	}*/
	/*public synchronized void addConnexionSendFluxWebcam(ConnexionSendFluxWebcam connexionsSendFluxWebcam) {
		this.connexionsSendFluxWebcam.add(connexionsSendFluxWebcam);
	}
	public synchronized void removeConnexionSendFluxWebcam(ConnexionSendFluxWebcam connexionsSendFluxWebcam) {
		this.connexionsSendFluxWebcam.remove(connexionsSendFluxWebcam);
	}*/
	
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
			System.out.println("ip du robot testé:"+connexionsRobotino.get(i).ipRobot+"   ip du robot recherché"+ip);
			if(connexionsRobotino.get(i).ipRobot.equals(ip)){
				connexionsRobotino.get(i).envoyerMessage(m);
				System.out.println("Envoyé à "+ip);
				found=true;
			}
		}
		for (int i = 0; i < connexionsArduinoRobotino.size(); i++) {
			System.out.println("ip du robot testé:"+connexionsArduinoRobotino.get(i).ipRobot+"   ip du ArduinoRobot recherché"+ip);
			if(connexionsArduinoRobotino.get(i).ipRobot.equals(ip)){
				connexionsArduinoRobotino.get(i).envoyerMessage(m);
				System.out.println("Envoyé à "+ip);
				found=true;
			}
		}
		if(!found){
			System.out.println("Aucun robot trouvé");
			//this.sendToAllRobotino(m);
		}
	}
	
	/**
	 * Envoie un message à toutes les connexions web
	 * @param m message JSON
	 */
	public synchronized void sendToAllWeb(String m) {
		for (int i = 0; i < connexionsWeb.size(); i++) {
			connexionsWeb.get(i).envoyerMessage(m);
		}
	}
	public synchronized void sendToAllSensorsDatabase(String m) {
		for (int i = 0; i < connexionsSensorsDatabase.size(); i++) {
			connexionsSensorsDatabase.get(i).envoyerMessage(m);
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
		//return "193.48.125.71:1337";
		return "193.48.125.70:50008";
	}

	public int getPortServeur() {
		return portServeur;
	}
	
	public void setPortServeur(int portServeur) {
		this.portServeur = portServeur;
	}
}
