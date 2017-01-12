package org.apache.zeppelin.echarts.command;

import org.apache.zeppelin.echarts.command.processor.Processor;
import org.apache.zeppelin.echarts.command.reader.*;
import org.apache.zeppelin.echarts.command.writer.*;

import java.util.Arrays;

/**
 * Created by Ethan Xiao on 2017/1/12.
 * 构建命令执行序列
 */
public class CommandBuilder {

	private static class CommandBuilderHolder {
		private static CommandBuilder builder = new CommandBuilder();
	}

	public static CommandBuilder getInstance() {
		return CommandBuilderHolder.builder;
	}

	/**
	命令类型
	 */
	enum CommandType {
		in,     //输入命令
		process,    //处理命令
		out,    //输出命令
		;

		public Reader getReader(String type) throws InstantiationException, IllegalAccessException {
			CommandInputType inputType = CommandInputType.valueOf(type);
			return inputType.newReader();
		}

		public Writer getWriter(String type) throws InstantiationException, IllegalAccessException {
			CommandOutputType outputType = CommandOutputType.valueOf(type);
			return outputType.newWriter();
		}

		public Processor getProcessor(String type) throws InstantiationException, IllegalAccessException {
			CommandProcessType processType = CommandProcessType.valueOf(type);
			return processType.newProcessor();
		}
	}

	/**
	输入命令名称
	 */
	enum CommandInputType {
		html(HTMLReader.class),   //HTML
		text(TextReader.class),   //文本
		http(HttpReader.class),   //http请求
		intpr(InterpreterReader.class),  //已配置的其他interpreter,调用指定的解释器
		json(JsonReader.class),   //JSON格式数据,将输入JSON数据理解为需要展现的数据
		;

		private Class<? extends Reader> cls;

		private CommandInputType(Class<? extends Reader> cls) {
			this.cls = cls;
		}

		public Reader newReader() throws IllegalAccessException, InstantiationException {
			return cls.newInstance();
		}
	}

	/**
	输出命令名称
	 */
	enum CommandOutputType {
		angular(AngularWriter.class),
		echarts(EChartsWriter.class),    //将内容以echarts图表输出
		html(HTMLWriter.class),   //以HTML类型输出
		image(ImageWriter.class),
		svg(SVGWriter.class),
		table(TableWriter.class),
		text(TextWriter.class),   //将内容用输出为文本
		;

		private Class<? extends Writer> cls;

		CommandOutputType(Class<? extends Writer> cls) {
			this.cls = cls;
		}

		public Writer newWriter() throws IllegalAccessException, InstantiationException {
			return cls.newInstance();
		}
	}

	/**
	处理命令名称
	 */
	enum CommandProcessType {
		;
		private Class<? extends Processor> cls;

		CommandProcessType(Class<? extends Processor> cls) {
			this.cls = cls;
		}

		public Processor newProcessor() throws IllegalAccessException, InstantiationException {
			return cls.newInstance();
		}
	}

	/**
	 * 根据命令与输入内容构建执行命令序列
	 * @param command 命令集合
	 * @param commandLine 命令行
	 * @param body 命令行输入内容
	 * @throws Exception 错误的命令将抛出异常
	 */
	public void build(Command command, String commandLine, String body) throws Exception {
		commandLine = commandLine.replace("%", "").replaceAll("\\t", " ").replaceAll("\\s+", " ");
		String[] cmds = commandLine.split(" ");
		String[] paras = null;
		if (cmds.length > 2) {
			paras = Arrays.copyOfRange(cmds, 2, cmds.length - 1);
		}
		CommandType type = CommandType.valueOf(cmds[0]);
		try {
			if (type == CommandType.in) {
				command.addExecutor(type.getReader(cmds[1]), paras, body);
			} else if (type == CommandType.out) {
				command.addExecutor(type.getWriter(cmds[1]), paras, body);
			} else if (type == CommandType.process) {
				command.addExecutor(type.getProcessor(cmds[1]), paras, body);
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
