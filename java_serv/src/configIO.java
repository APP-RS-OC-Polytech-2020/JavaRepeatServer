import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads and Writes to config.properties, placed at project root.
 * Has a number of handy methods to get quickly stuff.
 * Grab with: configIO.getInstance.getJavaPort
 * Uses Singleton Pattern.
 * (Or whatever properties you need)
 * @author prospere
 *
 */
public class configIO {
	private static File configFile = new File("java_serv/config.properties");
	private static configIO Instance = null;
	
	private configIO(){}
		 
		private Properties getProps(){
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
		    String port = configIO.getInstance().getProps().getProperty("javaPort");
			return Integer.parseInt(port);
		}
		
		/**
		 * Gets the WebSocket port from config.properties
		 * @return	int	port for Websocket
		 */
		public static int getWebsocketPort(){
			String port = configIO.getInstance().getProps().getProperty("websocketPort");
			return Integer.parseInt(port);
		}
		
		public static configIO getInstance(){
			if(Instance!=null){
				return Instance;
			}
			else{
				Instance = new configIO();
				return Instance;
			}
		}
	

}
