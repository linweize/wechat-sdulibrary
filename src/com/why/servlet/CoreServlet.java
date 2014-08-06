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

	private static final String ERROR_MESSAGE = "ָ���ʽ���󣬻ظ�\"help\"�鿴��ȷָ���ʽ��";

	@Override
	public org.easywechat.msg.BaseMsg handleTextMsg(String content) {

		String respContent;
		if (content.equalsIgnoreCase("help")) {
			respContent = "������ָ���ʽ��\n1��һ�����裺xj#У԰���˺ţ�����\n2����ǰ���ģ�jy#У԰���˺ţ�����\n"
					+ "3���鼮��ѯ��\n\tcst#��������������\n\tcsa#���ߣ�����������\n\tcsp#�����磨������������\n4��ͼ��ݶ�̬��ѯ��\n\t"
					+ "������Ϣ��dt#gg\n\t��Դ��̬��dt#zy\n\t������Ϣ��dt#jz\n��ʾ��У԰���˺�6λ��!";
			return new TextMsg(respContent);
		} else {

			String[] cmds = content.split("#", 2);
			if (cmds.length != 2) {
				return new TextMsg(ERROR_MESSAGE);
			}
			String cmd = cmds[0].toLowerCase();// #֮ǰ��
			String param = cmds[1];// #֮���

			if (cmd.equals("dt")) {
				// parmsΪ��gg���棩����zy��Դ������jz������
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
			// ��������ı���"xj"��ͷ,��ʾ�����衯���� xj#219814,123456
			else if (cmd.equals("xj")) {
				String[] params = param.split("[,��]");
				if (params.length != 2) {
					return new TextMsg(ERROR_MESSAGE);
				}
				String sid = params[0];
				String pwd = params[1];
				try {
					respContent = new ReBorrow(sid, pwd).renewAll();
				} catch (Exception e) {
					respContent = "����ʧ��,���������ʽ����������!";
				}
			}
			// ��������ı���"jy#"��ͷ����ʾ���鿴��ǰ���ġ�����jy#219651��252519
			else if (cmd.equals("jy")) {
				String[] params = param.split("[,��]");
				// ��ʽ����
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
					respContent = "����ʧ��,���������ʽ����������!";
				}
			} else if (cmd.startsWith("cs")) {

				NewsMsg newsMessage;
				// ����������
				if (cmd.equals("cst") || cmd.equals("cs")) {
					newsMessage = SearchBooks.getInfo(param,
							SearchBooks.TYPE_TITLE);
				}// �����߲���
				else if (cmd.equals("csa")) {
					newsMessage = SearchBooks.getInfo(param,
							SearchBooks.TYPE_AUTHER);
				}// �����������
				else if (cmd.equals("csp")) {
					newsMessage = SearchBooks.getInfo(param,
							SearchBooks.TYPE_PUBLISHER);
				} else {
					return new TextMsg(ERROR_MESSAGE);
				}
				return newsMessage;

			}
			return new TextMsg("ָ���ʽ���󣬻ظ�\"help\"�鿴��ȷָ���ʽ��");

			// respContent = "�����͵����ı���Ϣ��";
		}

	};

	@Override
	protected String getToken() {
		return "out";
	}

}
