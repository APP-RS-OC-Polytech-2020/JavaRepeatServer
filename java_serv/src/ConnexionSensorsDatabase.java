import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
/**
 * Classe gérant la connexion en java. (Surtout utilisé pour des tests)
 * 
 *
 */

public class ConnexionSensorsDatabase implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public ConnexionSensorsDatabase(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			out.println("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverRobotino=serverRobotino;
		this.socketClient=socketClient;
		System.out.println("CoSDb\tgetIntputStreamServer: "+firstLine);
		JSONObject JSON = new JSONObject(firstLine);
		//String info = JSON.getString("infoInit");
		//System.out.println("CoSDb\tinfo: "+info);
	}
	
	/**
	 * Initie la connexion et attend les requête de l'application Java
	 */
	public void run() {
		serverRobotino.addConnexionSensorsDatabase(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoSDb\tgetIntputStreamServer2: "+inLine);
				this.decodeurJson(inLine);
			}
		} catch (IOException e) {/*e.printStackTrace();*/}//connexion fermé
		System.out.println("CoSDb\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionSensorsDatabase(this);
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
			System.out.println("CoSDb\ttype:"+type);
			
			if(type.equals("init")){//inutilisé ici, uniquement au début de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("CoSDb\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("CoSDb\tMessage: "+message);
			}else if(type.equals("alert")){//message
				//String message = JSON.getString("message");
				//System.out.println("CoSDb\tMessage: "+message);
				serverRobotino.sendToAllWeb(j);
				serverRobotino.sendToAllRobotino(j);
			}
		}catch(org.json.JSONException e){
			System.out.println("CoSDb\terreur decodage JSON: "+e);
			System.out.println("CoSDb\tJSON: "+j);
		}
	}
	
	/**
	 * Envoie un message JSON aux autre utilisateur(Un ou plusieur selon ce qui est précisé dans le message)
	 * @param m message JSON à envoyer
	 */
	public void envoyerMessage(String m){
		out.println(m);
	}

}
