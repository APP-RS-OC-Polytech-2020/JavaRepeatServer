import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
/**
 * Classe g�rant la connexion en java. (Surtout utilis� pour des tests)
 * @author lalandef
 *
 */

public class ConnexionJava implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public ConnexionJava(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			out.println("{\"type\":\"init\",\"infoInit\":\"Connection accept�\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverRobotino=serverRobotino;
		this.socketClient=socketClient;
		System.out.println("CoJ\tgetIntputStreamServer: "+firstLine);
		JSONObject JSON = new JSONObject(firstLine);
		try{
			String info = JSON.getString("infoInit");
			System.out.println("CoRobot\tinfo: "+info);
		}catch(org.json.JSONException e){
		}
	}
	
	/**
	 * Initie la connexion et attend les requ�te de l'application Java
	 */
	public void run() {
		serverRobotino.addConnexionJava(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoJ\tgetIntputStreamServer2: "+inLine);
				this.decodeurJson(inLine);
			}
		} catch (IOException e) {/*e.printStackTrace();*/}//connexion ferm�
		System.out.println("CoJ\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionJava(this);
	}
	
	/**
	 * Fonction permettant le decodage du JSON et le traitement selon les params du projet
	 * appelle la lib JSON pour faire sa magie
	 * TODO faire autre chose que renvoyer les messages
	 * @param j
	 */
	public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("CoJ\ttype:"+type);
			
			if(type.equals("init")){//inutilis� ici, uniquement au d�but de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("CoJ\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("CoJ\tMessage: "+message);
			}
		}catch(org.json.JSONException e){
			System.out.println("CoJ\terreur decodage JSON: "+e);
			System.out.println("CoJ\tJSON: "+j);
		}
	}
	
	/**
	 * Envoie un message JSON aux autre utilisateur(Un ou plusieur selon ce qui est pr�cis� dans le message)
	 * @param m message JSON � envoyer
	 */
	public void envoyerMessage(String m){
		out.println(m);
	}

}
