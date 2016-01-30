package viktor.prog4.app;

import viktor.prog4.api.*;

import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.docx4j.wml.PPr;
import org.docx4j.wml.RPr;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Color;
import javax.xml.bind.JAXBElement;
import java.util.List;
import java.math.BigInteger;
import org.docx4j.wml.ParaRPr;

public class DocChanger{
	private org.docx4j.wml.HpsMeasure fontSize;
	private org.docx4j.wml.Color fontColor;
	private List<Object> contentList;

	public DocChanger(List<Object> contentList){this(contentList,0,null);}

	public DocChanger(List<Object> contentList,int fontsize){this(contentList,fontsize,null);}

	public DocChanger(List<Object> contentList,int fontsize,String fontcolor){
		this.contentList=contentList;
		org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();
	 	this.fontSize = wmlObjectFactory.createHpsMeasure();
   		if(fontsize!=0) this.fontSize.setVal(BigInteger.valueOf(fontsize));	
		this.fontColor=wmlObjectFactory.createColor();
		if(fontcolor!=null)	this.fontColor.setVal(getHexColor(fontcolor));
		
	}

	public org.docx4j.wml.P getParagraph(int index){
			return (org.docx4j.wml.P) contentList.get(index);
	}

	public org.docx4j.wml.Tbl getTable(int index){
		Object o =contentList.get(index);	
		if(o instanceof JAXBElement){
			if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Tbl")){
				return (org.docx4j.wml.Tbl)((JAXBElement) o).getValue();
			}
		}
		return null;
	}
	
	public void setTableFontSize(org.docx4j.wml.Tbl table){
		for(Object o : table.getContent()){
			if(o instanceof org.docx4j.wml.Tr){
				for(Object o1: ((org.docx4j.wml.Tr) o).getContent()){
				 if(o1 instanceof JAXBElement){
				  	 if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Tc")){
				    	 org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement)o1).getValue();
							for(Object o2 : tC.getContent()){
								if(o2 instanceof org.docx4j.wml.P){
										setParagraphFontSize((org.docx4j.wml.P) o2);
								}
							}
						}
				    }
				  }
			}
		}
	}

	public void setTableFontColor(org.docx4j.wml.Tbl table){
		for(Object o : table.getContent()){
			if(o instanceof org.docx4j.wml.Tr){
				for(Object o1: ((org.docx4j.wml.Tr) o).getContent()){
				 if(o1 instanceof JAXBElement){
				  	 if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Tc")){
				    	 org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement)o1).getValue();
							for(Object o2 : tC.getContent()){
								if(o2 instanceof org.docx4j.wml.P){
										setParagraphFontColor((org.docx4j.wml.P) o2);
								}
							}
						}
				    }
				  }
			}
		}
	}


	public void setParagraphFontSize(org.docx4j.wml.P par){
			org.docx4j.wml.ParaRPr parRProp=par.getPPr().getRPr();
			parRProp.setSz(fontSize);
			parRProp.setSzCs(fontSize);
			for(Object o : par.getContent()){
				if(o instanceof org.docx4j.wml.R){
					org.docx4j.wml.R run = (R) o;
					run.getRPr().setSz(fontSize);
					run.getRPr().setSzCs(fontSize);
				}
			}
	}
	
	public void setParagraphFontColor(org.docx4j.wml.P par){
			org.docx4j.wml.ParaRPr parRProp=par.getPPr().getRPr();
			parRProp.setColor(fontColor);
			for(Object o : par.getContent()){
				if(o instanceof org.docx4j.wml.R){
					org.docx4j.wml.R run = (R) o;
					run.getRPr().setColor(fontColor);
				}
			}
	}

	public org.docx4j.wml.R getRun(org.docx4j.wml.P par,String text){
		for(Object o : par.getContent()){
			if(o instanceof org.docx4j.wml.R){
				String textR=new String();
				org.docx4j.wml.R run =(org.docx4j.wml.R) o;
				for(Object o1 : run.getContent()){
					if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
					  org.docx4j.wml.Text textF=(org.docx4j.wml.Text) ((JAXBElement)o1).getValue();
						textR+=textF.getValue();
					}
				}
				if((textR.matches(".*"+text+".*"))||(text.matches(".*"+textR+".*"))) return run;
			}
		}
		return null;
	}

	public void setRunFontSize(org.docx4j.wml.R run){
		run.getRPr().setSz(fontSize);
		run.getRPr().setSzCs(fontSize);
	}

	public void setRunFontColor(org.docx4j.wml.R run){
		run.getRPr().setColor(fontColor);
	}

	private String getHexColor(String color){
			String[][] colorBase={{"black","red","green","blue","yellow","orange","white","violet"},{"000000","F20202","06CD51","0041B2","FFE143","F69806","FFFFFF","D20EEA"}};
			for(int i=0;i<colorBase.length;i++){
				if(colorBase[0][i].equals(color)) return colorBase[1][i];
			}
			return null;
	}

	public void setBulletFontSize(List<Integer> indexList){
		for(Integer index : indexList){
			org.docx4j.wml.P par=getParagraph(index);
			setParagraphFontSize(par);
		}
	}

	public void setBulletFontColor(List<Integer> indexList){
		for(Integer index : indexList){
			org.docx4j.wml.P par=getParagraph(index);
			setParagraphFontColor(par);
		}
	}

}
