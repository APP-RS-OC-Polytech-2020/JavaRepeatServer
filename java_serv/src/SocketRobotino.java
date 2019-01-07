import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;


public class SocketRobotino implements Runnable {

	PrintWriter out;
	BufferedReader in;
	Socket clientSocket;
	int port=50007;
	//String ipServer="192.168.56.1";//iplocal
	String ipServer="193.48.125.70";
	public SocketRobotino(String ipServer,int  port){
		this.port=port;
		this.ipServer=ipServer;
		try {
			clientSocket = new Socket(ipServer, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("{\"type\":\"init\",\"infoInit\":\"Client-->Server  demande de connexion\", \"clientName\": \""+""+"\", \"clientType\":\"Robotino\"}");//ajouter le nom du robot
		out.println("{\"type\":\"message\",\"message\":\"testRobotino\"}");
		//try {TimeUnit.MILLISECONDS.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		
	}
	public void run() {
		String inLine="";
		while(inLine!=null){//&&inLine!=null){//lecture des nouveau message
			//try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			try {
				inLine = in.readLine();
				decodeurJson(inLine);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("client\tgetIntputStreamServer: "+inLine);
		}
	}
	public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("CoRobo\ttype:"+type);
			
			if(type.equals("init")){//inutilisé ici, uniquement au début de la classe connexion
				String info = JSON.getString("infoInit");
				System.out.println("CoRobo\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("CoRobo\tMessage: "+message);
				
				//exmple pour le décodage de JSON
				//String dName = JSON.getJSONObject("infoCommande").getJSONObject("destinataire").getString("name");

			}
		}catch(org.json.JSONException e){
			System.out.println("CoRobo\terreur decodage JSON: "+e);
			System.out.println("CoRobo\tJSON: "+j);
		}
	}
}
