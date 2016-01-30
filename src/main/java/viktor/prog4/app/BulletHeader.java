package viktor.prog4.app;

import viktor.prog4.api.LineItem;


public class BulletHeader implements LineItem{
	protected static final String name="header";
	private String value,id;

	public BulletHeader(String val,String id){
		this.value=val;
		this.id=id;
	}
	public void setValue(String value){ this.value=value;}
	public void setId(String id){ this.id=id;}
	public String getName(){return name;}
	public String getValue(){ return value;}
	public String getId(){ return id;}
	public boolean isInner(){ return true;}
}
