package common.profiling;

import java.util.HashMap;
import java.util.Map;

public class Profile {
	static private Map<String, Profile> allProfiles;
	
	private String name;
	private long totalTime;
	private long startTime;
	
	static {
		allProfiles = new HashMap<String, Profile>();
	}
	
	protected Profile(String name){
		this.name = name;
		totalTime = 0;
	}
	
	public void start(){
		startTime = System.currentTimeMillis();
	}
	
	public void end(){
		totalTime += System.currentTimeMillis() - startTime;
	}
	
	public void increment(){
		totalTime += 1;
	}
	
	public long getTotalTime() {
		return totalTime;
	}
	
	static public Profile getProfile(String name){
		if (! allProfiles.containsKey(name) )
			allProfiles.put(name, new Profile(name));
		return allProfiles.get(name);
	}
	
	@Override
	public String toString() {
		return "Profile " + name + " --> " + totalTime + " milliseconds";
	}
	
	static public void printAll(){
		System.out.println("Profiling result");
		for (Profile profile : allProfiles.values()) {
			System.out.println(profile);
		}
		System.out.println("----------------");
	}
	
	static public void resetAll(){
		allProfiles.clear();
	}
}
