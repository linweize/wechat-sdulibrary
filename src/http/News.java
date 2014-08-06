package http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.easywechat.msg.Article;
import org.easywechat.msg.NewsMsg;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class News {
	public News(){
		
	}
	
	public static NewsMsg LibNews(int i){
		
		List<Article> articles = new ArrayList<Article>();
		String moreurl = "http://www.lib.sdu.edu.cn/portal/tpl/home/newslist?type=1";
		
		Document doc;
		try {
			doc = Jsoup.connect("http://www.lib.sdu.edu.cn/portal/tpl/home/index").timeout(0).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Elements eles=doc.select(".news_con_b");
		Element elet = eles.get(i);
//		System.out.println(elet);
		Elements elements = elet.select("li");
//		System.out.println(elements);
		
		String purl = "http://SduLibrary.jd-app.com/pic/sdu.jpg";
		Article articlep = new Article();
		articlep.setPicUrl(purl);
		articlep.setTitle("图书馆动态");
		articlep.setUrl(moreurl);
		articles.add(articlep);
		
//		Article articlet = new Article();
//		articlet.setTitle("图书馆动态：");
////		articlet.setUrl(finurl);
//		articles.add(articlet);
		
		for(Element element : elements){
			String date = element.getElementsByTag("span").get(0).text();
//			System.out.println(date);
			
			String href=element.getElementsByTag("a").get(0).attr("href");
//			System.out.println(href);
			String finalhref = "http://www.lib.sdu.edu.cn/portal/tpl/home/" + href;
//			System.out.println(finalhref);
			
			String news = element.getElementsByTag("a").get(0).text();
//			System.out.println(news);
			
//			System.out.println(news+"  "+date+"  "+finalhref);
			
			Article article = new Article();
			article.setTitle(date + " " + news);
			article.setUrl(finalhref);
			articles.add(article);
		}
		
		
		Article article = new Article();
		article.setTitle("更多信息-->请点击此处");
		article.setUrl(moreurl);
		articles.add(article);
		
		NewsMsg message = new NewsMsg();
		message.setArticles(articles);

		return message;
	}
	
	
	public static void main(String args[]){
//		System.out.println(doc);
		String str = "gg";
		News news = new News();
			news.LibNews(0);
		
	}
	
	
	
}
