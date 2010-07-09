package lbjse.rank.inde;

import java.io.IOException;

import lbjse.learning.professor.inde.dept_ranker;
import lbjse.learning.professor.inde.name_ranker;
import lbjse.learning.professor.inde.univ_ranker;
import lbj.professor.area_ranker;
import lbj.professor.other_ranker;
import lbjse.rank.ObjectRanker;
import LBJ2.learn.Learner;

import common.profiling.Profile;

public class ProfessorRank extends ObjectRanker{
	
	private static final String [] fieldNames  = 
		{"name","dept","univ","area","other"};
	private static final Learner [] rankers = 
		{new name_ranker(), new dept_ranker(), new univ_ranker(), new area_ranker(), new other_ranker()};
	
	public ProfessorRank(String lbjTrec, String queryFile, String outputFile, AggregationMode mode) {
		super(fieldNames, rankers, outputFile, mode);
		initializeData(lbjTrec, queryFile);
	}
	
	public ProfessorRank(String lbjTrec, int queryId, String outputFile, AggregationMode mode) throws Exception{
		super(fieldNames, rankers, outputFile, mode);
		initializeData(lbjTrec, queryId);
	}
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws IOException {
		Profile.getProfile("main").start();
		ProfessorRank ranker = new ProfessorRank("prof_train.trec", "prof_query.txt", "professor.ranked_result", AggregationMode.MULTIPLY);
		ranker.rankQueryAndOutputAP()
		;
		
//		ranker.showScoreForDocId(50682);
		Profile.getProfile("main").end();
		Profile.printAll();
	}
}
