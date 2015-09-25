/**
 * 
 */
package com.hgframework.search.crawler;

import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author ghuang
 *
 * @创建时间 2015年9月20日
 *
 */
public class JsoupCrawlerTest {

	private static final String userAgent = "Mozilla/5.0 (jsoup)";
	private static final int timeout = 5 * 1000;

	private static final String url = "http://www.douguo.com/caipu/%E5%AE%B6%E5%B8%B8%E8%8F%9C/30";

	private Document doc;

	@Before
	public void init() throws Exception {
		doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();
	}

	@Test
	public void testCrawlerData() {
		// Elements links = this.doc.select("a[href]");
		// Iterator it=links.iterator();
		// while(it.hasNext()){
		// System.out.println(it.next());
		// }
		/*
		 * 也爬单个的菜谱，然后根据菜谱连接找相应的数据 多个class类未必会有数据结果 例如：course r5 col
		 * masonry-brick 就没有数据结果
		 */
		Elements recipes = this.doc.select("div.course");
		// Iterator<Element> iterator=recipes.iterator();
		for (Element e : recipes) {
//			Elements es = e.select("div.recpio");
//			for (Element element : es) {
//				System.out.println(element.html());
//			}
			Elements esurls= e.select("h3.mts");
			for (Element element : esurls) {
				Elements links=element.select("a[href]");
				for (Element e2 : links) {
					String url=e2.attr("href");
					System.out.println(url);
					System.out.println(e2.ownText());
				}
//				System.out.println(links);
			}

//			System.out.println(e);
			// for (DataNode dn : e.dataNodes()) {
			// System.out.println(dn);
			// }
		}

	}

}
