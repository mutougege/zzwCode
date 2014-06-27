package com.popcorn.wordfilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * 多叉树关键词过滤
 * @author Alex.Zhangrj
 * 北京师范大学计算机系2000�?张人�?
 * zhrenjie04@126.com
 * alex.zhangrj@hotmail.com
 */
public class WordFilterUtil {

	private static Node tree;

	static {
		tree = new Node();
		InputStream is = WordFilterUtil.class.getResourceAsStream("/words.dict");
		try {
			InputStreamReader reader = new InputStreamReader(is, "UTF-8");
			Properties prop = new Properties();
			prop.load(reader);
			Enumeration<String> en = (Enumeration<String>)prop.propertyNames();
			while(en.hasMoreElements()){
				String word = en.nextElement();
				insertWord(word,Integer.valueOf(prop.getProperty(word)));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private static void insertWord(String word,int level){
		Node node = tree;
		for(int i=0;i<word.length();i++){
			node = node.addChar(word.charAt(i));
		}
		node.setEnd(true);
		node.setLevel(level);
	}

	private static boolean isPunctuationChar(String c) {
		//String regex = "[\\pP|\\pZ|\\pS|\\pM|\\pC]";
		String aa = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]";
		Pattern p = Pattern.compile(aa, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(c);
		return m.find();
		
	}

	private static PunctuationOrHtmlFilteredResult filterPunctation(String originalString){
		StringBuffer filteredString=new StringBuffer();
		ArrayList<Integer> charOffsets=new ArrayList<Integer>();
		for(int i=0;i<originalString.length();i++){
			String c = String.valueOf(originalString.charAt(i));
			if (!isPunctuationChar(c)) {
				filteredString.append(c);
				charOffsets.add(i);
			}
		}
		PunctuationOrHtmlFilteredResult result = new PunctuationOrHtmlFilteredResult();
		result.setOriginalString(originalString);
		result.setFilteredString(filteredString);
		result.setCharOffsets(charOffsets);
		return result;
	}

	private static PunctuationOrHtmlFilteredResult filterPunctationAndHtml(String originalString){
		StringBuffer filteredString=new StringBuffer();
		ArrayList<Integer> charOffsets=new ArrayList<Integer>();
		for(int i=0,k=0;i<originalString.length();i++){
			String c = String.valueOf(originalString.charAt(i));
			if (originalString.charAt(i) == '<') {
				for(k=i+1;k<originalString.length();k++) {
					if (originalString.charAt(k) == '<') {
						k = i;
						break;
					}
					if (originalString.charAt(k) == '>') {
						break;
					}
				}
				i = k;
			} else {
				if (!isPunctuationChar(c)) {
					filteredString.append(c);
					charOffsets.add(i);
				}
			}
		}
		PunctuationOrHtmlFilteredResult result = new PunctuationOrHtmlFilteredResult();
		result.setOriginalString(originalString);
		result.setFilteredString(filteredString);
		result.setCharOffsets(charOffsets);
		return result;
	}

	private static FilteredResult filter(PunctuationOrHtmlFilteredResult pohResult, char replacement){
		StringBuffer sentence =pohResult.getFilteredString();
		ArrayList<Integer> charOffsets = pohResult.getCharOffsets();
		StringBuffer resultString = new StringBuffer(pohResult.getOriginalString());
		StringBuffer badWords = new StringBuffer();
		int level=0;
		Node node = tree;
		int start=0,end=0;
		int count = 0;
		for(int i=0;i<sentence.length();i++){
			start=i;
			end=i;
			node = tree;
			for(int j=i;j<sentence.length();j++){
				node = node.findChar(sentence.charAt(j));
				if(node == null){
					break;
				}
				if(node.isEnd()){
					end=j;
					level = node.getLevel();
				}
			}
			if(end>start){
				for(int k=start;k<=end;k++){
					resultString.setCharAt(charOffsets.get(k), replacement);
				}
				if(badWords.length()>0)badWords.append(",");
				badWords.append(sentence.substring(start, end+1));
				i=end;
				count++;
			}
		}
		
		FilteredResult result = new FilteredResult();
		result.setOriginalContent(pohResult.getOriginalString());
		result.setFilteredContent(resultString.toString());
		result.setCount(count);
		result.setBadWords(badWords.toString());
		result.setLevel(level);
		return result;
	}
	
	/**
	 * �?��句子过滤
	 * 不处理特殊符号，不处理html，简单句子的过滤
	 * 不能过滤中间带特殊符号的关键词，如：黄_色_漫_�?
	 * @param sentence �?��过滤的句�?
	 * @param replacement 关键词替换的字符
	 * @return 过滤后的句子
	 */
	public static String simpleFilter(String sentence, char replacement){
		StringBuffer sb=new StringBuffer();
		Node node = tree;
		int start=0,end=0;
		for(int i=0;i<sentence.length();i++){
			start=i;
			end=i;
			node = tree;
			for(int j=i;j<sentence.length();j++){
				node = node.findChar(sentence.charAt(j));
				if(node == null){
					break;
				}
				if(node.isEnd()){
					end=j;
				}
			}
			if(end>start){
				for(int k=start;k<=end;k++){
					sb.append(replacement);
				}
				i=end;
			}else{
				sb.append(sentence.charAt(i));
			}
		}
		return sb.toString();
	}
	/**
	 * 纯文本过滤，不处理html标签，直接将去除�?��特殊符号后的字符串拼接后进行过滤，可能会去除html标签内的文字，比如：如果有关键字“fuckfont”，过滤fuck<font>a</font>后的结果�?***<****>a</font>
	 * @param originalString 原始�?��滤的�?
	 * @param replacement 替换的符�?
	 * @return
	 */
	public static FilteredResult filterText(String originalString, char replacement){
		return filter(filterPunctation(originalString), replacement);
	}
	/**
	 * html过滤，处理html标签，不处理html标签内的文字，略有不足，会跳�?>标签内的�?��内容，比如：如果有关键字“fuck”，过滤<a title="fuck">fuck</a>后的结果�?a title="fuck">****</a>
	 * @param originalString 原始�?��滤的�?
	 * @param replacement 替换的符�?
	 * @return
	 */
	public static FilteredResult filterHtml(String originalString, char replacement){
		return filter(filterPunctationAndHtml(originalString), replacement);
	}
	public static void main(String[] args){
		System.out.println(WordFilterUtil.simpleFilter("网站黄色漫画网站",'*'));
		FilteredResult result = WordFilterUtil.filterText("网站�?�?�?�?网站",'*');
		System.out.println(result.getFilteredContent());
		System.out.println(result.getBadWords());
		System.out.println(result.getLevel());
		result = WordFilterUtil.filterHtml("网站<font>�?/font>.<�?script>,�?�?网站",'*');
		System.out.println(result.getFilteredContent());
		System.out.println(result.getBadWords());
		System.out.println(result.getLevel());
		
		String str = "�?�?的社 会中  国是我们$多么和谐的一个中###�?#�?de民和�?�?会啊�?：魔司法";
		// str = "近日";
		// 5000�?
		str = "d";

		// str = "";
		long start = System.currentTimeMillis();
		result = WordFilterUtil.filterHtml(str,'*');
		long end = System.currentTimeMillis();
		System.out.println("====Time====" + (end - start));
		System.out.println("original:"+result.getOriginalContent());
		System.out.println("result:"+result.getFilteredContent());
		System.out.println("badWords:"+result.getBadWords());
		System.out.println("level:"+result.getLevel());
		start = System.currentTimeMillis();
		result = WordFilterUtil.filterText(str,'*');
		end = System.currentTimeMillis();
		System.out.println("====Time====" + (end - start));
		System.out.println("original:"+result.getOriginalContent());
		System.out.println("result:"+result.getFilteredContent());
		System.out.println("badWords:"+result.getBadWords());
		System.out.println("level:"+result.getLevel());

	}





	private static class PunctuationOrHtmlFilteredResult {
		private String originalString;
		private StringBuffer filteredString;
		private ArrayList<Integer> charOffsets;
		
		public String getOriginalString() {
			return originalString;
		}
		public void setOriginalString(String originalString) {
			this.originalString = originalString;
		}
		public StringBuffer getFilteredString() {
			return filteredString;
		}
		public void setFilteredString(StringBuffer filteredString) {
			this.filteredString = filteredString;
		}
		public ArrayList<Integer> getCharOffsets() {
			return charOffsets;
		}
		public void setCharOffsets(ArrayList<Integer> charOffsets) {
			this.charOffsets = charOffsets;
		}
	}

}
