package http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {

//	public static String getInfo2(String content) {
//		String url = "http://58.194.172.34/opac/openlink.php?strSearchType=title&match_flag=forward&historyCount=1&strText="
//				+ content
//				+ "&doctype=ALL&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&location=ALL";
//		// try {
//		// return Jsoup.connect(url).timeout(3000).get().toString();
//		//
//		// } catch (IOException e) {
//		// e.printStackTrace();
//		// }
//
//		return "llslasidjfe  content=" + content;
//	}

	public static String getInfo(String content) {
		
		String url = "http://58.194.172.34/opac/openlink.php?strSearchType=title&match_flag=forward&historyCount=1&strText="
				+ content
				+ "&doctype=ALL&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&location=ALL";

		List<Book> books = new ArrayList<Book>();

		try {
			Document document = Jsoup.connect(url).timeout(0).get();

//			System.out.println(document);
			
			Element element = document.getElementById("search_book_list");
//			System.out.println(element);
			Elements eleBooks = element.children();
//			System.out.println(eleBooks);

			for (Element eleBook : eleBooks) {
				Book book = new Book();
				
				String address="";
				StringBuilder sb=new StringBuilder();
				
				List<String> addrs=new ArrayList<String>();
				//�ڶ�������
				String href=eleBook.getElementsByTag("a").get(0).attr("href");
//				System.out.println(href);
				String link="http://58.194.172.34/opac/"+href;
				
				//�õ�ÿ�������ϸ��Ϣ
//				Document bookInfoDoc=Jsoup.connect(link).get();
				
//				if (bookInfoDoc.toString().indexOf("���鿯�������ڶ����л��ߴ�����")!=-1) {
//					address="���鿯�������ڶ����л��ߴ�����";
//				}else{
//					Elements trs=bookInfoDoc.getElementsByClass("whitetext");
//					for (Element tr : trs) {
//						String s=tr.child(3).text();
//						if (!addrs.contains(s)) {
//							addrs.add(s);
//						}
//					}
//					for (String addr : addrs) {
//						sb.append(addr).append("��");
//					}
//					if (sb.length()!=0) {
//						sb.deleteCharAt(sb.lastIndexOf("��"));
//					}
//					address=sb.toString();
//					book.setAddress(address);
//				}
				
//				System.out.println("link="+link);
				String bookName = eleBook.getElementsByTag("a").get(0).text();
//				System.out.println(bookName);
				
				//�ݲظ������ɽ踱��
				String availableCount = eleBook.getElementsByTag("span").get(1)
						.text();
//				System.out.println(availableCount);
				//������
				String raw = eleBook.getElementsByTag("p").get(0).toString();
//				System.out.println(raw);
				String s1 = raw.split("<br />")[2];
//				System.out.println(s1);
				String publisher = s1.split("&nbsp;")[0];
//				System.out.println(publisher);
				String year = s1.split("&nbsp;")[1];
//				System.out.println(year);
				
				book.setBookName(bookName);
				book.setAvailableCount(availableCount);
				book.setPublisher(publisher);
				book.setYear(year);

				books.add(book);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "ͼ���δ��¼!";
		}

		StringBuilder sb = new StringBuilder();

		// �жϵõ������Ŀ
		if (books.size() == 0) {
			return "ͼ���δ��¼";
		} else if (books.size() < 10) {//�����ĿС��10��
			for (Book book : books) {
				sb.append(book.getBookName()).append(" ")
						.append(book.getPublisher()).append(" ")
						.append(book.getYear()).append("\n")
						.append(book.getAvailableCount()).append("\n\n");
			}

		} else {//�����Ŀ����10��

			for (int i = 0; i < 10; i++) {
				sb.append(books.get(i).getBookName()).append(" ")
						.append(books.get(i).getPublisher()).append(" ")
						.append(books.get(i).getYear()).append("\n")
						.append(books.get(i).getAvailableCount()).append("\n")
						.append(books.get(i).getAddress())
						.append("\n\n");
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
//		long l1 = System.currentTimeMillis();
		String sss = Test.getInfo("java");
		System.out.println(sss);
//		long l2 = System.currentTimeMillis();
//		System.out.println("time=" + (l2 - l1));
	}

}
