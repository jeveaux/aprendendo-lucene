package com.jeveaux.palestra.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

public class IndexerAndSearcher {

	// Analisador
	private StandardAnalyzer analyzer = new StandardAnalyzer();
	
	// Diret�rio virtual para armazenar o �ndice em RAM
	private Directory indexDirectory = new RAMDirectory();
	
	/**
	 * 
	 * @param text
	 * @throws ParseException
	 * @throws IOException
	 */
	public void search(String text) throws ParseException, IOException {
		// Faz o parse da consulta e cria uma query do lucene  
		Query query = new QueryParser("title", analyzer).parse(text);
		
		int maxHits = 10;  
		  
		// Cria o acesso ao �ndice
		IndexSearcher searcher = new IndexSearcher(indexDirectory);  
		  
		// Prepara a cole��o para o resultado da pesquisa  
		TopDocCollector collector = new TopDocCollector(maxHits);  
		  
		// Executa a query e faz a pesquisa
		searcher.search(query, collector);
		
		// Separa os 10 itens mais relevantes para a consulta.
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		// Exibe os documentos encontrados na pesquisa  
		System.out.println("Pesquisando por [" + text + "] \n\t" +
				"[" + hits.length + "] resultados encontrados");
		
		for(int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			System.out.println("\t\t" + (i + 1) + " -> " + doc.get("title"));
		} 
		
	}
	
	/**
	 * 
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public void startMemoryIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		// Arquivo de �ndice sem limite de tamanho
		IndexWriter memoryIndexWriter = new IndexWriter(indexDirectory, analyzer, true,
				IndexWriter.MaxFieldLength.UNLIMITED);
		
		/*
		 * Para gravar o �ndice num arquivo em disco ao inv�s da RAM
		IndexWriter fileIndexWriter = new IndexWriter("/tmp/testindex", analyzer, true, 
				IndexWriter.MaxFieldLength.UNLIMITED);
		*/
		
		// Adicionando alguns documentos ao �ndice
		addDocument(memoryIndexWriter, "Java � no Caf� com Tapioca em Fortaleza"); 
		addDocument(memoryIndexWriter, "Cear� Java Users Group");
		addDocument(memoryIndexWriter, "Giran Solu��es e Ensino");
		addDocument(memoryIndexWriter, "Esp�rito Santo Java Users Group");
		addDocument(memoryIndexWriter, "Paulo C�sar Machado Jeveaux");
		
		// Fecha o arquivo
		memoryIndexWriter.close();
	}
	
	private void addDocument(IndexWriter indexWriter, String text) throws IOException {
		Document doc = new Document();
		doc.add(new Field("title", text, Field.Store.YES, Field.Index.ANALYZED));
		indexWriter.addDocument(doc);
	}
	
	public static void main(String[] args) {
		IndexerAndSearcher memoryIndexerAndSearcher = new IndexerAndSearcher();
		try {
			memoryIndexerAndSearcher.startMemoryIndex();
			
			memoryIndexerAndSearcher.search("Java");
			
			memoryIndexerAndSearcher.search("Cear�");
			
			memoryIndexerAndSearcher.search("Jeveaux");
			
			memoryIndexerAndSearcher.search("Algo que n�o existe");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
