package com.popcorn.wordfilter;

public class FilteredResult {

	private Integer level;// 文本�?��警告级别
	private String filteredContent;// 屏蔽敏感词后的文本内�?
	private String badWords;// 屏蔽的敏感词�?eg:色魔,法轮�?GCD
	private String originalContent;//原始内容
	private int count;
	
	
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getBadWords() {
		return badWords;
	}

	public void setBadWords(String badWords) {
		this.badWords = badWords;
	}

	public FilteredResult() {

	}

	public FilteredResult(String originalContent, String filteredContent, Integer level, String badWords) {
		this.originalContent=originalContent;
		this.filteredContent = filteredContent;
		this.level = level;
		this.badWords = badWords;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getFilteredContent() {
		return filteredContent;
	}

	public void setFilteredContent(String filteredContent) {
		this.filteredContent = filteredContent;
	}

	public String getOriginalContent() {
		return originalContent;
	}

	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}
	
}
