package com.why.servlet;

import http.CuntBow;
import http.News;
import http.ReBorrow;
import http.SearchBooks;

import java.util.Date;

import org.easywechat.msg.NewsMsg;
import org.easywechat.msg.TextMsg;
import org.easywechat.servlet.WexinServlet;
import org.easywechat.util.MessageUtil;

public class CoreServlet extends WexinServlet {
	private static final long serialVersionUID = 4440739483644821986L;

	private static final String ERROR_MESSAGE = "指令格式错误，回复\"help\"查看正确指令格式！";

	@Override
	public org.easywechat.msg.BaseMsg handleTextMsg(String content) {

		String respContent;
		if (content.equalsIgnoreCase("help")) {
			respContent = "以下是指令格式：\n1、一键续借：xj#校园卡账号，密码\n2、当前借阅：jy#校园卡账号，密码\n"
					+ "3、书籍查询：\n\tcst#题名（按题名）\n\tcsa#作者（按作者名）\n\tcsp#出版社（按出版社名）\n4、图书馆动态查询：\n\t"
					+ "公告信息：dt#gg\n\t资源动态：dt#zy\n\t讲座信息：dt#jz\n提示：校园卡账号6位数!";
			return new TextMsg(respContent);
		} else {

			String[] cmds = content.split("#", 2);
			if (cmds.length != 2) {
				return new TextMsg(ERROR_MESSAGE);
			}
			String cmd = cmds[0].toLowerCase();// #之前的
			String param = cmds[1];// #之后的

			if (cmd.equals("dt")) {
				// parms为（gg公告）、（zy资源）、（jz讲座）
				NewsMsg newsMessage;
				int i = 0;
				if (param.equalsIgnoreCase("gg")) {
					i = 0;
					newsMessage = News.LibNews(i);
				} else if (param.equalsIgnoreCase("zy")) {
					i = 1;
					newsMessage = News.LibNews(i);
				} else if (param.equalsIgnoreCase("jz")) {
					i = 2;
					newsMessage = News.LibNews(i);
				} else {
					return new TextMsg(ERROR_MESSAGE);
				}

				return newsMessage;

			}
			// 如果请求文本以"xj"开头,表示‘续借’，如 xj#219814,123456
			else if (cmd.equals("xj")) {
				String[] params = param.split("[,，]");
				if (params.length != 2) {
					return new TextMsg(ERROR_MESSAGE);
				}
				String sid = params[0];
				String pwd = params[1];
				try {
					respContent = new ReBorrow(sid, pwd).renewAll();
				} catch (Exception e) {
					respContent = "操作失败,请检查输入格式，重新输入!";
				}
			}
			// 如果请求文本以"jy#"开头，表示‘查看当前借阅’，如jy#219651，252519
			else if (cmd.equals("jy")) {
				String[] params = param.split("[,，]");
				// 格式错误
				if (params.length != 2) {
					return new TextMsg(ERROR_MESSAGE);
				}
				String sid = params[0];
				String pwd = params[1];
				try {
					NewsMsg newsMessage;
					newsMessage = new CuntBow(sid, pwd).look();
					return newsMessage;

				} catch (Exception e) {
					respContent = "操作失败,请检查输入格式，重新输入!";
				}
			} else if (cmd.startsWith("cs")) {

				NewsMsg newsMessage;
				// 按题名查书
				if (cmd.equals("cst") || cmd.equals("cs")) {
					newsMessage = SearchBooks.getInfo(param,
							SearchBooks.TYPE_TITLE);
				}// 按作者查书
				else if (cmd.equals("csa")) {
					newsMessage = SearchBooks.getInfo(param,
							SearchBooks.TYPE_AUTHER);
				}// 按出版社查书
				else if (cmd.equals("csp")) {
					newsMessage = SearchBooks.getInfo(param,
							SearchBooks.TYPE_PUBLISHER);
				} else {
					return new TextMsg(ERROR_MESSAGE);
				}
				return newsMessage;

			}
			return new TextMsg("指令格式错误，回复\"help\"查看正确指令格式！");

			// respContent = "您发送的是文本消息！";
		}

	};

	@Override
	protected String getToken() {
		return "out";
	}

}
