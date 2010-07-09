package annieWeb.utils;

public class ServeletUtils {
	public static String getCacheURL(int indexId, int docId){
		return "/annieWeb/CacheServlet?indexId=" + indexId + "&docId=" + docId;	
	}
}
