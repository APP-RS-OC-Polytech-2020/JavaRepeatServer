package Webcam;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class MainWebcam {

	public static void main(String[] args) {
		Webcam webcam = Webcam.getDefault();
		webcam.open();
		
		int port=50008;
		String ipServer="193.48.125.70";
		
		try {
			Socket clientSocket = new Socket(ipServer,port);
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out.println("{\"type\":\"init\",\"infoInit\":\"Client-->Server  demande de connexion\", \"clientName\": \""+""+"\", \"clientType\":\"Robotino\"}");
			out.println("{\"type\":\"video\",\"link\":\"http://tp-epu.univ-savoie.fr/~prospere/img.png\"}");
			while(true){
				//for(int i =0; i < 50; i++){
					try {
						ImageIO.write(webcam.getImage(), "PNG", new File("E:\\public_html\\img.png"));
					} catch (IOException e) {
						System.err.println("oups");
						e.printStackTrace();
					}
					try {
						Thread.sleep(90);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//}
				//out.println("{\"type\":\"video\",\"link\":\"http://tp-epu.univ-savoie.fr/~prospere/img.png\"}");
			}
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

	}

}
