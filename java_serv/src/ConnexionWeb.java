import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;


public class ConnexionWeb implements Runnable {
	private ServerRobotino serverRobotino;
	private Socket socketClient;
	private PrintWriter out;
	private BufferedReader in;
	public ConnexionWeb(ServerRobotino serverRobotino, Socket socketClient, String firstLine, BufferedReader in) {
		try {
			this.out = new PrintWriter(socketClient.getOutputStream(), true);
			this.in = in;
			this.serverRobotino=serverRobotino;
			this.socketClient=socketClient;
			System.out.println("firstLine: "+firstLine);
			//out.println("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			String inLine =" ";
			while(this.serverRobotino.isServerRunning()&&(!inLine.startsWith("Sec-WebSocket-Key: "))){//On attend la ligne qui porte la clée
				inLine = in.readLine();//récupération des informations au déput de la connexion connexion
				System.out.println("message inconu: "+inLine);
			}
			//String delims = "[: ]";
			String[] parse = inLine.split(": ");
			String key = (parse[1].toString());
			System.out.println("Sec-WebSocket-Key: "+key);
//			System.out.println("message: "+inLine);
//			try {
//				System.out.println("newSWSK: "+DatatypeConverter.printBase64Binary(MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8"))).toString());
//			} catch (NoSuchAlgorithmException e1) {
//				e1.printStackTrace();
//			}
			byte[] response;
			try {
				response = ("HTTP/1.1 101 Switching Protocols\r\n"
				        + "Connection: Upgrade\r\n"
				        + "Upgrade: websocket\r\n"
				        + "Sec-WebSocket-Accept: "
				        + DatatypeConverter
				        .printBase64Binary(
				                MessageDigest
				                .getInstance("SHA-1")
				                .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
				                        .getBytes("UTF-8")))
				        + "\r\n\r\n").getBytes("UTF-8");
			    //System.out.println("reponse: "+response.toString());
			    socketClient.getOutputStream().write(response, 0, response.length);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		            //.getBytes("UTF-8");
			inLine = in.readLine();//récupération des informations au début de la connexion connexion
			System.out.println("message inconu: "+inLine);
			inLine = in.readLine();//récupération des informations au début de la connexion connexion
			System.out.println("message inconu: "+inLine);
		    //out.println(response.toString());
	        String message = decodeWebSocketMessage();
			while(this.serverRobotino.isServerRunning()&&(!message.startsWith("{"))){
				//inLine = in.readLine();//récupération des informations au déput de la connexion connexion
				System.out.println("message non JSON: "+message);
	
		        message = decodeWebSocketMessage();
				
			}//on a le message d'init
			String initJSON = message;
			System.out.println("initJSON: "+initJSON);
			
			System.out.println("CoW\tgetIntputStreamServer: "+message);
			JSONObject JSON = new JSONObject(message);
			String info = JSON.getString("infoInit");
			System.out.println("CoW\tinfo: "+info);
			this.encodeWebSocketMessage("{\"type\":\"init\",\"infoInit\":\"Connection accepté\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverRobotino.addConnexionWeb(this);
		try {
			String inLine = "";
			while(this.serverRobotino.isServerRunning()&&inLine!=null){//lecture des nouveau message
				inLine = decodeWebSocketMessage();
				System.out.println("CoW\tgetIntputStreamServer: "+inLine);
				this.decodeurJson(inLine);
			}
		} catch (IOException e) {/*e.printStackTrace();*/}//connexion fermé
		System.out.println("CoW\ttest fin de conection par rupture de connexion: ");
		serverRobotino.removeConnexionWeb(this);
	}

	public void encodeWebSocketMessage(String message){
		try {
			//byte en byte [-128..127]  byte en int [0..255]
			String requete = "";
			//byte[] bytesMessage = "{\"test\":\"test\"}".getBytes("UTF-8");
			byte[] bytesMessage = message.getBytes("UTF-8");
			ArrayList<Byte> bytes = new ArrayList<Byte>();
			bytes.add((byte) 129);
			//bytes.add((byte) 128);
			//ArrayList<Byte> bytes = new ArrayList<Byte>();
			double messageLength=bytesMessage.length;
			int masked=0;
			byte[] bytesRequete;//requete+message
			int bytesRequeteIndex=0;
			if(messageLength<126){//longueur du message compris sur 7 bits
				bytes.add((byte) (128*masked+messageLength));
				bytesRequete= new byte[bytesMessage.length+2+0+masked*4];
			}else if(messageLength<(2<<16 - 1)){//longueur du message compris sur 16 bits
				bytes.add((byte) (128*masked+126));
				double messageLengthTemp=messageLength;
				int quotient=0;
				for(int i = 2;i>0;i--){
					quotient=(int) (messageLengthTemp/(Math.pow(256, i-1)));
					System.out.println(i+": "+quotient+" = "+messageLengthTemp+"/"+(Math.pow(256, i-1)));
					messageLengthTemp-=quotient*Math.pow(256, i-1);
					bytes.add((byte)quotient);
				}
				bytesRequete= new byte[bytesMessage.length+2+2+masked*4];
			}else{//longueur du message compris sur 64 bits
				bytes.add((byte) (128*masked+127));
				double messageLengthTemp=messageLength;
				int quotient=0;
				for(int i = 8;i>0;i--){
					quotient=(int) (messageLengthTemp/(Math.pow(256, i-1)));
					System.out.println(i+": "+quotient+" = "+messageLengthTemp+"/"+(Math.pow(256, i-1)));
					messageLengthTemp-=quotient*Math.pow(256, i-1);
					bytes.add((byte)quotient);
				}
				bytesRequete= new byte[bytesMessage.length+2+8+masked*4];
			}
			 if (masked==1) {//inutilisé
				 byte maskingKey[] = new byte[4];
				 maskingKey[0] = (byte) 120;//random
				 maskingKey[1] = (byte) 120;//random
				 maskingKey[2] = (byte) 120;//random
				 maskingKey[3] = (byte) 120;//random
				 bytes.add(maskingKey[0]);
				 bytes.add(maskingKey[1]);
				 bytes.add(maskingKey[2]);
				 bytes.add(maskingKey[3]);
	            for (int i = 0; i < bytesMessage.length; i++){
	            	bytesMessage[i] = (byte) ((maskingKey[i % 4] & 0xFF)^bytesMessage[i]);
	            }
			}
			for (int i = 0; i < bytes.size(); i++) {
				bytesRequete[bytesRequeteIndex]=bytes.get(i);
				bytesRequeteIndex++;
			}
			for (int i = 0; i < bytesMessage.length; i++) {
				bytesRequete[bytesRequeteIndex]=bytesMessage[i];
				bytesRequeteIndex++;
			}
			//bytesRequete
			/*bytes.add((byte)bytesMessage[0]);
			bytes.add((byte)bytesMessage[1]);
			bytes.add((byte)bytesMessage[2]);
			bytes.add((byte)bytesMessage[3]);
			System.out.println("test bytes: "+bytes);
			//out.write();
			byte[] b = new byte[0];
			byte[] b2 = bytes.toArray(new byte[0]);*/
			/*System.out.println("test bytes Messages: "+bytesMessage[0]);
			System.out.println("test bytes Messages: "+bytesMessage[1]);
			System.out.println("test bytes Messages: "+bytesMessage[2]);
			System.out.println("test bytes Messages: "+bytesMessage[3]);*/
			System.out.println("test bytes: "+bytes);
			try {
				socketClient.getOutputStream().write(bytesRequete);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	
			
			//this.out.println(requete); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public String decodeWebSocketMessage() throws IOException{//code trouvé: https://stackoverflow.com/questions/18368130/how-to-parse-and-validate-a-websocket-frame-in-java
		String message="";
		InputStream buf = socketClient.getInputStream();
        message = "";

        // Fin + RSV + OpCode byte
        byte b = (byte) buf.read();
		System.out.println("byte inconu: "+b);
        boolean fin = ((b & 0x80) != 0);
        boolean rsv1 = ((b & 0x40) != 0);
        boolean rsv2 = ((b & 0x20) != 0);
        boolean rsv3 = ((b & 0x10) != 0);
        //frame.opcode = (byte)(b & 0x0F);

        // TODO: add control frame fin validation here
        // TODO: add frame RSV validation here

        // Masked + Payload Length
        b = (byte) buf.read();
        System.out.println("byte buffer: "+b);
        boolean masked = ((b & 0x80) != 0);
        int payloadLength = (0x7F & b);
        int byteCount = 0;
        if (payloadLength == 0x7F) {
            // 8 byte extended payload length
            byteCount = 8;
            payloadLength = 0;
        }
        else if (payloadLength == 0x7E) {
            // 2 bytes extended payload length
            byteCount = 2;
            payloadLength = 0;
        }

        // Decode Payload Length
        while (byteCount-- > 0){
        	b = (byte) buf.read();
            System.out.println("payloadLength1: "+payloadLength+", "+((b & 0xFF) << (8 * byteCount))+", "+(b & 0xFF)+", "+(8 * byteCount));
        	 payloadLength += (b & 0xFF) << (8 * byteCount);
        }

        // TODO: add control frame payload length validation here

        byte maskingKey[] = null;
        if (masked) {
            // Masking Key
            maskingKey = new byte[4];
            buf.read(maskingKey,0,4);
            /*maskingKey[0] = (byte) buf.read();
            maskingKey[1] = (byte) buf.read();
            maskingKey[2] = (byte) buf.read();
            maskingKey[3] = (byte) buf.read();*/
        }

        // TODO: add masked + maskingkey validation here

        // Payload itself
		System.out.println("payloadLength: "+payloadLength);
        byte[] payload = new byte[payloadLength];//texte codé si maked==true
        buf.read(payload,0,payloadLength);
        //buf.get(frame.payload,0,payloadLength);

        // Demask (if needed)
        if (masked){
            for (int i = 0; i < payload.length; i++){
                payload[i] ^= (maskingKey[i % 4] & 0xFF);
            }
        }
		message=new String(payload, "UTF-8");
		return message;
	}
	public void decodeurJson(String j) {
		try{
			JSONObject JSON = new JSONObject(j);
			String type = JSON.getString("type");
			System.out.println("CoW\ttype:"+type);
			
			if(type.equals("init")){//inutilisé ici, uniquement au début de la classe connexion
				String info = JSON.getString("infoStart");
				System.out.println("CoW\tinfo: "+info);
				
			}else if(type.equals("message")){//message
				String message = JSON.getString("message");
				System.out.println("CoW\tMessage: "+message);
			}else if(type.equals("commande")){//message
				serverRobotino.sendToAllRobotino(j);
			}
		}catch(org.json.JSONException e){
			System.out.println("CoW\terreur decodage JSON: "+e);
			System.out.println("CoW\tJSON: "+j);
		}
	}
	public void envoyerMessage(String m){
		encodeWebSocketMessage(m);
	}

}
