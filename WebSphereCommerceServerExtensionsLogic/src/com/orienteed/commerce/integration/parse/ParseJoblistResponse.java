package com.orienteed.commerce.integration.parse;

import java.time.Instant;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import com.orienteed.commerce.integration.utils.FormatMethods;

public class ParseJoblistResponse extends XMLFilterImpl {
	
	private String tagName = "";
	private String text = "";
	
	public ParseJoblistResponse() throws SAXException {
		super(XMLReaderFactory.createXMLReader());
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException {
		tagName = qName;
		super.startElement(uri, localName, qName, atts);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (tagName.equals("duration")) {
			long duration = Long.parseLong(text);
			char[] ch = FormatMethods.millisToString(duration).toCharArray();
			super.characters(ch, 0, ch.length);
		}
		if (tagName.equals("timestamp")) {
			long now = Instant.now().toEpochMilli();
			long number = Long.parseLong(text);
			char[] ch = FormatMethods.millisToString(now - number).toCharArray();
			super.characters(ch, 0, ch.length);
		}
		text = "";
		tagName = "";
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (tagName.equals("duration") || tagName.equals("timestamp")) {
			text += String.valueOf(Arrays.copyOfRange(ch, start, start+length)); 
		} else {          
			super.characters(ch, start, length);
		}
	}
}
