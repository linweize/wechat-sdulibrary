package com.why.service;

import java.awt.image.BufferedImage;
import java.beans.FeatureDescriptor;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.room325.yzm.YZM;
import org.room325.yzm.YZM2;

public class SduLibrary {

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

	public SduLibrary(String userNumber, String pwd) {
		this.userNumber = userNumber;
		this.pwd = pwd;
		httpClient = HttpClients.createDefault();
//		System.out.println(httpClient);
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

	/**
	 * 续借所有可以续借的书
	 */
	public String renewAll() throws Exception {

		for (int i = 0; i < 5; i++) {
			if (login()) {
				break;
			}
		}

		// 查看已借书籍
		HttpGet httpGet = new HttpGet(bookListUrl);
		HttpResponse response = httpClient.execute(httpGet);
		String page = EntityUtils.toString(response.getEntity(), "utf-8");

		//得到验证码图片，并用程序识别出来
		httpGet = new HttpGet(renewCodeUrl);
		response = httpClient.execute(httpGet);
		byte[] byteImg = EntityUtils.toByteArray(response.getEntity());

		// File outFile = new File("G:\\temp\\temp.png");
		// OutputStream os = new FileOutputStream(outFile);
		// os.write(byteImg);
		// os.close();

		ByteArrayInputStream in = new ByteArrayInputStream(byteImg);
		BufferedImage image = ImageIO.read(in);
		String code = YZM2.getCode(image);
		// System.out.println("code=" + code);
		// 解析已借书籍，得到 bar_code(书的编号)和check(??)
		Document document = Jsoup.parse(page);
//		System.out.println(document);
		Elements elements = document.getElementsByAttribute("onclick");
		System.out.println(elements);

		int y = 0, n = 0;
		// 针对每本书，续借之
		for (Element element : elements) {
			String barCode = element.attr("onclick").split("'")[1];
//			System.out.println(barCode);
			String check = element.attr("onclick").split("'")[3];
//			System.out.println(check);
			System.out.println("barCode=" + barCode + ",check=" + check);
			String finalUrl = renewUrl + "?bar_code=" + barCode + "&check="
					+ check + "&captcha=" + code + "&time="
					+ System.currentTimeMillis();

			HttpGet get = new HttpGet(finalUrl);
			get.setHeader("X-Requested-With", "XMLHttpRequest");
			get.setHeader("cookie", cookieValue);
			HttpResponse httpResponse = httpClient.execute(get);
			String answer = EntityUtils.toString(httpResponse.getEntity(),
					"utf-8");
			if (answer.indexOf("续借成功") != -1) {
				// 续借成功
				y++;
			} else {
				n++;
			}
		}

		// 再次查看已借书籍
		HttpGet httpGet2 = new HttpGet(bookListUrl);
		HttpResponse response2 = httpClient.execute(httpGet2);
		String page2 = EntityUtils.toString(response2.getEntity(), "utf-8");

		// 得到最近的到期时间
		String deadline = page2.substring(page2.indexOf("color=>"));
		deadline = deadline.substring(7, deadline.indexOf(' '));

		StringBuilder sb = new StringBuilder();
		sb.append("续借完成，共有").append(y).append("本书续借成功。\n").append(n)
				.append("本书达到最大续借次数，无法续借。\n");

		sb.append(analyzeDate(deadline));
		
		return sb.toString();
	}

	protected static String analyzeDate(String deadline) throws Exception {
		if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new Exception();
		}
		String[] date = deadline.split("-");
		int dy = Integer.parseInt(date[0]);
		int dm = Integer.parseInt(date[1]);
		int dd = Integer.parseInt(date[2]);

		Calendar calendar = Calendar.getInstance();
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH) + 1;
		int d = calendar.get(Calendar.DAY_OF_MONTH);

		if (dy < y || (dy == y && dm < m)||(dy==y&&dm==m&&dd<d)) {
			//有书已经超期了
			return "有书已于"+deadline+"到期，请尽快归还。";
		}
		
		return "别忘了"+deadline+"之前去还书哦！";

	}

	public static void main(String[] args) {
		 String deadline;
		 try {
		 deadline = new SduLibrary("219651", "252519").renewAll();
		
		 System.out.println(deadline);
		 } catch (Exception e) {
		 e.printStackTrace();
		 }

	}
}
