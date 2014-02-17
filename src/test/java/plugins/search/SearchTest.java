package plugins.search;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;

import plugins.search.Search.SearchResult;

public class SearchTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Search search = new Search(Logger.getLogger(Search.class.getName()));
		try {
			SearchResult[] results = search.runSearch("votre",
					new File[] { new File("data") });
			assertEquals(1, results.length);
			assertTrue("freemind_fr.mm".equals(results[0].getFileName()));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

}
