import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

/**
 * G�re la connexion d'un robotino au server
 * Classe qui �coute pour des connexions entrantes de types robotino, et qui les g�re.
 * @author lalandef
 *
 */
public class ConnexionArduinoRobotino implements Runnable {
	private ServerRobotino serverRobotino;
	@SuppressWarnings("unused")
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public String ipRobot;
	public ConnexionArduinoRobotino(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			out.println("{\"type\":\"init\",\"infoInit\":\"Connection accept�\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverRobotino=serverRobotino;
		this.socketClient=socketClient;
		ipRobot=socketClient.getInetAddress().toString();
		System.out.println("ipRobot:"+ipRobot);
		System.out.println("CoArduiRobo\tgetIntputStreamServer: "+firstLine);
		JSONObject JSON = new JSONObject(firstLine);
		try{
			String info = JSON.getString("infoInit");
			this.ipRobot = JSON.getString("ipRobot");
			System.out.println("CoArduiRobo\tinfo: "+info);
		}catch(org.json.JSONException e){
			ipRobot=socketClient.getInetAddress().toString();
			System.out.println("CoArduiRobo\tPas d'ip envoy�: "+e);
			System.out.println("CoArduiRobo\tJSON: "+firstLine);
		}
		System.out.println("ipRobot:"+ipRobot);
	}

	/**
	 * Initie la connexion et attend les requ�te du robotino
	 */
	public void run() {
		serverRobotino.addConnexionArduinoRobotino(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoArduiRobo\tgetIntputStreamServer2: "+inLine);
				this.decodeurJson(inLine);
			}
		} catch (Exception e) {/*e.printStackTrace();*/}//connexion ferm�
		System.out.println("CoArduiRobo\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionArduinoRobotino(this);
	}

	/**
	 * Decode un message JSON pour pouvoir l'utiliser et le traite
	 * pour les autres utilisateurs/le serveur.
	 * @param j Message JSON aux format texte
	 */
	public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("CoArduiRobo\ttype:"+type);
			
			if(type.equals("init")){//inutilis� ici, uniquement au d�but de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("CoArduiRobo\tinfo: "+info);
				
			}else if(type.equals("command")){//message
				System.out.println("CoArduiRobo\tJSON: "+j);
				serverRobotino.sendToOneRobotino(j,ipRobot);
			}
		}catch(org.json.JSONException e){
			System.out.println("CoArduiRobo\terreur decodage JSON: "+e);
			System.out.println("CoArduiRobo\tJSON: "+j);
		}
	}
	
	/**
	 * Envoie un message JSON au Robotino g�r� par cette connexion
	 * @param m message JSON � envoyer
	 */
	public void envoyerMessage(String m){
		out.println(m);
	}

}
