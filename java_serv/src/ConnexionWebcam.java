import java.io.BufferedReader;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

/**
 * Classe qui doit faire une connection a la webcam, et qui doit redistribuer a tout les clients.
 * @author prospere
 *
 */
public class ConnexionWebcam implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public String ipWebcam;
	public String portWebcam;
	public String name;
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
		try{
			this.name = JSON.getString("clientName");
		}catch(org.json.JSONException e){
			name="";
			System.out.println("CoWebcam\tPas de \"clientName\" envoyé: "+e);
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
		//String portString=serverRobotino.mapNameWebcam_Port.get(name).toString();
		int port;
		WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam;
		if(serverRobotino.mapNameWebcam_Port.get(name)==null){
			port = serverRobotino.portDispo;
			serverRobotino.portDispo++;
			serverRobotino.mapNameWebcam_Port.put(name,port);
			new Thread(new WaitNewConnexionSendFluxWebcam(port,serverRobotino,name, ipWebcam, portWebcam)).start();
			//new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name, waitNewConnexionSendFluxWebcam);
		}else{
			port = serverRobotino.mapNameWebcam_Port.get(name);
			waitNewConnexionSendFluxWebcam = serverRobotino.getWaitNewConnexionSendFluxWebcam(name);
			//System.out.println("waitNewConnexionSendFluxWebcam"+waitNewConnexionSendFluxWebcam);
			//new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name, waitNewConnexionSendFluxWebcam);
			new Thread(new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name, waitNewConnexionSendFluxWebcam)).start();
			//new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name, waitNewConnexionSendFluxWebcam);
		}
		/*//String portString=serverRobotino.mapNameWebcam_Port.get(name).toString();
		int port;
		WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam;
		if(serverRobotino.mapNameWebcam_Port.get(name)==null){
			port = serverRobotino.portDispo;
			serverRobotino.portDispo++;
			serverRobotino.mapNameWebcam_Port.put(name,port);
			Thread threadWaitNewConnexionSendFluxWebcam = new Thread(waitNewConnexionSendFluxWebcam = new WaitNewConnexionSendFluxWebcam(port,serverRobotino,name));
			threadWaitNewConnexionSendFluxWebcam.start();
			new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name, waitNewConnexionSendFluxWebcam);
		}else{
			port = serverRobotino.mapNameWebcam_Port.get(name);
			waitNewConnexionSendFluxWebcam = serverRobotino.getWaitNewConnexionSendFluxWebcam(name);
			new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name, waitNewConnexionSendFluxWebcam);
		}*/
		/*int portDispo = serverRobotino.portDispo;
		serverRobotino.portDispo++;*/
		//ConnexionFluxWebcam connexionFluxWebcamne;
		//WaitNewConnexionSendFluxWebcam waitNewConnexionSendFluxWebcam;
		//Thread threadConnexionFluxWebcam = new Thread(connexionFluxWebcamne = new ConnexionFluxWebcam(serverRobotino, ipWebcam, portWebcam, name));
		//Thread threadWaitNewConnexionSendFluxWebcam = new Thread(waitNewConnexionSendFluxWebcam = new WaitNewConnexionSendFluxWebcam(port,serverRobotino,name));
		//threadWaitNewConnexionSendFluxWebcam.start();
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
