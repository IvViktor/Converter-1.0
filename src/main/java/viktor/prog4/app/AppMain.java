package viktor.prog4.app;

import viktor.prog4.api.*;
import java.util.List;
import java.io.FileOutputStream;
import java.io.File;
import org.jdom2.Element;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;


public class AppMain{
		private static final String usage="syntax: converter 'mode' 'xmlfile' 'docxfile' [options]\n"
									+"-cr create xml file from docx file\n"
							+"-ch -change content of docx file in according to xml file indexes\n"
							+"-sf <docx file> define docx source file\n"
					+"-xf <xml file> define xml file (if file doesn't exist it will be create"
					+" automatically\n"
					+"-id <id> set id number of xml tag\n"
					+"-fs <size> set the font size of selected lineitem\n"
					+"-fc <color> set the font color of selected lineitem. Possible values:"
					+"black, red, green, blue, yellow, orange,white, violet\n"
					+"-h -print this help message";
	public static void main(String[] args) throws Exception{
		if(args.length==0){System.out.println("SYNTAX ERROR\n"+usage);System.exit(1);}
		String fontColor=null, id=null;
		File srcFile=null, xmlFile=null;
		int fontSize=0;
		WordprocessingMLPackage wmlPack=null;
		boolean wordToXml=false;
		boolean xmlParse=false;
		DocParser parser=null;
	   	List<Object> contentList=null; 	
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-cr")) wordToXml=true;
			else if(args[i].equals("-ch")) xmlParse=true;
			else if(args[i].equals("-sf")) srcFile=new File(args[++i]);
			else if(args[i].equals("-xf")) xmlFile=new File(args[++i]);
			else if(args[i].equals("-id")) id=args[++i];
			else if(args[i].equals("-fs")) fontSize=Integer.parseInt(args[++i]);
			else if(args[i].equals("-fc")) fontColor=args[++i];
			else if(args[i].equals("-h")) System.out.println(usage);
			else System.out.println("ERROR!!! UNKNOWN KEY.\n"+usage);
		}
		if(wordToXml || xmlParse){
			parser=new DocParser();
			wmlPack=parser.getFileContent(srcFile);
			contentList=parser.getPackContent(wmlPack);
		}
		if(wordToXml){
		try{
		XMLBuilder builder=new XMLBuilder();
		List<LineItem> itemList=parser.parseContent(contentList);
		List<LineItem> filteredList=parser.itemListFilter(itemList);
		builder.setContent(filteredList);
		builder.setXMLOutput(new FileOutputStream(xmlFile));
		builder.buildXml();
		System.out.println("Program finished.");
		}catch (Exception e){e.printStackTrace();}
		}
		if(xmlParse){
			XMLParser xmlparser=new XMLParser();	
			List<Element> elementList=xmlparser.readXMLFile(xmlFile);
			Element element=xmlparser.getNodeValue(elementList,id);
			String value=xmlparser.getElementText(element);
			String[] idSplit=id.split("\\.");
			String fNum=idSplit[0];
			String type=idSplit[idSplit.length-1];
			DocChanger dc=new DocChanger(contentList,fontSize*2,fontColor);
			if((type.equals("T"))||(type.equals("BH"))){
				org.docx4j.wml.P par=dc.getParagraph(Integer.parseInt(fNum));
				dc.setParagraphFontSize(par);
				dc.setParagraphFontColor(par);
			}
			else if(type.equals("TT")){
				org.docx4j.wml.Tbl table=dc.getTable(Integer.parseInt(fNum));
				dc.setTableFontSize(table);
				dc.setTableFontColor(table);
			}
			else if((type.equals("BB"))||(type.equals("BP"))){
					List<Integer> intList=xmlparser.getBulletIndexes(element);
					dc.setBulletFontSize(intList);
					dc.setBulletFontColor(intList);
			}
			else{
				org.docx4j.wml.P par=dc.getParagraph(Integer.parseInt(fNum));
				org.docx4j.wml.R run =dc.getRun(par,value);
				dc.setRunFontSize(run);
				dc.setRunFontColor(run);
			}
			wmlPack.save(srcFile);
		}
	}
}
