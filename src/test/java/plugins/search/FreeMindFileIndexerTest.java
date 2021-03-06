package plugins.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;

public class FreeMindFileIndexerTest {

	private static final Logger _logger = Logger
			.getLogger(FreeMindFileIndexerTest.class.getName());

	String mapsDir = "data";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void indexFileOrDirectory() {

		FileSearchModel indexer = new FileSearchModel(
				Logger.getLogger(FreeMindFileIndexerTest.class.getName()));
		// try to add file into the index
		try {
			Directory index = indexer.indexFileOrDirectory(mapsDir);
			DirectoryReader reader = DirectoryReader.open(index);

			File folder = new File(mapsDir);
			File[] listOfFiles = folder.listFiles();

			_logger.info("Should be " + listOfFiles.length
					+ " files in folder " + mapsDir);

			assertSame(listOfFiles.length, reader.numDocs());
		} catch (IOException e) {
			_logger.warning("Failed to index " + mapsDir);
			fail();
		}
	}

	@Test
	public void findVotre() {
		try {
			FileSearchModel indexer = new FileSearchModel(Logger
					.getLogger(FreeMindFileIndexerTest.class.getName()));
			// try to add file into the index 
			Directory index = indexer.indexFileOrDirectory(new File(mapsDir));

			IndexSearcher searcher = indexer.getSearcher(index);
			Query query = indexer.getQuery("votre");

			TopDocs results = indexer.doSearch(query, 10, searcher);

			ScoreDoc[] hits = results.scoreDocs;
			_logger.info("Results: " + hits.length);

			assertEquals(1, hits.length);

			int docId = hits[0].doc;
			Document d = searcher.doc(docId);
			_logger.info(indexer.getPath(d) + " score=" + hits[0].score);
			assertTrue("freemind_fr.mm".equals(indexer.getFilename(d)));

			String[] nameStrings = indexer.getFilepathsFromSearchResults(
					searcher, results);
			assertEquals(1, nameStrings.length);
			_logger.info("Filename: " + nameStrings[0]);
			assertTrue(nameStrings[0].equals(indexer.getPath(d)));

		} catch (IOException | ParseException e) {
			_logger.warning("Failed to start indexer" + e.getMessage());
			fail();
		}
	}
}
