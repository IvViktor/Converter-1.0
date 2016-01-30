package viktor.prog4.app;

import viktor.prog4.api.LineItem;


public class Sentence implements LineItem{
	protected static final String name="sentence";
	private String value,id;
	private boolean inner;
	
	public Sentence(String value,String id,boolean inner){
		this.value=value;
		this.id=id;
		this.inner=inner;
	}
	public void setValue(String value){ this.value=value;}
	public void setId(String id){ this.id=id;}
	public String getName(){return name;}
	public String getValue(){ return value;}
	public String getId(){ return id;}
	public boolean isInner(){ return inner;}

}
