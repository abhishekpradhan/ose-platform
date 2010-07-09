package lbjse.utils;

import LBJ2.classify.DiscreteFeature;

public class LbjUtils {

	public static DiscreteFeature parseDiscreteFeature(String s){
		String p, i, v;
		if (s.indexOf(":") != -1){
			p = s.substring(0,s.indexOf(":"));
			s = s.substring(s.indexOf(":")+1);
		}
		else {
			p = "";
		}
		if (s.indexOf("(") != -1){
			i = s.substring(0,s.indexOf("("));
			v = s.substring(s.indexOf("(")+1, s.indexOf(")"));
		}
		else {
			i = s;
			v = "";
		}
		return new DiscreteFeature(p.intern(),i.intern(),v.intern());
	}
	
	public static DiscreteFeature parseDiscreteFeatureAndNegate(String s){
		String p, i, v;
		if (s.indexOf(":") != -1){
			p = s.substring(0,s.indexOf(":"));
			s = s.substring(s.indexOf(":")+1);
		}
		else {
			p = "";
		}
		if (s.indexOf("(") != -1){
			i = s.substring(0,s.indexOf("("));
			v = "-" + s.substring(s.indexOf("(")+1, s.indexOf(")"));
		}
		else {
			i = s;
			v = "";
		}
		return new DiscreteFeature(p.intern(),i.intern(),v.intern());
	}

}
