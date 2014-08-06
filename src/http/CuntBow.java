package http;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import model.Book;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.easywechat.msg.Article;
import org.easywechat.msg.NewsMsg;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.room325.yzm.YZM;

public class CuntBow {
	HttpClient httpClient;
	String userNumber;// 校园卡账号
	String pwd;// 图书馆系统密码

	String cookieValue = "";

	final static String host = "http://58.194.172.34";
	final static String loginCodeUrl = host + "/reader/yz.php";
	final static String bookListUrl = host + "/reader/book_lst.php";
	final static String loginUrl = host + "/reader/redr_verify.php";
	final static String renewCodeUrl = host + "/reader/captcha.php";
	final static String renewUrl = host + "/reader/ajax_renew.php";

	public CuntBow(String userNumber, String pwd) {
		this.userNumber = userNumber;
		this.pwd = pwd;
		httpClient = HttpClients.createDefault();
	}

	/** 登录图书馆 */
	public boolean login() throws Exception {

		// 访问得到验证码的页面(cookie也是从这个页面得到)
		HttpGet httpGet = new HttpGet(loginCodeUrl);
		HttpResponse response = httpClient.execute(httpGet);

		// 设值cookie
		Header cookie = response.getFirstHeader("Set-Cookie");
		this.cookieValue = cookie.getValue();

		// 得到验证码图片，并用程序识别出来
		byte[] byteImg = EntityUtils.toByteArray(response.getEntity());
		ByteArrayInputStream in = new ByteArrayInputStream(byteImg);
		BufferedImage image = ImageIO.read(in);
		String code = YZM.getTextFromYZM(image);

		String finalLoginUrl = loginUrl + "?number=" + userNumber + "&passwd="
				+ pwd + "&code=" + code + "&returnUrl=";
		httpGet = new HttpGet(finalLoginUrl);
		// 登录
		response = httpClient.execute(httpGet);
		String page = EntityUtils.toString(response.getEntity(), "utf-8");
		if (page.indexOf("mylib_content") != -1) {
			return true;
		} else if (page.indexOf("验证码错误") != -1) {
			System.out.println("验证码错误");
		}
		return false;
	}

	List<Book> books = new ArrayList<Book>();
	/**
	 * 续借所有可以续借的书
	 */
	public NewsMsg look() throws Exception {
		
		List<Article> articles = new ArrayList<Article>();

		//多次登陆，防止验证码识别错误
		for (int i = 0; i < 5; i++) {
			if (login()) {
				break;
			}
		}

		// 查看已借书籍
		HttpGet httpGet = new HttpGet(bookListUrl);
		HttpResponse response = httpClient.execute(httpGet);
		String page = EntityUtils.toString(response.getEntity(), "utf-8");

		Document document = Jsoup.parse(page);
//		System.out.println(document);
		
		Element tbody = document.select("tbody").first();
//		System.out.println(tbody);
//		Elements trs=tbody.getElementsByTag("tr");
//		System.out.println(trs);
		
//		for (Element ele:trs) {
//			Element td=ele.child(1);
////			System.out.println(td);
//		}
//		System.out.println(tbody);
		
		
		Elements elements = tbody.getElementsByAttribute("href");
//		System.out.println(elements);
//		Elements elems = tbody.getElementsByTag("font");
//		System.out.println(elems);
//		String linkHref = elements.attr("href");
//		System.out.println(linkHref);
//		String RightHref = linkHref.substring(2);
//		System.out.println(RightHref);
//		String LastlinkHref = host + linkHref;//到此获得访问书籍具体信息的链接
//		System.out.println(LastlinkHref);
		
		Article articleb = new Article();
		articleb.setTitle("您当前借阅了：");
		articles.add(articleb);
		
		//得到书名和链接
		for (Element eleBook : elements) {
			//输出链接
			String linkHref = elements.attr("href");
			String RightHref = linkHref.substring(2);
			String LastlinkHref = host + RightHref;
			
//			System.out.print(LastlinkHref+"  ");
			//输出书名
			String bookName = eleBook.getElementsByTag("a").get(0).text();
//			System.out.println(bookName);
			
			Article article = new Article();
			article.setTitle(bookName);
			article.setUrl(LastlinkHref);
			articles.add(article);
		}
//		return elements.toString();
		NewsMsg message = new NewsMsg();
		message.setArticles(articles);

		return message;
	}



//	public static void main(String[] args) {
//		NewsMsg deadline;
//		 try {
//		 deadline = new CuntBow("219651", "252519").look();
////		 System.out.println(deadline);
//		 } catch (Exception e) {
//		 e.printStackTrace();
//		 }
//	}
	
}


