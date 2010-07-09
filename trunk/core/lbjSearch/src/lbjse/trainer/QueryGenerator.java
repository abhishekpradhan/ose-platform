package lbjse.trainer;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.LBJTrecFileParser;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Program that produces queries in JSON format.
 * 
 * <p>
 * Usage: <blockquote> <code>
 *     java lbjse.trainer.QueryGenerator
 *          -mode&lt;mode&gt; [-data:&lt;file name&gt;]
 *          [-filter:&lt;file name&gt;] [-fields:&lt;fields&gt;]
 *          [-count:&lt;n&gt;]
 *   </code>
 * </blockquote>
 * 
 * <p>
 * The only required command line parameter is <code>-mode</code>. All others
 * are optional. See their descriptions below.
 * 
 * <p>
 * <b>Options:</b>
 * <dl>
 * <dt> <code>-mode:&lt;mode&gt;</code> </dt>
 * <dd> The argument to this parameter can be either
 * <ul>
 * <li> <code>all</code>, which causes all information in the query fields to
 * be included in each generated query, or
 * <li> <code>random</code>, which includes randomly selected query fields in
 * the generated query. If a document has a list of values associated with a
 * particular query field, one value is selected at random.
 * </ul>
 * </dd>
 * <dt> <code>-data:&lt;file name&gt;</code> </dt>
 * <dd> Specifies the file containing the documents from which queries will be
 * generated. If omitted, the default is
 * <code>annotated_docs_index_30000_domain_2.trec</code> </dd>
 * <dt> <code>-filter:&lt;file name&gt;</code> </dt>
 * <dd> Specifies a file containing a JSON query. Queries will be generated from
 * the documents in the data file returned by this query. If omitted, the
 * default is <code>query.txt</code>. </dd>
 * <dt> <code>-fields:&lt;fields&gt;</code> </dt>
 * <dd> Specifies which fields can potentially appear in a generated query. The
 * argument to this parameter should be a space separated list of one or more of
 * the field names <code>name</code>, <code>dept</code>, <code>univ</code>,
 * and <code>area</code>. Omitting this option is the same as specifying all
 * fields in the list. </dd>
 * <dt> <code>-count:&lt;n&gt;</code> </dt>
 * <dd> <code>&lt;n&gt;</code> should be an integer value. In <code>all</code>
 * mode, this parameter specifies the maximum number of queries that should be
 * generated (1 per document). In <code>random</code> mode, it specifies
 * queries per document. If there are <code>&lt;k&gt; &lt; &lt;n&gt;</code>
 * possible queries for a given document, then each of them will be generated
 * only once. When omitted, the default is unlimited for <code>all</code> mode
 * and 10 for <code>random<code> mode.
 *   </dd>
 * </dl>
 *
 * <p> <b>Output:</b>
 * The output will be a list of JSON queries, one per line, sent to
 * <code>STDOUT</code>.
 */
public class QueryGenerator {

	/** The possible modes. */
	public enum Mode {
		ALL, RANDOM, SINGLEW
	}

	/** The possible field names. */
	public enum Field {
		NAME, DEPT, UNIV, AREA
	}

	/** The genereated queries are allowed to contain these fields. */
	private final EnumSet<Field> fields;

	/** Returns a copy of {@link #fields}. */
	public EnumSet<Field> getFields() {
		return EnumSet.copyOf(fields);
	}

	static public Map<Field, Set<String>> fieldValuePool;

	/**
	 * Full constructor.
	 * 
	 * @param mode
	 *            Value for {@link #mode}.
	 * @param fields
	 *            Value for {@link #fields}.
	 */
	public QueryGenerator(EnumSet<Field> fields) {
		this.fields = fields;
	}

	/**
	 * Queries are generated from the specified data file after filtering by the
	 * specified query file.
	 * 
	 * @param dataFile
	 *            The file containing the documents.
	 * @param filterQueryFile
	 *            The file containing the filter query.
	 * @param count
	 *            The <code>count</code> parameter as described above.
	 * @return The generated queries.
	 */
	public Set<String> generateQueriesAllField(String dataFile, int count)
			throws IOException {
		Set<String> result = new HashSet<String>();
		if (count <= 0)
			return result;

		LBJTrecFileParser data = new LBJTrecFileParser(dataFile);

		for (DocumentFromTrec doc : data.getDocs()) {
			EnumMap<Field, List<String>> values = new EnumMap<Field, List<String>>(
					Field.class);
			boolean empty = false;

			for (Field f : fields) {
				List<String> tags = doc.getTagForField(f.toString()
						.toLowerCase());
				values.put(f, tags);
				empty |= values.get(f).size() == 0;
			}

			if (empty)
				continue;
			JSONObject query = new JSONObject();
			boolean queryGood = true;

			try {
				for (Field f : fields) {

					Set<String> allowed = fieldValuePool.get(f);

					Iterator<String> I = values.get(f).iterator();
					String s = "";
					while (I.hasNext()) {
						String word = I.next();
						queryGood &= allowed == null || allowed.contains(word);
						s += " " + word;
					}

					query.put(f.toString().toLowerCase(), s.substring(1));
				}
			} catch (JSONException e) {
				System.err.println("Error adding to query:");
				e.printStackTrace();
				System.exit(1);
			}

			if (queryGood)
				result.add(query.toString());

			if (result.size() == count) {
				return result;
			}

			break;
		}

		return result;
	}

	public Set<String> generateQueriesAllFieldSingleWord(String dataFile,
			int count) throws IOException {
		Set<String> result = new HashSet<String>();
		if (count <= 0)
			return result;

		LBJTrecFileParser data = new LBJTrecFileParser(dataFile);
		for (DocumentFromTrec doc : data.getDocs()) {
			EnumMap<Field, List<String>> values = new EnumMap<Field, List<String>>(
					Field.class);
			boolean empty = false;

			for (Field f : fields) {
				List<String> tags = doc.getTagForField(f.toString()
						.toLowerCase());
				if (tags.size() == 0)
					empty = true;
				else{
					ArrayList<String> first = new ArrayList<String>();
					first.add(tags.get(0));
					values.put(f, first);
				}
			}

			if (empty) continue;
			JSONObject query = new JSONObject();
			boolean queryGood = true;

			try {
				for (Field f : fields) {
					Set<String> allowed = fieldValuePool.get(f);

					Iterator<String> I = values.get(f).iterator();
					String s = "";
					while (I.hasNext()) {
						String word = I.next();
						queryGood &= allowed == null || allowed.contains(word);
						s += " " + word;
					}

					query.put(f.toString().toLowerCase(), s.substring(1));
				}
			} catch (JSONException e) {
				System.err.println("Error adding to query:");
				e.printStackTrace();
				System.exit(1);
			}

			if (queryGood)
				result.add(query.toString());

			if (result.size() == count) {
				break;
			}
		}

		return result;
	}

	public Set<String> generateQueriesRandomField(String dataFile, int count)
			throws IOException {
		Set<String> result = new HashSet<String>();
		if (count <= 0)
			return result;

		LBJTrecFileParser data = new LBJTrecFileParser(dataFile);
		Random rand = new Random(System.currentTimeMillis());

		for (DocumentFromTrec doc : data.getDocs()) {
			EnumMap<Field, List<String>> values = new EnumMap<Field, List<String>>(
					Field.class);
			boolean empty = false;

			for (Field f : fields) {
				List<String> tags = doc.getTagForField(f.toString()
						.toLowerCase());
				values.put(f, tags);
				empty |= values.get(f).size() == 0;
			}

			if (empty)
				continue;
			BigInteger totalQueries = BigInteger.ONE;
			for (Field f : fields)
				totalQueries = totalQueries.multiply(BigInteger.valueOf(values
						.get(f).size() + 1));
			totalQueries = totalQueries.subtract(BigInteger.ONE);

			if (totalQueries.compareTo(BigInteger.valueOf(1 << 10)) < 0) {
				int tq = totalQueries.intValue();
				LinkedList<JSONObject> allQueries = new LinkedList<JSONObject>();

				for (int i = 0; i < tq; ++i) {
					JSONObject query = new JSONObject();
					allQueries.add(query);
					int t = i + 1;

					try {
						for (Field f : fields) {
							int v = t % (values.get(f).size() + 1);
							if (v > 0)
								query.put(f.toString().toLowerCase(), values
										.get(f).get(v - 1));
							t /= values.get(f).size() + 1;
						}
					} catch (JSONException e) {
						System.err.println("Error adding to query:");
						e.printStackTrace();
						System.exit(1);
					}
				}

				Collections.shuffle(allQueries);
				int i = 0;
				for (Iterator<JSONObject> it = allQueries.iterator(); it
						.hasNext()
						&& i < count; ++i)
					result.add(it.next().toString());
			} else {
				Field[] fieldArray = fields.toArray(new Field[0]);

				try {
					for (int i = 0; i < count; ++i) {
						JSONObject query = new JSONObject();
						int fieldMask = rand.nextInt((1 << fields.size()) - 1) + 1;
						for (int j = 0; j < fieldArray.length; ++j)
							if (((1 << j) & fieldMask) > 0)
								query.put(fieldArray[j].toString()
										.toLowerCase(), values.get(
										fieldArray[j]).get(
										rand.nextInt(values.get(fieldArray[j])
												.size())));
						result.add(query.toString());
					}
				} catch (JSONException e) {
					System.err.println("Error adding to query:");
					e.printStackTrace();
					System.exit(1);
				}
			}

			break;
		}

		return result;
	}

	/** The default value for the <code>-data</code> command line parameter. */
	private static final String defaultDataFile = "annotated_docs_index_30000_domain_2.trec";
	/**
	 * The default value for the <code>-fields</code> command line parameter.
	 */
	private static final EnumSet<Field> defaultFields = EnumSet
			.allOf(Field.class);
	/**
	 * The default value for the <code>-count</code> command line parameter in
	 * <code>random</code> mode.
	 */
	private static final int defaultCount = 10;

	public static void main(String[] args) throws Exception {
		Mode mode = null;
		String dataFile = null;
		EnumSet<Field> fields = null;
		int count = 0;
		fieldValuePool = new EnumMap<Field, Set<String>>(Field.class);

		try {

			for (String arg : args) {
				if (arg.startsWith("-mode:")) {
					if (mode != null)
						throw new Exception();
					mode = Mode.valueOf(arg.substring(6).toUpperCase());
				} else if (arg.startsWith("-data:")) {
					if (dataFile != null)
						throw new Exception();
					dataFile = arg.substring(6);
				}
				// -valUNIV:univ.txt -valDEPT:dept.txt
				else if (arg.startsWith("-val") && arg.indexOf(':') != -1) {
					int colon = arg.indexOf(':');
					String field = arg.substring(4, colon);
					String valueFile = arg.substring(colon + 1);
					fieldValuePool.put(Field.valueOf(field.toUpperCase()),
							readValuesFromFile(valueFile));
				} else if (arg.startsWith("-fields:")) {
					if (fields != null)
						throw new Exception();
					fields = EnumSet.noneOf(Field.class);
					for (String field : arg.substring(8).split("[,\\s]"))
						fields.add(Field.valueOf(field.toUpperCase()));
				} else if (arg.startsWith("-count:")) {
					if (count != 0)
						throw new Exception();
					count = Integer.parseInt(arg.substring(7));
					if (count <= 0)
						throw new Exception();
				} else
					throw new Exception();
			}

			if (mode == null)
				throw new Exception();
			if (dataFile == null)
				dataFile = defaultDataFile;
			if (fields == null)
				fields = defaultFields;
			if (count == 0)
				count = mode == Mode.ALL ? Integer.MAX_VALUE : defaultCount;
		} catch (Exception e) {
			System.err
					.println("usage: java lbjse.trainer.QueryGenerator -mode:<mode> [options]\n"
							+ "options:\n"
							+ "  -data:<file name>\n"
							+ "  -filter:<file name>\n"
							+ "  -fields:<fields>\n"
							+ "  -count:<n>\n"
							+ "notes:\n"
							+ "  <mode> must be either \"all\" or \"random\".\n"
							+ "  <fields> is a space separated list containing words from the set\n"
							+ "    { \"name\", \"dept\", \"univ\", \"area\" }.\n"
							+ "  <n> must be a positive integer.");
			System.exit(1);
		}

		QueryGenerator generator = new QueryGenerator(fields);
		Set<String> queries = null;
		switch (mode) {
		case ALL:
			queries = generator.generateQueriesAllField(dataFile, count);
			break;
		case RANDOM:
			queries = generator.generateQueriesRandomField(dataFile, count);
			break;
		case SINGLEW:
			queries = generator.generateQueriesAllFieldSingleWord(dataFile,
					count);
			break;
		default:
			break;
		}
		for (String query : queries)
			System.out.println(query);
	}

	public static Set<String> readValuesFromFile(String fileName)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		Set<String> lines = new HashSet<String>();
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			lines.add(line);
		}
		return lines;
	}
}
