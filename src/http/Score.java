package http;

import java.util.ArrayList;
import java.util.List;

import org.easywechat.msg.Article;
import org.easywechat.msg.NewsMsg;

public class Score {
	
	public static NewsMsg Myscore(String stunum){
		
		String mynum = stunum;
		String url1 = "http://jwxt.sdu.edu.cn:7890/pls/wwwbks/qcb.table_browse?ctable=CJ_LRCJB&ntable_type=2&ccolumns=*&cclauses=+where+xh%3D%27";
		String url2 = "%27&nrow_min=1&nrow_max=100";
		String finurl = url1+mynum+url2;
		
		List<Article> articles = new ArrayList<Article>();
		String purl = "http://SduLibrary.jd-app.com/pic/sd.jpg";
		Article article = new Article();
		article.setPicUrl(purl);
		article.setTitle("点击查看成绩");
		article.setUrl(finurl);
		articles.add(article);
		
		NewsMsg message = new NewsMsg();
		message.setArticles(articles);

		return message;
	}

}
