package ose.processor.splock.utests;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.DocPositionIterator;
import ose.processor.cascader.FeatureQuery;

public class GenerateSpanByOldProcessor {

	String indexPath ;
	
	public GenerateSpanByOldProcessor(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public void generate(String testOutputFile, String featureQuery) throws IOException, IllegalAccessException, InstantiationException{
		IndexReader reader = IndexReader.open(indexPath);
		OSQueryParser parser = new OSQueryParser();
		FeatureQuery query = parser.parseFeatureQuery(featureQuery);
		DocPositionIterator iterator = query.getDocPositionIterator(reader);
		int count = 0 ;
		int MAX_COUNT = 300000;
		PrintWriter writer = new PrintWriter(testOutputFile);
		writer.println("<UTEST>");
		writer.println("<INDEX>");
		writer.println(indexPath);
		writer.println("</INDEX>");
		writer.println("<FEATURE>");
		writer.println(featureQuery);
		writer.println("</FEATURE>");
		int spanCount = 0 ;
		int docCount = 0;
		while (iterator.next() && count <= MAX_COUNT){
			
			do {
				writer.println("<SPAN>");
				writer.println("	<DOCID>" + iterator.getDocID() + "</DOCID>");
				writer.println("	<POS>" + iterator.getPosition() + "</POS>");
				writer.println("</SPAN>");
				spanCount  += 1;
			} while (iterator.nextPosition());
			count += 1;
			docCount += 1;
		
		}
		writer.println("<TOTALDOC>" +  docCount + "</TOTALDOC>");
		writer.println("<TOTALSPAN>" +  spanCount + "</TOTALSPAN>");
		
		writer.println("</UTEST>");
		writer.close();
		reader.close();
		System.out.println("Done : " + testOutputFile);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
				
//		String featureQuery = "Phrase(Token('$'),Number_body(_range(150,160))) Phrase(Number_body(_range(6,6)),Token(mp))";
//		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/collection4";
//		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/camera_10";
//		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/random100pages";
		String indexPath = "C:\\working\\annieIndex\\combine_testing_index";
//		String featureQuery = "Proximity(Token(camera),Number_body(),-1,1)";		
//		String featureQuery = "HTMLTitle(canon)  Token(canon) HTMLTitle(g10)  Token(g10)  Proximity(Number_body(_range(8,30)),Token(megapixel,megapixels,mp),-3,1) Proximity(Number_body(),Token(megapixel,megapixels,mp),-3,1)  Proximity(Number_title(_range(8,30)),HTMLTitle(megapixel,megapixels,mp),-3,0) Proximity(Number_body(_range(5,20)),Phrase(Token(optical),Token(zoom)),-4,3) Proximity(Number_body(),Phrase(Token(optical),Token(zoom)),-4,3)) Phrase(Number_title(_range(5,20)),HTMLTitle(x)) Phrase(Token('$'),Number_body(_range(300,500))) Proximity(Token(price),Phrase(Token('$'),Number_body(_range(300,500))),-3,3) Proximity(Phrase(Token(your,our,special,sale),Token(price)),Phrase(Token('$'),Number_body(_range(300,500))),-7,0)  Token(checkout)  Token(shop shopping)  Token(shipping shipped ships)  Phrase(add to cart) Token(availability)";
//		String featureQuery = "HTMLTitle(john)  Token(john)  Token(science) Proximity(Token(department),Token(science),-5,5) Proximity(Token(professor),Token(science),-3,3) Proximity(Token(university),Token(illinois),-5,5) Token(illinois) Proximity(Token(professor),Token(illinois),-6,0) Proximity(Phrase(research group),Token(computer),-10,10) Proximity(Token(group),Token(computer),-3,3) Proximity(Or(Phrase(my research),Phrase(research interests),Phrase(research summary)),Token(computer),-50,0) Token(computer) HTMLTitle(homepage)  Token(professor)  Token(publication,publications,papers)  Phrase(Token(associate,adjunct,assistant),Token(professor))  Token(graduated,received) Phrase(program,Token(chair,committee)) Phrase(my research) Phrase(research interests) Token(biography) Phrase(curriculum vitae) Token(cv)";
//		String featureQuery = "HTMLTitle(lenovo) Proximity(Token(lenovo),Token(manufacturer),-3,3) Token(lenovo) Proximity(Number_body(_range(12.1,15.6)),Token('\"',in,inch),-2,0) Proximity(Number_title(_range(12.1,15.6)),HTMLTitle('\"',in,inch),-2,0) Proximity(Number_body(_range(12.1,15.6)),Or(Phrase(screen size),Token(display)),0,7) Proximity(Number_body(_range(12.1,15.6)),Token(widescreen,wxga,xga,tft),-8,0) Proximity(Number_body(_range(119.95,384.05)),Token(gb),-2,0) Proximity(Number_title(_range(119.95,384.05)),HTMLTitle(gb),-2,0) Proximity(Number_body(_range(119.95,384.05)),Token(rpm),-5,5) Proximity(Number_body(_range(119.95,384.05)),Token(drive,hdd),-5,5) Proximity(Phrase(processor speed),Number_body(_range(1.95,2.55)),-4,0) Proximity(Number_body(_range(1.95,2.55)),Token(ghz),-2,0) Proximity(Number_title(_range(1.95,2.55)),HTMLTitle(ghz),-2,0) Phrase(Token('$'),Number_body(_range(563.83,1597.94))) Proximity(Token(price),Phrase(Token('$'),Number_body(_range(563.83,1597.94))),-4,3) Phrase(Top(Token('$'),3),Number_body(_range(563.83,1597.94))) Proximity(Top(Phrase(price,':'),1),Number_body(_range(563.83,1597.94)),-10,0) HTMLTitle(laptop)  Token(availability)  Token(laptop)  Phrase(Token(product),Token(descriptions,description,specification,specifications))  Token(warranty)  Token(manufacturer) ";
		String featureQuery = "HTMLTitle(john)  Token(john)  Token(science) Proximity(Token(department),Token(science),-5,5) Proximity(Token(professor),Token(science),-3,3) Proximity(Token(university),Token(illinois),-5,5) Token(illinois) Proximity(Token(professor),Token(illinois),-6,0) Proximity(Phrase('research group'),Token(computer),-10,10) Proximity(Phrase('group'),Token(computer),-3,3) Proximity(Or(Phrase('my research'),Phrase('research interests'),Phrase('research summary')),Token(computer),-50,0) Token(computer) HTMLTitle(homepage)  Token(professor)  Token(publication,publications,papers)  Phrase(Token(associate,adjunct,assistant),Token(professor))  Token(graduated,received) Phrase(program,Token(chair,committee)) Phrase('my research') Phrase('research interests') Token(biography) Phrase('curriculum vitae') Token(cv)";
//		String featureQuery = "HTMLTitle(system)  Token(system)  Phrase(system) Token(john) Phrase(john) Proximity(Token(instructor),Token(john),-5,0) Proximity(Token(fall),Token(semester,term),-5,5) Proximity(Token(fall),Number_body(_range(1900,2100)),-3,3) Proximity(Token(fall,winter,summer,spring,autumn),Number_body(_range(2009,2009)),-3,3) Number_body(_range(2009,2009)) Number_title(_range(2009,2009)) Proximity(Token(edu),Token(illinois),-3,3) Proximity(Top(Token(edu),1),Token(illinois),-3,3) Proximity(Token(university),Token(illinois),-3,3) Token(illinois) Token(computing) Phrase(computing) Token(instructor) Token(semester) Token(grading) Token(homework) Token(textbook) Token(announcements) Token(midterm) Phrase(course information) Phrase(class schedule) Token(spring,fall,summer,winter)";
		
		String testCheckFile = System.getProperty("user.dir") + "/utests/Test11.xml";
		GenerateSpanByOldProcessor generator = new GenerateSpanByOldProcessor(indexPath);
		generator.generate(testCheckFile, featureQuery);
		
	}

}
