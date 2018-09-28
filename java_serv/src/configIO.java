import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads and Writes to config.properties, placed at project root.
 * Has a number of handy methods to get quickly stuff.
 * Grab with: configIO.getJavaPort
 * (Or whatever propertie you need)
 * @author prospere
 *
 */
public class configIO {
	private static File configFile = new File("java_serv/config.properties");
	
	public configIO(){}
		 
		private static Properties getProps(){
			try {
			    FileReader reader = new FileReader(configFile);
			    Properties props = new Properties();
			    props.load(reader);
			    reader.close();
			    return props;
			} catch (FileNotFoundException ex) {
			    // file does not exist
				ex.printStackTrace();
			} catch (IOException ex) {
			    // I/O error
				ex.printStackTrace();
			}
			return null;
		}
		/**
		 * Get the java port from the config.properties
		 * @return int	the port for java comms
		 */
		public static int getJavaPort(){
		    String port = getProps().getProperty("javaPort");
			//int port = 50007;
			return Integer.parseInt(port);
		}
		
		/**
		 * Gets the WebSocket port from config.properties
		 * @return	int	port for Websocket
		 */
		public static int getWebsocketPort(){
			String port = getProps().getProperty("websocketPort");
			return Integer.parseInt(port);
		}
	

}
