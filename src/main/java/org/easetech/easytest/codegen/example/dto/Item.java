
package org.easetech.easytest.codegen.example.dto;

import org.easetech.easytest.codegen.example.dto.ExampleConstants.ExampleEnumType;

public class Item {
  
	
	/** error code root key. */
    public static final String ERRORCODE_ROOT_KEY = ".errorcode.";

    /** Error Code 15001. */
    public static final String TRANSACTION_WAS_NOT_STARTED = ERRORCODE_ROOT_KEY + "15001";

    /** Error Code 15002. */
    public static final String TRANSACTION_ALREADY_STARTED = ERRORCODE_ROOT_KEY + "15002";

    /** Error Code 15003. */
    public static final String TRANSACTION_NOT_COMMIT_OR_ROLLBACK = ERRORCODE_ROOT_KEY + "15003";
    private String[] orderByAscendingFieldNames = null;
    private String[] orderByDescendingFieldNames = null;
    

	

	private String description;
    private String itemType;
    private String itemSubType;
 

	private String itemId;    
    private ExampleEnumType exampleEnumType;
    private java.sql.Timestamp ts;


	private Boolean isItem;
    private boolean isItemPrim;
    private short shortItem;
    private Short langShortItem;
    private Byte langByteItem;
    private byte byteItem;
    private char charItem;

    public String getItemSubType() {
 		return itemSubType;
 	}

 	public void setItemSubType(String itemSubType) {
 		this.itemSubType = itemSubType;
 	}
	public java.sql.Timestamp getTs() {
		return ts;
	}

	public void setTs(java.sql.Timestamp ts) {
		this.ts = ts;
	}

	private ItemCategory itemCategory;

    public ItemCategory getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	/**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType the itemType to set
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public ExampleEnumType getExampleEnumType() {
		return exampleEnumType;
	}

	public void setExampleEnumType(ExampleEnumType exampleEnumType) {
		this.exampleEnumType = exampleEnumType;
	}
	
    public Boolean getIsItem() {
		return isItem;
	}

	public void setIsItem(Boolean isItem) {
		this.isItem = isItem;
	}

	public boolean getIsItemPrim() {
		return isItemPrim;
	}

	public void setIsItemPrim(boolean isItemPrim) {
		this.isItemPrim = isItemPrim;
	}

	public short getShortItem() {
		return shortItem;
	}

	public void setShortItem(short shortItem) {
		this.shortItem = shortItem;
	}

	public Short getLangShortItem() {
		return langShortItem;
	}

	public void setLangShortItem(Short langShortItem) {
		this.langShortItem = langShortItem;
	}

	public Byte getLangByteItem() {
		return langByteItem;
	}

	public void setLangByteItem(Byte langByteItem) {
		this.langByteItem = langByteItem;
	}

	public byte getByteItem() {
		return byteItem;
	}

	public void setByteItem(byte byteItem) {
		this.byteItem = byteItem;
	}

	public char getCharItem() {
		return charItem;
	}

	public void setCharItem(char charItem) {
		this.charItem = charItem;
	}
	

    public String[] getOrderByAscendingFieldNames() {
		return orderByAscendingFieldNames;
	}

	public void setOrderByAscendingFieldNames(String[] orderByAscendingFieldNames) {
		this.orderByAscendingFieldNames = orderByAscendingFieldNames;
	}

	public String[] getOrderByDescendingFieldNames() {
		return orderByDescendingFieldNames;
	}

	public void setOrderByDescendingFieldNames(String[] orderByDescendingFieldNames) {
		this.orderByDescendingFieldNames = orderByDescendingFieldNames;
	}

	@Override
	public String toString() {
		return "Item [description=" + description + ", itemType=" + itemType
				+ ", itemId=" + itemId + ", exampleEnumType=" + exampleEnumType
				+ ", ts=" + ts + ", isItem=" + isItem + ", isItemPrim="
				+ isItemPrim + ", shortItem=" + shortItem + ", langShortItem="
				+ langShortItem + ", langByteItem=" + langByteItem
				+ ", byteItem=" + byteItem + ", charItem=" + charItem
				+ ", itemCategory=" + itemCategory + "]";
	}



}

