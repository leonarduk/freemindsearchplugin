package plugins.search;

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

public class Search {
	private FreeMindFileIndexer indexer;
	private Logger _logger;
	private IndexSearcher searcher;
	private ScoreDoc[] hits;

	public Search(Logger logger) {
		this._logger = logger;
	}

	public SearchResult[] runSearch(String searchString, File[] mapsFiles)
			throws IOException, ParseException {
		indexer = new FreeMindFileIndexer(_logger);
		_logger.fine("runSearch :" + searchString + " in " + mapsFiles);
		Directory index = indexer.indexFileOrDirectory(mapsFiles);

		searcher = indexer.getSearcher(index);
		_logger.fine("Run search");
		Query query = indexer.getQuery(searchString);
		DirectoryReader reader = DirectoryReader.open(index);
		TopDocs results = indexer.doSearch(query, reader.numDocs(), searcher);

		hits = results.scoreDocs;
		_logger.fine("Returned: " + hits.length + " results");
		SearchResult[] listData = new SearchResult[hits.length];
		for (int i = 0; i < hits.length; i++) {
			Document d;
			try {
				int docId = hits[i].doc;
				d = searcher.doc(docId);
				listData[i] = new SearchResult(hits[i].doc,
						indexer.getFilename(d), indexer.getPath(d),
						hits[i].score, hits[i].shardIndex);
				_logger.fine(i + " " + listData[i]);
			} catch (IOException e) {
				_logger.warning("Failed:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return listData;
	}

	public class SearchResult {
		private String fileName;
		private String path;
		private int docId;

		private float score;
		private int shardIndex;

		public SearchResult(int docId, String fileName, String path,
				float score, int shardIndex) {
			this.fileName = fileName;
			this.path = path;
			this.docId = docId;
			this.score = score;
			this.shardIndex = shardIndex;
		}

		@Override
		public String toString() {
			return getFileName();
		}

		public int getDocId() {
			return docId;
		}

		public float getScore() {
			return score;
		}

		public int getShardIndex() {
			return shardIndex;
		}

		public String getFileName() {
			return this.fileName;
		}

		public String getPath() {
			return this.path;
		}

	}
}