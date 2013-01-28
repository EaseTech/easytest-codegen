
package org.easetech.easytest.codegen.example.dto;

/**
 * 
 * An example of user defined Strongly typed object
 * 
 */
public class ItemId {

    /**
     * The id
     */
    private Long id;
    
    public ItemId() {
        super();
    }

    public Long getId() {
  	return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
     * 
     * Construct a new ItemId
     * 
     * @param id theid
     */
    public ItemId(Long id) {
        this.id = id;
    }

    /**
     * @return the toString representation
     */
    @Override
    public String toString() {
        return "ItemId [id=" + id + "]";
    }

}

