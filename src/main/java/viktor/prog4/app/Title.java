package viktor.prog4.app;

import viktor.prog4.api.LineItem;


public class Title implements LineItem{
	protected static final String name="title";
	private String value,id;

	public Title(String value,String id){
		this.value=value;
		this.id=id;
	}
	public void setValue(String value){ this.value=value;}
	public void setId(String id){ this.id=id;}
	public String getName(){return name;}
	public String getValue(){ return value;}
	public String getId(){ return id;}
	public boolean isInner(){ return false;}
}
