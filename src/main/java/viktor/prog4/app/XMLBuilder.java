package viktor.prog4.app;

import viktor.prog4.api.*;
import java.util.List;
import java.io.OutputStream;
import java.io.IOException;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class XMLBuilder{
	private OutputStream outputStream;
	private List<LineItem> itemList;

	public void setXMLOutput(OutputStream out){ this.outputStream=out;}

	public void setContent(List<LineItem> list){ this.itemList=list;}
	
	public  void buildXml() throws IOException{
		boolean bulletPresent=false;
		Element bulletBlock=null;
		Element bulletPoint=null;
		Element document=new Element("document");
		Document doc=new Document(document);
		//doc.setRootElement(document);
/*------------------------------------------------------------------------------------------------------*/
		for(LineItem item : itemList){
			if(!item.isInner()){
				if(bulletPresent){
					bulletBlock.addContent(bulletPoint);
					bulletPoint=null;
					doc.getRootElement().addContent(bulletBlock);
					bulletPresent=false;
				}
				doc.getRootElement().addContent(createElement(item));
				continue;
			}
			else{
				if(!bulletPresent){
					String id=item.getId();
			bulletBlock=createElement(new Bullet(id.substring(0,id.indexOf("."))+".BB"));
				bulletPresent=true;
				}
				if(item.getName().equals(BulletHeader.name)){
					bulletBlock.addContent(createElement(item));
				}
				else if(item.getName().equals(BulletPoint.name)){
					if(bulletPoint!=null) bulletBlock.addContent(bulletPoint);
					bulletPoint=createElement(item);
					continue;
				}
				else if(item.getName().equals(Sentence.name)){
					bulletPoint.addContent(createElement(item));
					continue;
				}
			}
		}
		if(bulletPresent){
			bulletBlock.addContent(bulletPoint);
			doc.getRootElement().addContent(bulletBlock);
		}
		
/*------------------------------------------------------------------------------------------------------*/
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc,outputStream);
	}
	
	private Element createElement(LineItem item){
			Element elem=new Element(item.getName());
			elem.setAttribute(new Attribute("id",item.getId()));
			String text=item.getValue();
			if(text!=null) elem.setText(text);
			return elem;
	}
}
