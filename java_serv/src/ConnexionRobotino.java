import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;


public class ConnexionRobotino implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public ConnexionRobotino(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			out.println("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverRobotino=serverRobotino;
		this.socketClient=socketClient;
		System.out.println("CoRobo\tgetIntputStreamServer: "+firstLine);
		JSONObject JSON = new JSONObject(firstLine);
		String info = JSON.getString("infoInit");
		System.out.println("CoRobo\tinfo: "+info);
	}

	@Override
	public void run() {
		serverRobotino.addConnexionRobotino(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("CoRobo\tgetIntputStreamServer2: "+inLine);
				this.decodeurJson(inLine);
			}
		} catch (IOException e) {/*e.printStackTrace();*/}//connexion fermé
		System.out.println("CoRobo\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionRobotino(this);
	}
	public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("CoRobo\ttype:"+type);
			
			if(type.equals("init")){//inutilisé ici, uniquement au début de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("CoRobo\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("CoRobo\tMessage: "+message);
			}
		}catch(org.json.JSONException e){
			System.out.println("CoRobo\terreur decodage JSON: "+e);
			System.out.println("CoRobo\tJSON: "+j);
		}
	}
	public void envoyerMessage(String m){
		out.println(m);
	}

}
