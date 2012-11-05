package org.easetech.easytest.codegen.example.dto;

public class ItemCategory {
  
	private int categoryId;
	@Override
	public String toString() {
		return "ItemCategory [categoryId=" + categoryId + ", categoryName="
				+ categoryName + "]";
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	private String categoryName;

}

