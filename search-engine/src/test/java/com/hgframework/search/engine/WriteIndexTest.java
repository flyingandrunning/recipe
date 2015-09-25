/**
 * 
 */
package com.hgframework.search.engine;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * <pre>
 * //创建一个分析器，这里使用的是标准分析器，适用于大多数场景，并且在StandardAnalyzer中包括了部分中文分析处理功能，虽然其本身也有一个中文分析器ChineseAnalyzer，  
 * //不过ChineseAnalyzer将会在5.0的版本中被去掉，使用StandardAnalyzer即可。  
 * // 另外在analyzers-common中，包括了针对很多种不同语言的分析器，其中包括中文分析器
 * Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
 * 
 * <pre>
 * // Directory是用于索引文件的存储的抽象类，其子类有将索引文件写到文件的，也有直接放到内存中的，这里的RAMDirectory就是放在内存中索引
 * //优点是速度快，缺少是不适合于大量数据的索引。这里的数据比较少，所以使用RAMDirctory非常适合。
 * 
 * <pre>
 * // 具体的可以查看Directory,
 * // RAMDirectory,FSDirectory等API说明，这里要强调一下的是FSDirectory是一个文件索引存储的抽象类，下面还有三个子类:MMapDirectory,
 * // NIOFSDirectory, SimpleFSDirectory，根据不同的操作系统及使用场景进行不同的选择了。
 * Directory index = new RAMDirectory();
 * 
 * <pre>
 * 
 * <pre>//IndexWriterConfig包括了所有创建IndexWriter的配置，一旦IndexWriter创建完成后，此时再去修改IndexWriterConfig是不会影响到IndexWriter实例的，此时如果想获取正确的IndexWirter的配置，最好是通过IndexWirter.getConfig()方法了，另外IndexWriterConfig本身也是一个final类。
 * 
 * <pre>
 * IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44, analyzer);
 * 
 * // 顾名思义,IndexWriter是用于维护及增加索引的
 * IndexWriter w = new IndexWriter(index, config);
 * 
 * <pre>
 * /通过查询解析器QueryParser创建一个查询Query.  
 * //QueryParser是JavaCC(http://javacc.java.net)编译的其中最重要的方法就是QueryParserBase.parse(String)，
 * //特别需要注意的是QueryParser不是线程安全的
 * 
 * <pre>
 * Query q = new QueryParser(Version.LUCENE_44, "title", analyzer).parse(querystr);
 * 
 * //这个表示每次最多显示的结果数  
 * int hitsPerPage = 10;  
 * //创建索引读取器  
 * IndexReader reader = IndexReader.open(index);  
 * //创建索引查询器  
 * IndexSearcher searcher = new IndexSearcher(reader);  
 * //以TopDocs的方式返回最多hitsPerPage的查询结果  
 * TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);  
 * //执行查询  
 * searcher.search(q, collector);  
 * ScoreDoc[] hits = collector.topDocs().scoreDocs;  
 * 
 * 
 * @author ghuang
 *
 * @创建时间 2015年9月8日
 *
 */
public class WriteIndexTest {

	/**
	 * 1、Lucene的核心jar包
	 * 
	 * lucene-core-5.2.1.jar
	 * 
	 * lucene-analyzers-common-5.2.1.jar
	 * 
	 * lucene-queryparser-5.2.1.jar
	 * 
	 * 2、主要开发包说明
	 * 
	 * org.apache.lucene.analysis：语言分析器，主要用于分词
	 * 
	 * org.apache.lucene.document：索引文档的管理
	 * 
	 * org.apache.lucene.index：索引管理，如增、删、改
	 * 
	 * org.apache.lucene.queryparser：查询分析
	 * 
	 * org.apache.lucene.search：检索管理
	 * 
	 * org.apache.lucene.store：数据存储管理
	 * 
	 * org.apache.lucene.util：工具包
	 * 
	 * 3、写入索引操作的核心类
	 * 
	 * Directory：代表索引文档的存储位置，这是一个抽象类有FSDirectory和RAMDirectory两个主要子类。前者将索引写入文件系统，
	 * 后者将索引文档写入内存。
	 * 
	 * Analyzer：建立索引时使用的分析器，主要子类有StandardAnalyzer（一个汉字一个词），
	 * 还可以由第三方提供如开源社区提供一些中文分词器。
	 * 
	 * IndexWriterConfig：操作索引库的配置信息
	 * 
	 * IndexWriter：建立索引的核心类，用来操作索引（增、删、改）
	 * 
	 * Document：代表一个索引文档
	 * 
	 * Field：代表索引文档中存储的数据，新版本的Lucene进行了细化给出了多个子类：IntField、LongField、FloatField、
	 * DoubleField、TextField、StringField等。
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartLuenceWithWriteIndex() throws Exception {
		/* 标准分词器，一个单词一个索引 */
		Analyzer a = new StandardAnalyzer();
		/* 硬盘存储 */
		Directory dir = FSDirectory.open(Paths.get("./index"));
		/* 索引配置 */
		IndexWriterConfig iwc = new IndexWriterConfig(a);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		/* 写入索引 */
		IndexWriter iw = new IndexWriter(dir, iwc);
		/* 建立文档 */
		Document doc = new Document();
		doc.add(new TextField("info", "这里用的是中文搜索技术，请注意哦", Field.Store.YES));
//		doc.add(new StringField("lucene", "lucene lucene lucene", Field.Store.YES));
		/* 建立文档索引，后期方便查询 */
		iw.addDocument(doc);
		iw.close();
		dir.close();

	}

	/**
	 * IndexReader：读取索引的工具类，常用子类有DirectoryReader
	 * 
	 * IndexSearch：查询索引的核心类
	 * 
	 * QueryParser：查询分析器，表示从哪里查用哪个分析器
	 * 
	 * Query：代表一次查询
	 * 
	 * TopDocs：封装了匹配情况，比如匹配多少个
	 * 
	 * ScoreDoc：匹配的数据，里面封装了索引文档的得分和索引ID
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartLuenceWithReadIndex() throws Exception {
		Analyzer a = new StandardAnalyzer();
		Directory dir = FSDirectory.open(Paths.get("./index"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(reader);
		QueryParser parser = new QueryParser("info", a);
		Query query = parser.parse("中文");
		TopDocs topDocs = is.search(query, 1000);
		System.out.println("总共匹配多少个：" + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		// 应该与topDocs.totalHits相同
		System.out.println("多少条数据：" + hits.length);
		for (ScoreDoc scoreDoc : hits) {
			System.out.println("匹配得分：" + scoreDoc.score);
			System.out.println("文档索引ID：" + scoreDoc.doc);
			Document document = is.doc(scoreDoc.doc);
			System.out.println(document.get("info"));
//			System.out.println(document.get("lucene"));
		}
		reader.close();
		dir.close();
	}

}
