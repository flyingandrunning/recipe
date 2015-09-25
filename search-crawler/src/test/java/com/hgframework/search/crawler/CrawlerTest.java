/**
 * 
 */
package com.hgframework.search.crawler;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;
import org.junit.Test;

/**
 * 
 * @author ghuang
 *
 * @创建时间 2015年9月8日
 *
 */
public class CrawlerTest {

	private void parseFromString(String content) throws Exception {
		Parser parser = new Parser(content);
		HasAttributeFilter filter = new HasAttributeFilter("id","page_cm_id");
//		TableTag
		

		try {
			NodeList list = parser.parse(filter);
			int count = list.size();

			// process every link on this page
			for (int i = 0; i < count; i++) {
				Node node = list.elementAt(i);
				if(node instanceof TagNode){
					TagNode tagNode=(TagNode)node;
					System.out.println(tagNode.toPlainTextString());
				}
				
				

//				if (node instanceof LinkTag) {
//					LinkTag link = (LinkTag) node;
//					System.out.println(link.getLink());
////					String nextlink = link.extractLink();
////					String mainurl = "http://johnhany.net/";
////					String wpurl = mainurl + "wp-content/";
//				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHttpClientWithLink() throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			HttpGet httpget = new HttpGet("http://www.douguo.com/cookbook/1247465.html");
			System.out.println("executing request " + httpget.getURI());
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}
			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			this.parseFromString(responseBody);

			// print the content of the page
			// System.out.println("----------------------------------------");
			// System.out.println(responseBody);
			// System.out.println("----------------------------------------");

			// parsePage.parseFromString(responseBody,conn);

		} finally {
			httpclient.close();
		}

	}

}
