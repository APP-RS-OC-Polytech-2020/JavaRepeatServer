import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;


public class ConnexionJava implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public ConnexionJava(ServerRobotino serverRobotino, Socket socketClient, String firstLine) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			out.println("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverRobotino=serverRobotino;
		this.socketClient=socketClient;
		System.out.println("C\tgetIntputStreamServer: "+firstLine);
	}

	@Override
	public void run() {
		serverRobotino.addConnexionJava(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = in.readLine();
				System.out.println("C\tgetIntputStreamServer2: "+inLine);
				this.decodeurJson(inLine);
			}
		} catch (IOException e) {/*e.printStackTrace();*/}//connexion fermé
		System.out.println("C\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionJava(this);
	}
	public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("S\ttype:"+type);
			
			if(type.equals("init")){//inutilisé ici, uniquement au début de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("S\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("Message: "+message);
			}
		}catch(org.json.JSONException e){
			System.out.println("erreur decodage JSON: "+e);
			System.out.println("JSON: "+j);
		}
	}

}
