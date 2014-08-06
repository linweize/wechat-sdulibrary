package http;

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

public class ReBorrow {

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

	public ReBorrow(String userNumber, String pwd) {
		this.userNumber = userNumber;
		this.pwd = pwd;
		httpClient = HttpClients.createDefault();
//		System.out.println(httpClient);
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

	/**
	 * �������п����������
	 */
	public String renewAll() throws Exception {

		for (int i = 0; i < 5; i++) {
			if (login()) {
				break;
			}
		}

//		// �鿴�ѽ��鼮
		HttpGet httpGet = new HttpGet(bookListUrl);
		HttpResponse response = httpClient.execute(httpGet);
		String page = EntityUtils.toString(response.getEntity(), "utf-8");
//		System.out.println(page);
		
		//�õ���֤��ͼƬ�����ó���ʶ�����
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
		// �����ѽ��鼮���õ� bar_code(��ı��)��check(??)
		Document document = Jsoup.parse(page);
//		System.out.println(document);
		Elements elets=document.getElementsByTag("font");
//		System.out.println(elets);
		
		
		Elements elements = document.getElementsByAttribute("onclick");
//		System.out.println(elements);

		int x = 0,  y= 0, z=0, t=0;
		// ���ÿ���飬����֮
		for (Element element : elements) {
			String barCode = element.attr("onclick").split("'")[1];
//			System.out.println(barCode);
			String check = element.attr("onclick").split("'")[3];
//			System.out.println(check);
//			System.out.println("barCode=" + barCode + ",check=" + check);
			String finalUrl = renewUrl + "?bar_code=" + barCode + "&check="
					+ check + "&captcha=" + code + "&time="
					+ System.currentTimeMillis();
//			System.out.println(finalUrl);
			
			HttpGet get = new HttpGet(finalUrl);
//			System.out.println(get);
			get.setHeader("X-Requested-With", "XMLHttpRequest");
			get.setHeader("cookie", cookieValue);
			HttpResponse httpResponse = httpClient.execute(get);
//			System.out.println(httpResponse);
			String answer = EntityUtils.toString(httpResponse.getEntity(),
					"utf-8");
//			System.out.println(answer);
//			System.out.println(answer.indexOf("����ɹ�"));
			if (answer.indexOf("����ɹ�") != -1) {
				// ����ɹ�
				x++;
			} 
			else if(answer.indexOf("��������ʱ�䣬�������裡") != -1) {
				y++;
			}else if(answer.indexOf("�����������������������裡") != -1){
				z++;
			}
			t=y+x+z;
		}

		// �ٴβ鿴�ѽ��鼮
		HttpGet httpGet2 = new HttpGet(bookListUrl);
		HttpResponse response2 = httpClient.execute(httpGet2);
		String page2 = EntityUtils.toString(response2.getEntity(), "utf-8");
//		System.out.println(page2);
		
		// �õ�����ĵ���ʱ��
		String deadline = page2.substring(page2.indexOf("color=>"));
		deadline = deadline.substring(7, deadline.indexOf(' '));
//		System.out.println(deadline);

		StringBuilder sb = new StringBuilder();
		sb.append("�����ɹ�����һ������").append(t).append("���飡\n����")
		        .append(z).append("���鳬���������������������裡\n")
		        .append(y).append("���鲻������ʱ�䣬�޷����裡\n��")
		        .append(x).append("��������ɹ���\n");

		sb.append(analyzeDate(deadline));
		
		return sb.toString();
	}
	//����ʱ��
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
			//�����Ѿ�������
			return "��������"+deadline+"���ڣ��뾡��黹��";
		}
		
		return "������"+deadline+"֮ǰȥ����Ŷ��";

	}

	public static void main(String[] args) {
		 String deadline;
		 try {
		 deadline = new ReBorrow("219651", "252519").renewAll();
		
//		 System.out.println(deadline);
		 } catch (Exception e) {
		 e.printStackTrace();
		 }

	}
}