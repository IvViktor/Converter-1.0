package viktor.prog4.app;

import viktor.prog4.api.*;
import java.util.List;
import java.util.LinkedList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.io.IOException;

public class XMLParser{

    /*public XMLParser(File file){
			this.xmlFile=file;
	}*/

	public List<Element> readXMLFile(File xmlFile){
		try{	
			SAXBuilder builder=new SAXBuilder();
			Document doc=builder.build(xmlFile);
			Element rootNode=doc.getRootElement();
			return rootNode.getChildren();
		}catch (JDOMException jde){System.err.println("I/O Error occured while reading "+xmlFile.getName());jde.printStackTrace();}
		catch (IOException jde){System.err.println("I/O Error occured while reading "+xmlFile.getName());}
			return new LinkedList<Element>();
	}

	public Element getNodeValue(List<Element> list, String id) throws IllegalArgumentException{
		List<Element> subList=new LinkedList<Element>();
		for(Element element : list){
			if(element.getName().equals(Bullet.name)) subList.add(element);
			if(element.getName().equals(BulletPoint.name)) subList.add(element);
			if(element.getAttributeValue("id").equals(id)){
					return element;
			}
		}
		for(Element subEl : subList){
			try{	
			return getNodeValue(subEl.getChildren(),id);
			}catch (IllegalArgumentException e){ continue;}
		}
		throw new IllegalArgumentException("No such ID in current xml file. Please try another.");
	}
	
	public String getElementText(Element element){
			return element.getText();
	}

	public List<Integer> getBulletIndexes(Element element){
		List<Integer> intList=new LinkedList<>();	
		for(Element el : element.getChildren()){
			if(el.getName().equals(BulletPoint.name)){
				for(Element el1 : el.getChildren()){
					intList.add(getElementIndex(el1));
				}
			}
			else intList.add(getElementIndex(el));
		}
		return intList;
	}

	private Integer getElementIndex(Element element){
			String preId=element.getAttributeValue("id");
			String[] idSplit=preId.split("\\.");
			return new Integer(Integer.parseInt(idSplit[0]));
	}

}	
