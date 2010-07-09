package lbjse.rank.conditional;

import lbj.professor.area_ranker;
import lbj.professor.dept_ranker;
import lbj.professor.name_ranker;
import lbj.professor.other_ranker;
import lbj.professor.univ_ranker;
import lbjse.rank.ObjectRanker;
import LBJ2.learn.Learner;

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

	
}
