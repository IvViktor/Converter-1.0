package viktor.prog4.app;

import viktor.prog4.api.*;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.regex.*;

import javax.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.PPrBase.NumPr;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.BooleanDefaultTrue;

import org.docx4j.wml.Tbl;

public class DocParser{
	private List<LineItem> itemList;
	private StringBuilder shareLines;
	private StringBuilder possibleBullet;	

	public DocParser(){
		this.itemList=new LinkedList<LineItem>();
		this.shareLines=new StringBuilder();
		this.possibleBullet=new StringBuilder();
	}

	public void addItem(LineItem item){ itemList.add(item);}


	public List<LineItem> parseContent(List<Object> body){
		int contentIndex=-1;
		for(Object o : body){
			contentIndex++;
			if(o instanceof JAXBElement){
			if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Tbl")){
				Tbl table=(org.docx4j.wml.Tbl)((JAXBElement) o).getValue();
				parseWMLTable(table, contentIndex);	
			}
			}
			else if(o instanceof org.docx4j.wml.P){
				parseParagraph((P) o, contentIndex);
			} 
		}
		return this.itemList;
	}

	public WordprocessingMLPackage getFileContent(java.io.File file) throws Docx4JException{
	//	try{
		WordprocessingMLPackage wmlPack=WordprocessingMLPackage.load(file);
		return wmlPack;//.	
	//	}catch (Docx4JException e){System.out.println("Cannot load docx file "+file.getName());
	//									 e.printStackTrace();}
	}	

	public List<Object> getPackContent(WordprocessingMLPackage pack){
			return pack.getMainDocumentPart().getContent();
	}

	public void parseWMLTable(org.docx4j.wml.Tbl table,int upperId){
		StringBuilder tableText=new StringBuilder();
		for(Object o : table.getContent()){
		  if(o instanceof org.docx4j.wml.Tr){
			for(Object o1: ((org.docx4j.wml.Tr) o).getContent()){
			  if(o1 instanceof JAXBElement){
			   if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Tc")){
			     org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement)o1).getValue();
				for(Object o2 : tC.getContent()){
				  if(o2 instanceof org.docx4j.wml.P){
				   for(Object o3 : ((org.docx4j.wml.P) o2).getContent()){
					if(o3 instanceof org.docx4j.wml.R){
					  for(Object o4 :((org.docx4j.wml.R)o3).getContent()){
						if(((JAXBElement)o4).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
						  org.docx4j.wml.Text text=(org.docx4j.wml.Text) ((JAXBElement)o4).getValue();
			 			  tableText.append(text.getValue());
						  tableText.append(" ");
						}
					  }
					}
				   }
				  }
				}
			   }
			  }
			}
		  }
		}
		if(tableText.length()>0){
			StringBuilder idStr=new StringBuilder();
			idStr.append(upperId);
			addItem(new TableText(tableText.toString(),idStr.toString()+".TT"));
		} 
	}

	public void parseParagraph(org.docx4j.wml.P par,int upperId){
		StringBuilder idStr=new StringBuilder();
		StringBuilder items=shareLines;
		//boolean isPCenter=isCenterAlign(par);
		boolean isPoint=isPBulletPoint(par);///
		List<String> stringList=new LinkedList<>();
/*-----------------------------------------------------------------------------------------------------*/
		for(Object o : par.getContent()){
		  if(o instanceof org.docx4j.wml.R){
			org.docx4j.wml.R run=(org.docx4j.wml.R)o;//((JAXBElement)o).getValue();
			//boolean isRBold=isRunBold(run);
			for(Object o1 : run.getContent()){
			 if(o1 instanceof JAXBElement){
			  if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
			  org.docx4j.wml.Text text=(org.docx4j.wml.Text) ((JAXBElement)o1).getValue();
				String textContent=text.getValue().trim();
				textAnalyser(stringList,textContent);
			  }
			 }
			}
		  // }
		  }
		}
/*------------------------------------------------------------------------------------------------------*/
		if(!stringList.isEmpty()){
		  if(isPoint){
			if(possibleBullet.length()>0){
	//	           addItem(new Bullet(upperId+".BB"));
 			   addItem(new BulletHeader(possibleBullet.toString(),(upperId-1)+".BH"));
			   possibleBullet.delete(0,possibleBullet.length());
			}
		  }
		  else{
			if(possibleBullet.length()>0){
 			   addItem(new Title(possibleBullet.toString(),upperId+".T"));
			   possibleBullet.delete(0,possibleBullet.length());
			}
		  }
		  boolean alreadyHasItem=false;
		  int subInd=0;
		  int stringListSize=stringList.size();
		  for(String str : stringList){
			subInd++;
			if(isPoint){
				if(items.length()==0){
				   if(str.matches(".*[.!?;]$")){
					if(!alreadyHasItem) addItem(new BulletPoint(upperId+".BP"));
					addItem(new Sentence(str,upperId+"."+subInd+".BS",true));
					alreadyHasItem=true;
					continue;
				   }
				   else{
					if(subInd!=stringListSize){
						items.append(str+" ");
						continue;
					}
					else{
						items.append(str);
				if(items.toString().matches(".*\\b\\p{javaUpperCase}+\\b$")){
				addItem(new Title(items.toString(),upperId+"."+subInd+".T"));
				}
			    		    else{
				//if(!alreadyHasItem) addItem(new BulletPoint(upperId+".BP"));
				addItem(new BulletHeader(items.toString(),upperId+".BH"));
				//alreadyHasItem=true;
					    }	
						items.delete(0,items.length());
						continue;
					}
				   }
				}
				else{
				   if(str.matches(".*[.!?;]$")){
					items.append(str);
				if(!alreadyHasItem) addItem(new BulletPoint(upperId+".BP"));
				addItem(new Sentence(items.toString(),upperId+"."+subInd+".BS",true));
				alreadyHasItem=true;
					items.delete(0,items.length());
					continue;
				   }
				   else if(subInd==stringListSize){
					items.append(str);
				if(!alreadyHasItem) addItem(new BulletPoint(upperId+".BP"));
				addItem(new Sentence(items.toString(),upperId+"."+subInd+".BS",true));
				alreadyHasItem=true;
					items.delete(0,items.length());
					continue;
				   }
				   else{
					items.append(str+" ");
					continue;
				   }
				}
			}
			else if(str.matches("^\\p{javaUpperCase}.*")){
				if(str.matches(".+[.!;?]$")){
				  if(items.length()>0){
				    items.append(str+" ");
				    addItem(new Sentence(items.toString(),upperId+"."+subInd+".S",false));
				    alreadyHasItem=true;
				    items.delete(0,items.length());
				    continue;
				  }
				  if((!alreadyHasItem)&&(subInd==stringListSize)){
					addItem(new Title(str,upperId+"."+subInd+".T"));
					continue;
				  }
				  else{
					addItem(new Sentence(str,upperId+"."+subInd+".S",false));
					alreadyHasItem=true;
					continue;
				  }
				}
				else if((str.matches(".*[:]$"))&&subInd==stringListSize){
				   possibleBullet.append(str);
				   continue;
				}
				else{
					if((!alreadyHasItem)&&(subInd==stringListSize)){
						addItem(new Title(str,upperId+"."+subInd+".T"));
						continue;
					}
					else{
						items.append(str+" ");
						continue;
					}
				}
			}
			else{
				if(str.matches(".*[!?;.]$")){
				    items.append(str+" ");
  				    addItem(new Sentence(items.toString(),upperId+"."+subInd+".S",false));
				    alreadyHasItem=true;
				    items.delete(0,items.length());
				    continue;
				}
				else if((str.matches(".*[:]$"))&&(subInd==stringListSize)){
				    items.append(str);
				    possibleBullet.append(items.toString());
				    items.delete(0,items.length());
				    continue;
				}
				else{
					items.append(str+" ");
					continue;
				}
			}
		  }
		}
	}
	
	private boolean isPBulletPoint(org.docx4j.wml.P par){
		org.docx4j.wml.PPrBase.NumPr numProp=par.getPPr().getNumPr();
		if(numProp!=null) return true;
		return false;
	}	

	private boolean isCenterAlign(org.docx4j.wml.P par){
		org.docx4j.wml.PPr parProp=par.getPPr();
		org.docx4j.wml.Jc parAlign=parProp.getJc();
		return (parAlign.getVal().equals(JcEnumeration.CENTER));
	}
	
	private boolean isRBold(org.docx4j.wml.R run){
		org.docx4j.wml.RPr runPr=run.getRPr();
		return runPr.getB().isVal();
	}

	private void textAnalyser(List<String> list,String text){
		Pattern regex=Pattern.compile("\\b.{3,}?(?:[.?;!]|(?:[:]\\s*$)|$)(?=\\s|$)");
	//	Pattern regex=Pattern.compile(".*");
		Matcher regexMatcher=regex.matcher(text);
		while(regexMatcher.find()){
			String completeSentence=regexMatcher.group().trim();
			if(completeSentence.length()>0) list.add(completeSentence);
		}
	}

	/*
	 *public static void main(String args[])throws Docx4JException{
	 *    DocParser dp=new DocParser();
	 *    List<Object> obList=dp.getFileContent(new java.io.File(args[0]));
	 *    List<LineItem> list=dp.parseContent(obList);
	 *    for(LineItem li : list){
	 *        System.out.println(li.getName()+"    "+li.getId()+"    "+li.getValue());
	 *    }
	 *}
	 */

	public List<LineItem> itemListFilter(List<LineItem> sourceList){
		List<LineItem> tmpList=new LinkedList<>();
		List<LineItem> rmList=new LinkedList<>();
		
		for(ListIterator<LineItem> iterator=sourceList.listIterator();iterator.hasNext();){
	/*		String itemName=item.getName();
			if(itemName.equals(Title.name)){
				for(LineItem remItem : tmpList) sourceList.remove(remItem);
				tmpList.clear();
				tmpList.add(item);
				continue;
			}
			else if(itemName.equals(BulletHeader.name)){
				tmpList.add(item);
				continue;
			}
			else{
				tmpList.clear();
			}*/
			LineItem item=iterator.next();
			if(item.getName().equals(BulletHeader.name)){
				tmpList.add(item);
			}
			else if(item.getName().equals(Title.name)){
				for(LineItem li : tmpList) rmList.add(li);
				tmpList.clear();
				tmpList.add(item);
			}
			else tmpList.clear();
		}
		for(LineItem item : rmList) sourceList.remove(item);
		return sourceList;
	}

}
