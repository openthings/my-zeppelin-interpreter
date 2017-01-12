package org.apache.zeppelin.echarts.command.reader;

import org.apache.zeppelin.echarts.utils.PropertyGetter;
import org.apache.zeppelin.interpreter.InterpreterContext;

/**
 * Created by Ethan Xiao on 2017/1/12.
 * HTML读取执行器
 */
public class HTMLReader extends Reader<String, String> {
	/**
	 * 命令后紧跟的HTML并作为输出
	 */
	private String html;

	public void setParameters(String[] parameters) {
		//没有参数
	}

	public void setBody(String body) {
		this.html = body;
	}

	public String execute(String input, PropertyGetter propertyGetter, InterpreterContext interpreterContext) {
		return html;
	}
}
