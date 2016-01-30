package viktor.prog4.app;

import viktor.prog4.api.LineItem;


public class BulletPoint implements LineItem{
	protected static final String name="point";
	private String id;

	public BulletPoint(String id){
		this.id=id;
	}
	public void setValue(String value){ }
	public void setId(String id){ this.id=id;}
	public String getName(){return name;}
	public String getValue(){ return null;}
	public String getId(){ return id;}
	public boolean isInner(){ return true;}

}
