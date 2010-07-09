package ose.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lbjse.data.DocumentFromTrec;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.CommandLineOption;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.html.HtmlParser;
import ose.html.ParsedHtmlDocument;
import ose.index.IndexFieldConstant;
import ose.index.TrecDocument;
import ose.index.Utils;
import ose.index.tool.TrecFilter;
import ose.query.OQuery;

public class IndexExporter {

	public IndexExporter() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * This is the ExportDataServlet, to export data onto a temporary file
	 */
	public String exportTrecFileFromIndex(int indexId, int domainId) throws IOException, SQLException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		if (iinfo == null)
			throw new SQLException("no index is found in the database");
		IndexReader cacheReader = IndexReader.open(iinfo.getCachePath());
        File temp = File.createTempFile("annotated_docs_index_" + indexId + "_domain_" + domainId + "_", ".trec");
        Map<Integer, ArrayList<String>> docTagsMap = getTagMapForIndexDomain(indexId, domainId);
        int count = 0;
        PrintWriter output = new PrintWriter(temp, "utf-8");
        
        HtmlParser parser = new HtmlParser();
        for (Integer docId : docTagsMap.keySet()){
    		Document doc = cacheReader.document(docId);    		
    		String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
    		String html = doc.get(IndexFieldConstant.FIELD_DOCUMENT_CONTENT);
    		ParsedHtmlDocument parsed = parser.parse(html);
    		DocumentFromTrec trec = new DocumentFromTrec(docId, url, parsed.getTitle(), parsed.getPlainBody());
    		trec.setTags(docTagsMap.get(docId));
    		trec.serialize(output);
            count += 1;
        }
        System.out.println("Exported " + count + " documents");
        output.close();
        cacheReader.close();
        return temp.getAbsolutePath();
	}

	/*
	 * This is for learning components, that get data from index
	 */
	public void exportTrecForLearning(int indexId, int domainId, double trainingRatio, String outputTraining, String outputTesting) throws IOException, SQLException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		if (iinfo == null)
			throw new SQLException("no index is found in the database");
		IndexReader cacheReader = IndexReader.open(iinfo.getCachePath());
        Map<Integer, ArrayList<String>> docTagsMap = getTagMapForIndexDomain(indexId, domainId);
        
        
        List<Integer> testingDocIds = new ArrayList<Integer>();
        List<Integer> trainingDocIds = new ArrayList<Integer>();
        splitDocId(docTagsMap.keySet(), trainingDocIds, testingDocIds, trainingRatio);
        exportIndexDocsToFile(outputTraining, cacheReader, trainingDocIds, docTagsMap);
        exportIndexDocsToFile(outputTesting, cacheReader, testingDocIds, docTagsMap);
        cacheReader.close();
	}
	
	public void exportAnnotatedTrec(int indexId, int domainId, String output, boolean plainTrec) throws IOException, SQLException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		if (iinfo == null)
			throw new SQLException("no index is found in the database");
		IndexReader cacheReader = IndexReader.open(iinfo.getCachePath());
        Map<Integer, ArrayList<String>> docTagsMap = getTagMapForIndexDomain(indexId, domainId);
        
        
        List<Integer> docIds = new ArrayList<Integer>(docTagsMap.keySet());
        if (plainTrec)
        	exportIndexDocsToFile(output, cacheReader, docIds , docTagsMap);
        else
        	exportRawIndexDocsToFile(output, cacheReader, docIds, docTagsMap);
        cacheReader.close();
	}

	public void exportAnnotation(int indexId, int domainId, String output) throws IOException, SQLException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		if (iinfo == null)
			throw new SQLException("no index is found in the database");
		IndexReader cacheReader = IndexReader.open(iinfo.getCachePath());
		PrintWriter writer = new PrintWriter(output);
        Map<Integer, ArrayList<String>> docTagsMap = getTagMapForIndexDomain(indexId, domainId);
        for (Map.Entry<Integer, ArrayList<String>> entry : docTagsMap.entrySet()){
        	int docId = entry.getKey();
        	ArrayList<String> tags = entry.getValue();
        	JSONObject jsonTags = new JSONObject();
    		try {
				for (String valuePair : tags){
					String key = valuePair.substring(0,valuePair.indexOf(":"));
					String value = valuePair.substring(valuePair.indexOf(":") + 1);
					if (jsonTags.has(key)){
						jsonTags.getJSONArray(key).put(value);
					}
					else{
						JSONArray arr = new JSONArray();
						arr.put(value);
						jsonTags.put(key, arr);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		TrecDocument doc = new TrecDocument(cacheReader, docId);
    		writer.println(jsonTags.toString());
    		writer.println(TrecFilter.urlNormalize(doc.getUrl()));
        }
        writer.close();
		
	}    
    
	/**
	 * @param output
	 * @param cacheReader
	 * @param docIds
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void exportIndexDocsToFile(String output,
			IndexReader cacheReader, List<Integer> docIds, Map<Integer, ArrayList<String>> docTagsMap)
			throws FileNotFoundException, UnsupportedEncodingException,
			CorruptIndexException, IOException {
		PrintWriter writer = new PrintWriter(output, "utf-8");
        int count = 0;
        for (Integer docId : docIds){
    		TrecDocument trecDoc = new TrecDocument(cacheReader, docId);
    		if (!trecDoc.isParsed()){
    			trecDoc.parseHtml(true);
    		}
    		DocumentFromTrec trec = new DocumentFromTrec(docId, trecDoc.getUrl(), trecDoc.getTitle(), trecDoc.getPlainBody());
    		trec.setTags(docTagsMap.get(docId));
    		trec.serialize(writer);
            count += 1;
        }
        System.out.println("Exported " + count + " documents to " + output);
        writer.close();
	}
	
	/**
	 * @param output
	 * @param cacheReader
	 * @param docIds
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void exportRawIndexDocsToFile(String output,
			IndexReader cacheReader, List<Integer> docIds, Map<Integer, ArrayList<String>> docTagsMap)
			throws FileNotFoundException, UnsupportedEncodingException,
			CorruptIndexException, IOException {
		PrintWriter writer = Utils.getPrintWriter(output);
        int count = 0;
        for (Integer docId : docIds){
    		TrecDocument trecDoc = new TrecDocument(cacheReader, docId);
    		if (!trecDoc.isParsed()){
    			trecDoc.parseHtml(true);
    		}
    		trecDoc.setTags(docTagsMap.get(docId));
    		trecDoc.serialize(writer);
            count += 1;
        }
        writer.close();
        System.out.println("Exported " + count + " documents to " + output);
	}
	
	
	/*
	 * This is the ExportDataServlet, to export everything data onto a temporary file
	 */
	public String exportAllTrecFileFromIndex(int indexId, int domainId) throws IOException, SQLException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		if (iinfo == null)
			throw new SQLException("no index is found in the database");
		IndexReader cacheReader = IndexReader.open(iinfo.getCachePath());
        File temp = File.createTempFile("annotated_docs_index_" + indexId + "_domain_" + domainId + "_", ".trec");
        PrintWriter output = new PrintWriter(temp, "utf-8");
        Map<Integer, ArrayList<String>> docTagsMap = getTagMapForIndexDomain(indexId, domainId);
        int count = 0;
        
        HtmlParser parser = new HtmlParser();
        for (int docId = 0; docId < cacheReader.numDocs() ; docId ++){
        	if (cacheReader.isDeleted(docId)) continue;
    		Document doc = cacheReader.document(docId);    		
    		String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
    		String html = doc.get(IndexFieldConstant.FIELD_DOCUMENT_CONTENT);
    		ParsedHtmlDocument parsed = parser.parse(html);
    		if (parsed == null)
    			continue;
    		DocumentFromTrec trec = new DocumentFromTrec(docId, url, parsed.getTitle(), parsed.getPlainBody());
    		trec.setTags(docTagsMap.get(docId));
    		trec.serialize(output);
            count += 1;
            if (count % 10 == 0){
            	System.out.print(".");
            	if (count % 200 == 0)
            		System.out.println();
            }
        }
        System.out.println("Exported " + count + " documents");
        output.close();
        cacheReader.close();
        return temp.getAbsolutePath();
	}
	
	public void exportAllHtmlTrecFileFromIndex(int indexId, String outputFile) throws IOException, SQLException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		if (iinfo == null)
			throw new SQLException("no index is found in the database");
		IndexReader cacheReader = IndexReader.open(iinfo.getCachePath());
        PrintWriter writer = Utils.getPrintWriter(outputFile);
        int count = 0;
        
        for (int docId = 0; docId < cacheReader.numDocs() ; docId ++){
        	if (cacheReader.isDeleted(docId)) continue;
    		TrecDocument trec = new TrecDocument(cacheReader, docId);
    		trec.serialize(writer);
            count += 1;
            if (count % 10 == 0){
            	System.out.print(".");
            	if (count % 200 == 0)
            		System.out.println();
            }
        }
        System.out.println("Exported " + count + " documents");
        writer.close();
        cacheReader.close();
	}

	private void splitDocId(Collection<Integer> docIds, List<Integer> training, List<Integer> testing, double trainingRatio){
		training.clear();
		testing.clear();
		for (Integer docId: docIds) {
			if (Math.random() <= trainingRatio) {
				training.add(docId);
			}
			else
				testing.add(docId);
		}
	}
	/**
	 * @param indexId
	 * @param domainId
	 * @param domain
	 * @return
	 * @throws SQLException
	 */
	public static Map<Integer, ArrayList<String>> getTagMapForIndexDomain(
			int indexId, int domainId) throws SQLException {
		OQuery domain = new OQuery(domainId);
		Map<Integer, ArrayList<String>> docTagsMap = new HashMap<Integer, ArrayList<String>>();
        DocTagManager dtMan = new DocTagManager();
        for (DocTag tag : dtMan.getAllTagForIndexDomain(indexId, domainId)){            
            int docId = tag.getDocId();
            if (!docTagsMap.containsKey(docId)){
                docTagsMap.put(docId, new ArrayList<String>());
            }
            String tagString = domain.getFieldNameFromId(tag.getFieldId()) + 
            	":" + tag.getValue();
            docTagsMap.get(docId).add(tagString);
        }
		return docTagsMap;
	}
	
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("Examples : \n"
						+ "- Export all document into a TEMPORARY plain trec file:\n"
						+ "\t IndexExporter --mode all  --index [indexId] --domainId [domainId]\n"
						+ "- Export all document into a html trec file:\n"
						+ "\t IndexExporter --mode allhtml  --index [indexId] --output [output file] \n"
						+ "- Export only tagged document into plain trec file:\n"
						+ "\t IndexExporter --mode tagtrec --index [indexId] --domainId [domainId] --output output_trec_file\n"
						+ "- Export only tagged document into html trec file:\n"
						+ "\t IndexExporter --mode raw  --index [indexId] --domainId [domainId] --output output_trec_file\n"
						+ "- Export all document into plain trec file but split them into training-testing partitions:\n"
						+ "\tIndexExporter --mode split --index [indexId] --domainId [domainId] --train output_training_file --test output_testing_file --ratio training_ratio \n");

		options.require(new String[] { "mode", "index" });
		int indexId = options.getInt("index");
		String mode = options.getString("mode");
		
		IndexExporter exporter = new IndexExporter();
		if ("split".equals(mode)){
			options.require(new String[] { "train", "test", "domain" });
			int domainId = options.getInt("domain");
			String outputTraining = options.getString("train");
			String outputTesting = options.getString("test");
			double ratio = options.getDouble("ratio");
			exporter.exportTrecForLearning(indexId, domainId, ratio, outputTraining, outputTesting);
		}
		else if ("all".equals(mode)){
			options.require(new String[] { "domain" });
			int domainId = options.getInt("domain");
			String output = exporter.exportAllTrecFileFromIndex(indexId, domainId);
			System.out.println("Exported to " + output);
		}
		else if ("allhtml".equals(mode)){
			options.require(new String[]{"output"});
			String output = options.getString("output"); 
			exporter.exportAllHtmlTrecFileFromIndex(indexId, output);
			System.out.println("Exported to " + output);
		}
		else if ("tagtrec".equals(mode)){
			options.require(new String[]{"output", "domain"});
			int domainId = options.getInt("domain");
			String output = options.getString("output");
			exporter.exportAnnotatedTrec(indexId, domainId, output, true);
			System.out.println("Exported to " + output);
		}
		else if ("raw".equals(mode)){
			options.require(new String[]{"output", "domain"});
			int domainId = options.getInt("domain");
			String output = options.getString("output");
			exporter.exportAnnotatedTrec(indexId, domainId, output,false);
			System.out.println("Exported to " + output);
		}
		else if ("tagonly".equals(mode)){
			options.require(new String[]{"output", "domain"});
			int domainId = options.getInt("domain");
			String output = options.getString("output");
			exporter.exportAnnotation(indexId, domainId, output);
			System.out.println("Exported to " + output);
		}
		else {
			System.err.println("Unknown mode : " + mode);
		}
	}
}
