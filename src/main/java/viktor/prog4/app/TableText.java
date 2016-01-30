package viktor.prog4.app;

import viktor.prog4.api.LineItem;


public class TableText implements LineItem{
	protected static final String name="table_text";
	private String value,id;

	public TableText(String value,String id){
		this.id=id;
		this.value=value;
	}
	public void setValue(String value){this.value=value;}
	public void setId(String id){ this.id=id;}
	public String getName(){return name;}
	public String getValue(){ return value;}
	public String getId(){ return id;}
	public boolean isInner(){ return false;}
}
