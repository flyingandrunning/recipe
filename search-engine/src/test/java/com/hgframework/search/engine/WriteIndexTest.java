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
 * //����һ��������������ʹ�õ��Ǳ�׼�������������ڴ����������������StandardAnalyzer�а����˲������ķ��������ܣ���Ȼ�䱾��Ҳ��һ�����ķ�����ChineseAnalyzer��  
 * //����ChineseAnalyzer������5.0�İ汾�б�ȥ����ʹ��StandardAnalyzer���ɡ�  
 * // ������analyzers-common�У���������Ժܶ��ֲ�ͬ���Եķ����������а������ķ�����
 * Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
 * 
 * <pre>
 * // Directory�����������ļ��Ĵ洢�ĳ����࣬�������н������ļ�д���ļ��ģ�Ҳ��ֱ�ӷŵ��ڴ��еģ������RAMDirectory���Ƿ����ڴ�������
 * //�ŵ����ٶȿ죬ȱ���ǲ��ʺ��ڴ������ݵ���������������ݱȽ��٣�����ʹ��RAMDirctory�ǳ��ʺϡ�
 * 
 * <pre>
 * // ����Ŀ��Բ鿴Directory,
 * // RAMDirectory,FSDirectory��API˵��������Ҫǿ��һ�µ���FSDirectory��һ���ļ������洢�ĳ����࣬���滹����������:MMapDirectory,
 * // NIOFSDirectory, SimpleFSDirectory�����ݲ�ͬ�Ĳ���ϵͳ��ʹ�ó������в�ͬ��ѡ���ˡ�
 * Directory index = new RAMDirectory();
 * 
 * <pre>
 * 
 * <pre>//IndexWriterConfig���������д���IndexWriter�����ã�һ��IndexWriter������ɺ󣬴�ʱ��ȥ�޸�IndexWriterConfig�ǲ���Ӱ�쵽IndexWriterʵ���ģ���ʱ������ȡ��ȷ��IndexWirter�����ã������ͨ��IndexWirter.getConfig()�����ˣ�����IndexWriterConfig����Ҳ��һ��final�ࡣ
 * 
 * <pre>
 * IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44, analyzer);
 * 
 * // ����˼��,IndexWriter������ά��������������
 * IndexWriter w = new IndexWriter(index, config);
 * 
 * <pre>
 * /ͨ����ѯ������QueryParser����һ����ѯQuery.  
 * //QueryParser��JavaCC(http://javacc.java.net)�������������Ҫ�ķ�������QueryParserBase.parse(String)��
 * //�ر���Ҫע�����QueryParser�����̰߳�ȫ��
 * 
 * <pre>
 * Query q = new QueryParser(Version.LUCENE_44, "title", analyzer).parse(querystr);
 * 
 * //�����ʾÿ�������ʾ�Ľ����  
 * int hitsPerPage = 10;  
 * //����������ȡ��  
 * IndexReader reader = IndexReader.open(index);  
 * //����������ѯ��  
 * IndexSearcher searcher = new IndexSearcher(reader);  
 * //��TopDocs�ķ�ʽ�������hitsPerPage�Ĳ�ѯ���  
 * TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);  
 * //ִ�в�ѯ  
 * searcher.search(q, collector);  
 * ScoreDoc[] hits = collector.topDocs().scoreDocs;  
 * 
 * 
 * @author ghuang
 *
 * @����ʱ�� 2015��9��8��
 *
 */
public class WriteIndexTest {

	/**
	 * 1��Lucene�ĺ���jar��
	 * 
	 * lucene-core-5.2.1.jar
	 * 
	 * lucene-analyzers-common-5.2.1.jar
	 * 
	 * lucene-queryparser-5.2.1.jar
	 * 
	 * 2����Ҫ������˵��
	 * 
	 * org.apache.lucene.analysis�����Է���������Ҫ���ڷִ�
	 * 
	 * org.apache.lucene.document�������ĵ��Ĺ���
	 * 
	 * org.apache.lucene.index����������������ɾ����
	 * 
	 * org.apache.lucene.queryparser����ѯ����
	 * 
	 * org.apache.lucene.search����������
	 * 
	 * org.apache.lucene.store�����ݴ洢����
	 * 
	 * org.apache.lucene.util�����߰�
	 * 
	 * 3��д�����������ĺ�����
	 * 
	 * Directory�����������ĵ��Ĵ洢λ�ã�����һ����������FSDirectory��RAMDirectory������Ҫ���ࡣǰ�߽�����д���ļ�ϵͳ��
	 * ���߽������ĵ�д���ڴ档
	 * 
	 * Analyzer����������ʱʹ�õķ���������Ҫ������StandardAnalyzer��һ������һ���ʣ���
	 * �������ɵ������ṩ�翪Դ�����ṩһЩ���ķִ�����
	 * 
	 * IndexWriterConfig�������������������Ϣ
	 * 
	 * IndexWriter�����������ĺ����࣬������������������ɾ���ģ�
	 * 
	 * Document������һ�������ĵ�
	 * 
	 * Field�����������ĵ��д洢�����ݣ��°汾��Lucene������ϸ�������˶�����ࣺIntField��LongField��FloatField��
	 * DoubleField��TextField��StringField�ȡ�
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartLuenceWithWriteIndex() throws Exception {
		/* ��׼�ִ�����һ������һ������ */
		Analyzer a = new StandardAnalyzer();
		/* Ӳ�̴洢 */
		Directory dir = FSDirectory.open(Paths.get("./index"));
		/* �������� */
		IndexWriterConfig iwc = new IndexWriterConfig(a);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		/* д������ */
		IndexWriter iw = new IndexWriter(dir, iwc);
		/* �����ĵ� */
		Document doc = new Document();
		doc.add(new TextField("info", "�����õ�������������������ע��Ŷ", Field.Store.YES));
//		doc.add(new StringField("lucene", "lucene lucene lucene", Field.Store.YES));
		/* �����ĵ����������ڷ����ѯ */
		iw.addDocument(doc);
		iw.close();
		dir.close();

	}

	/**
	 * IndexReader����ȡ�����Ĺ����࣬����������DirectoryReader
	 * 
	 * IndexSearch����ѯ�����ĺ�����
	 * 
	 * QueryParser����ѯ����������ʾ����������ĸ�������
	 * 
	 * Query������һ�β�ѯ
	 * 
	 * TopDocs����װ��ƥ�����������ƥ����ٸ�
	 * 
	 * ScoreDoc��ƥ������ݣ������װ�������ĵ��ĵ÷ֺ�����ID
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
		Query query = parser.parse("����");
		TopDocs topDocs = is.search(query, 1000);
		System.out.println("�ܹ�ƥ����ٸ���" + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		// Ӧ����topDocs.totalHits��ͬ
		System.out.println("���������ݣ�" + hits.length);
		for (ScoreDoc scoreDoc : hits) {
			System.out.println("ƥ��÷֣�" + scoreDoc.score);
			System.out.println("�ĵ�����ID��" + scoreDoc.doc);
			Document document = is.doc(scoreDoc.doc);
			System.out.println(document.get("info"));
//			System.out.println(document.get("lucene"));
		}
		reader.close();
		dir.close();
	}

}
