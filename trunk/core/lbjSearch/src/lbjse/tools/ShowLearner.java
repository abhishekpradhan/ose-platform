package lbjse.tools;

import lbjse.trainer.TrainingSession;
import LBJ2.learn.Learner;

public class ShowLearner {

	public ShowLearner() {
		// TODO Auto-generated constructor stub
	}
	
	public void show(Learner ranker) {
		ranker.write(System.out);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		other_ranker ranker = new other_ranker();
//		univ_ranker ranker = new univ_ranker();
//		dept_ranker ranker = new dept_ranker();
//		name_ranker ranker = new name_ranker();
//		area_ranker ranker = new area_ranker();
//		lbjse.learning.professor.inde.dept_ranker ranker = new lbjse.learning.professor.inde.dept_ranker();
//		lbjse.learning.professor.inde.univ_ranker ranker = new lbjse.learning.professor.inde.univ_ranker();
//		lbjse.objectsearch.lbj.other_ranker ranker = new lbjse.objectsearch.lbj.other_ranker();
		TrainingSession session = new TrainingSession(3);
		
		ShowLearner shower = new ShowLearner();
		shower.show(session.getLearner());
	}
}
