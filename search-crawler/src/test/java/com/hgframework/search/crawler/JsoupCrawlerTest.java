/**
 * 
 */
package com.hgframework.search.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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

	private Document root;

	private BlockingQueue<String> detailUrls = new LinkedBlockingQueue<String>();

	private ExecutorService es = Executors.newFixedThreadPool(3);

	@Before
	public void init() throws Exception {
		root = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();
		 this.initCrawlerData();
	}

	@Test
	public void testCrawlerDetail() throws Exception {
		String url = null;
		while ((url = this.detailUrls.take()) != null) {
			es.execute(new CrawlerTask(url));
		}
	}

	private class CrawlerTask implements Runnable {

		private String url;

		public CrawlerTask(String url) {
			super();
			this.url = url;
		}

		public void run() {
			try {
				Document detailDoc = Jsoup.connect(url).userAgent(userAgent)
						.timeout(timeout).get();
				Elements tables = detailDoc.select("table.retamr>tbody>tr");
				for (Element element : tables) {
					System.out.println(element);
				}
				// Elements es=detailDoc.select("div.xtip");
				// for (Element element : es) {
				// System.out.println(element.ownText());
				// }

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testCrawlerData() {
		
		/*
		 * 也爬单个的菜谱，然后根据菜谱连接找相应的数据 多个class类未必会有数据结果 例如：course r5 col
		 * masonry-brick 就没有数据结果
		 */
		Elements recipes = this.root.select("div.course");
		for (Element e : recipes) {

			Elements esurls = e.select("h3.mts");
			Elements desc = e.select("p.descri");
			System.out.println(desc);
			for (Element element : esurls) {
				Elements links = element.select("a[href]");
				for (Element e2 : links) {
					String url = e2.attr("href");
					System.out.println(url);
					System.out.println(e2.ownText());
				}
			}


		}

	}

	public void initCrawlerData() {
		/*
		 * 也爬单个的菜谱，然后根据菜谱连接找相应的数据 多个class类未必会有数据结果 例如：course r5 col
		 * masonry-brick 就没有数据结果
		 */
		Elements recipes = this.root.select("div.course");
		for (Element e : recipes) {
			Elements esurls = e.select("h3.mts");
//			Elements desc = e.select("p.descri");
//			System.out.println(desc.get(0).ownText());

			for (Element element : esurls) {
				Elements links = element.select("a[href]");
				for (Element e2 : links) {
					String url = e2.attr("href");
					this.detailUrls.offer(url);
//					System.out.println(url);
//					System.out.println(e2.ownText());
				}

			}

		}

	}

}
