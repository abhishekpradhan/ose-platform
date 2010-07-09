/**
 * 
 */
package ose.retrieval;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import ose.database.Feedback;
import ose.database.FeedbackManager;
import ose.utils.JsonIO;

/**
 * @author Pham Kim Cuong
 *
 */
public interface RankingEvaluation {
	public double evaluate(Integer [] rankedDocIds);
	public JSONObject getJSONObject() throws JSONException;
	public void prettyPrint();	
}
