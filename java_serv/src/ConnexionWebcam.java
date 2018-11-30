import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class ConnexionWebcam implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public String ipWebcam;
	public String portWebcam;
	public ConnexionWebcam(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			//out.println("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("CoWebcam\tgetIntputStreamServer: "+firstLine);
		JSONObject JSON = new JSONObject(firstLine);
		try{
			this.ipWebcam = JSON.getString("ip");
		}catch(org.json.JSONException e){
			ipWebcam=socketClient.getInetAddress().toString();
			System.out.println("CoWebcam\tPas d'ip envoyé: "+e);
			System.out.println("CoWebcam\tJSON: "+firstLine);
		}
		try{
			this.portWebcam = JSON.getString("port");
		}catch(org.json.JSONException e){
			portWebcam="50009";
			System.out.println("CoWebcam\tPas de port envoyé: "+e);
			System.out.println("CoWebcam\tJSON: "+firstLine);
		}
		this.serverRobotino=serverRobotino;
		this.socketClient=socketClient;
		//System.out.println("CoWebcam\tgetIntputStreamServer: "+firstLine);
		String info = JSON.getString("infoInit");
		System.out.println("CoWebcam\tinfo: "+info);
	}
	/**
	 * Initie la connexion et attend les requête de la webcam
	 */
	public void run() {
		new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam);
		//serverRobotino.addConnexionWebcam(this);
		/*try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoWebcam\tgetIntputStreamServer2: "+inLine);
				this.decodeurJson(inLine);
			}*/
		//} catch (IOException e) {/*e.printStackTrace();*/}//connexion fermé
		//System.out.println("CoWebcam\ttest fin de conection par rupture de connexion: ");
		//serverRobotino.removeConnexionWebcam(this);
	}
	
	/**
	 * Fonction permettant le decodage du JSON et le traitement selon les params du projet
	 * appelle la lib JSON pour faire sa magie
	 * TODO faire autre chose que renvoyer les messages
	 * @param j
	 */
	/*public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("CoWebcam\ttype:"+type);
			
			if(type.equals("init")){//inutilisé ici, uniquement au début de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("CoWebcam\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("CoWebcam\tMessage: "+message);
			}
		}catch(org.json.JSONException e){
			System.out.println("CoWebcam\terreur decodage JSON: "+e);
			System.out.println("CoWebcam\tJSON: "+j);
		}
	}
	*/
	/**
	 * Envoie un message JSON aux autre utilisateur(Un ou plusieur selon ce qui est précisé dans le message)
	 * @param m message JSON à envoyer
	 */
	/*public void envoyerMessage(String m){
		out.println(m);
	}*/

}
