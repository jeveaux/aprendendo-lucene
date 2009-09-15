package com.jeveaux.palestra.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * 
 * @author jeveaux
 *
 */
public class Indexer {

	/**
	 * 
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public void startIndexer() throws CorruptIndexException, LockObtainFailedException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriter iw = new IndexWriter("/tmp/lucene-test-index", analyzer, true, MaxFieldLength.UNLIMITED);
		
		Document doc = new Document();
		doc.add(new Field("body", "This is my TEST document", Field.Store.YES,
				Field.Index.TOKENIZED));
		
		iw.addDocument(doc);
		iw.optimize();
		iw.close();
		
	}

}
