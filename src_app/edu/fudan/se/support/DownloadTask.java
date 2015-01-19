/**
 * 
 */
package edu.fudan.se.support;

/**
 * 从服务器上下载goal model的任务
 * 
 * @author whh
 * 
 */
public class DownloadTask {

	private String name;
	private String url;
	private boolean isDownload; // 是否已经被下载

	public DownloadTask(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

}
