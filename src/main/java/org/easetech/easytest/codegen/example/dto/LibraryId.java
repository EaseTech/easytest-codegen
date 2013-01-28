
package org.easetech.easytest.codegen.example.dto;

/**
 * 
 * An example of user defined Strongly typed object
 * 
 */
public class LibraryId {

    /**
     * The id
     */
    private Long id;

    /**
     * 
     * Construct a new LibraryId
     * 
     * @param id the id
     */
    public LibraryId(Long id) {
        this.id = id;
    }
    
    public LibraryId() {
        super();
    }

    public Long getId() {
  	return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
     * @return the toString representation
     */
    @Override
    public String toString() {
        return "LibraryId [id=" + id + "]";
    }

}
