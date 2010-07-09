package annieWeb.utils;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

public class QueryStringOption {
	private String usage;
	private HttpServletRequest request ;
	private Writer response;
	
	public QueryStringOption(HttpServletRequest request, Writer response) {
		this.request = request;
		this.response = response;
	}
	
	public void require(String [] requirements) throws RuntimeException {
		for (int i = 0; i < requirements.length; i++) {
			if (request.getParameter(requirements[i]) == null){
				throw new RuntimeException("Argument " + requirements[i] + " is required\n" +
						"Usage : " + usage);
			}
		}
	}
	
	public String getString(String key){
		return request.getParameter(key);
	}
	
	/*
	 * get comma separated strings
	 */
	public String [] getStrings(String key){
		if (request.getParameterValues(key) == null)
			return new String[]{};
		else
			return request.getParameterValues(key);
	}
	
	public Integer getInt(String key){
		return Integer.parseInt(request.getParameter(key));
	}
	
	public Integer getInt(String key, int defaultValue){
		try {
			return Integer.parseInt(request.getParameter(key));
		} catch (Exception e){
			return defaultValue;	
		}
	}
	
	public Double getDouble(String key){
		return Double.parseDouble(request.getParameter(key));
	}
		
	public boolean hasArg(String key){
		return request.getParameter(key) != null;
	}
	
	
	public void setUsage(String u){
		usage = u;
	}
	
	public void printUsage() throws IOException {
		response.write("Usage : \n" +
				"" + usage);
	}
	
}
