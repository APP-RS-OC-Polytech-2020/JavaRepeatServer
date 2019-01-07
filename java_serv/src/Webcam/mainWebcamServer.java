package Webcam;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class mainWebcamServer {

	public static void main(String[] args) {
		try{
        ServerSocket serverSocket = new ServerSocket(13085);
        Socket socket = serverSocket.accept();
        
        InputStream inputStream = socket.getInputStream();

        System.out.println("Reading: " + System.currentTimeMillis());


        BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(inputStream));

        ImageIO.write(image, "jpg", new File("E:\\temp\\Received.jpg"));

        //serverSocket.close();
        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{}

	}

}
