/**
 * 
 */
package ose.runner;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * @author Pham Kim Cuong
 *
 */
public class OSConfiguration {
	private XMLConfiguration xmlConfig ;
	
	static private OSConfiguration configuration;
	
	static public OSConfiguration getConfiguration() throws ConfigurationException{
		if (configuration == null){
			configuration = new OSConfiguration();
		}
		return configuration;
	}
	
	private OSConfiguration() throws ConfigurationException{
		xmlConfig =  new XMLConfiguration("osconfig.xml");
	}
	
	public String getIndexPath(){
		return xmlConfig.getString("index_dir") + "/" + xmlConfig.getString("main_index");
	}
	
	public String getIndexDir(){
		return xmlConfig.getString("index_dir") ;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			OSConfiguration conf = OSConfiguration.getConfiguration();
			System.out.println("Path : " + conf.getIndexPath());
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
