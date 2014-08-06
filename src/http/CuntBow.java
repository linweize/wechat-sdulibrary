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
	String userNumber;// У԰���˺�
	String pwd;// ͼ���ϵͳ����

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

	/** ��¼ͼ��� */
	public boolean login() throws Exception {

		// ���ʵõ���֤���ҳ��(cookieҲ�Ǵ����ҳ��õ�)
		HttpGet httpGet = new HttpGet(loginCodeUrl);
		HttpResponse response = httpClient.execute(httpGet);

		// ��ֵcookie
		Header cookie = response.getFirstHeader("Set-Cookie");
		this.cookieValue = cookie.getValue();

		// �õ���֤��ͼƬ�����ó���ʶ�����
		byte[] byteImg = EntityUtils.toByteArray(response.getEntity());
		ByteArrayInputStream in = new ByteArrayInputStream(byteImg);
		BufferedImage image = ImageIO.read(in);
		String code = YZM.getTextFromYZM(image);

		String finalLoginUrl = loginUrl + "?number=" + userNumber + "&passwd="
				+ pwd + "&code=" + code + "&returnUrl=";
		httpGet = new HttpGet(finalLoginUrl);
		// ��¼
		response = httpClient.execute(httpGet);
		String page = EntityUtils.toString(response.getEntity(), "utf-8");
		if (page.indexOf("mylib_content") != -1) {
			return true;
		} else if (page.indexOf("��֤�����") != -1) {
			System.out.println("��֤�����");
		}
		return false;
	}

	List<Book> books = new ArrayList<Book>();
	/**
	 * �������п����������
	 */
	public NewsMsg look() throws Exception {
		
		List<Article> articles = new ArrayList<Article>();

		//��ε�½����ֹ��֤��ʶ�����
		for (int i = 0; i < 5; i++) {
			if (login()) {
				break;
			}
		}

		// �鿴�ѽ��鼮
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
//		String LastlinkHref = host + linkHref;//���˻�÷����鼮������Ϣ������
//		System.out.println(LastlinkHref);
		
		Article articleb = new Article();
		articleb.setTitle("����ǰ�����ˣ�");
		articles.add(articleb);
		
		//�õ�����������
		for (Element eleBook : elements) {
			//�������
			String linkHref = elements.attr("href");
			String RightHref = linkHref.substring(2);
			String LastlinkHref = host + RightHref;
			
//			System.out.print(LastlinkHref+"  ");
			//�������
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


