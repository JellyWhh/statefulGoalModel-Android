/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

/**
 * 树形结构节点类
 * 
 * @author whh
 * 
 */
public class TreeElement {

	private String id; // 当前节点ID
	private String title; // 当前界面文字，也就是要显示在界面上名称或者描述
	private boolean hasParent; // 当前节点是否有父节点
	private boolean hasChild; // 当前节点是否有子节点
	private String parentId; // 如果有父节点，这个是父节点的ID
	private int level; // 当前界面层级
	private boolean fold; // 当前节点是否处于折叠状态，true为折叠状态，false为unfold展开状态

	public TreeElement(String id, String title, boolean hasParent,
			boolean hasChild, String parentId, int level, boolean fold) {
		this.id = id;
		this.title = title;
		this.hasParent = hasParent;
		this.hasChild = hasChild;
		this.parentId = parentId;
		this.level = level;
		this.fold = fold;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHasParent() {
		return hasParent;
	}

	public void setHasParent(boolean hasParent) {
		this.hasParent = hasParent;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isFold() {
		return fold;
	}

	public void setFold(boolean fold) {
		this.fold = fold;
	}

}
