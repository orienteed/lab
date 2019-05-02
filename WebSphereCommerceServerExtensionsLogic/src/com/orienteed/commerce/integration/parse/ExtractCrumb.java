package com.orienteed.commerce.integration.parse;

import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class ExtractCrumb extends XMLFilterImpl {
	private String tagName = "";
	public String crumb;
	public String crumbField;
	
	public ExtractCrumb() throws SAXException {
		super(XMLReaderFactory.createXMLReader());
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException {
		tagName = qName;
		super.startElement(uri, localName, qName, atts);
	}
	public void endElement(String uri, String localName, String qName) throws SAXException {
		tagName = "";
		super.endElement(uri, localName, qName);
	}
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (tagName.equals("crumb")) {
			crumb = String.valueOf(Arrays.copyOfRange(ch, start, start+length));
		} else if (tagName.equals("crumbRequestField")) {
			crumbField = String.valueOf(Arrays.copyOfRange(ch, start, start+length));
		}
	}
}
