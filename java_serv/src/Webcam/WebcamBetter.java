package Webcam;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class WebcamBetter {
	
	public static void main(String[] args) {
		Webcam webcam = Webcam.getDefault();
		webcam.open();
		
//		int port=50008;
//		String ipServer="193.48.125.70";
//		
//		PrintWriter out =null;
//		BufferedReader in = null;
//		Socket clientSocket;
//		
//		try{
//			clientSocket = new Socket(ipServer,port);
//			out = new PrintWriter(clientSocket.getOutputStream(), true);
//			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//		}
		
		try{
			
			Socket socket = new Socket("localhost", 13085);
	        OutputStream outputStream = socket.getOutputStream();
	
	        //BufferedImage image = ImageIO.read(new File("E:\\temp\\tosend.jpg"));
	        
	        BufferedImage image = webcam.getImage();
	   
	        ImageIO.write(image, "jpg", socket.getOutputStream());
	        
	        System.out.println("Closing: " + System.currentTimeMillis());
	        //socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{}
			
	}

}
