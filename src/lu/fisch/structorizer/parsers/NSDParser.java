/*
    Structorizer
    A little tool which you can use to create Nassi-Schneiderman Diagrams (NSD)

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.structorizer.parsers;

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    XML input parser to generate a structogramm frm it savefile.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date            Description
 *      ------          ----            -----------
 *      Bob Fisch       2007.12.13      First Issue
 *      Kay Gürtzig     2015.10.29      Enhancement on For loop (new attributes KGU#3)
 *      Kay Gürtzig     2015.10.29      Modification on For loop (new attribute KGU#3)
 *      Kay Gürtzig     2015.12.16      Bugfix #63 (KGU#111): Exception on parsing failure
 *      Kay Gürtzig     2016.01.08      Bugfix #99 (KGU#134): workaround for defective FOR loops
 *      Kay Gürtzig     2016.03.21      Enh. #84 (KGU#61): Enhancement towards FOR-IN loops
 *      Kay Gürtzig     2016.04.14      Enh. #158 (KGU#177): method parse() cloned
 *      Kay Gürtzig     2016.09.24      Enh. #250 - Robustness of FOR loop reconstruction improved
 *      Kay Gürtzig     2016.09.25      Enh. #253: D7Parser.keywordMap refactoring done.
 *      Kay Gürtzig     2016.10.13      Enh. #270: New Element property "disabled" integrated
 *      Kay Gürtzig     2016.12.21      Bugfix #317: Awareness of color attributes in subqueue nodes
 *      Kay Gürtzig     2017.03.10      Enh. #372: Additional attributes (Simon Sobisch)
 *
 ******************************************************************************************************
 *
 *      Comment:		/
 *
 ******************************************************************************************************///


import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;

import lu.fisch.utils.*;
import lu.fisch.structorizer.elements.*;
import lu.fisch.structorizer.io.Ini;

public class NSDParser extends DefaultHandler {

	private Root root = null;
	
	private Stack<Element>  stack   = new Stack<Element>();
	private Stack<Subqueue> ifStack = new Stack<Subqueue>();
	private Stack<Subqueue> qStack  = new Stack<Subqueue>();
	private Stack<Case>     cStack  = new Stack<Case>();
	private Stack<Parallel> pStack  = new Stack<Parallel>();
	
	//private boolean multi = false;
	
	private Subqueue lastQ = null;
	private Element lastE = null;
	
	private String fileVersion = "";
	
	// START KGU#258 2016-09-25: Enh. #253 holds the parser preferences saved with the file (3.25-01)
	private HashMap<String, StringList> savedParserPrefs = new HashMap<String, StringList>();
	private boolean ignoreCase = false;
	private boolean refactorKeywords = false;
	// END KGU#258 2016-09-25

        @Override
	public void startElement(String namespaceUri, String localName, String qualifiedName, Attributes attributes) throws SAXException 
	{
		// --- ELEMENTS ---
		if (qualifiedName.equals("root"))
		{
//			if (multi)
//			{
//				/*
//				root:=BRoot.create;
//				RootList.Add(root);
//				*/
//			}
			
			// START KGU#134 2016-01-08: File version now needed for bugfix #99 
			if (attributes.getIndex("version") != -1) { fileVersion = attributes.getValue("version"); }
			// So we might react to some incompatibility... 
//			if (version.indexOf("dev") != 0)
//			{
//				// Unstable version ...
//			}
			// END KGU 2016-01-08
			
			// START KGU#258 2016-09-25: Enh. #253 - read the saved parser preferences if any
			
			if (fileVersion.compareTo("3.25") > 0 && refactorKeywords)
			{
				for (String key: D7Parser.keywordSet())
				{
					if (attributes.getIndex(key) != -1)
					{
						String keyword = attributes.getValue(key);
						savedParserPrefs.put(key, Element.splitLexically(keyword, false));
					}
				}
				if (attributes.getIndex("ignoreCase") != -1)
				{
					ignoreCase = attributes.getValue("ignoreCase").equals("true");
				}
				// If no stored keywords were found then we don't need to refactor anything
				refactorKeywords = !savedParserPrefs.isEmpty();
			}
			// END KGU#258 2016-09-25
			
			// read attributes
			root.isProgram = true;
			if(attributes.getIndex("type")!=-1)  {if (attributes.getValue("type").equals("sub")) {root.isProgram=false;}}
			root.isNice=true;
			if(attributes.getIndex("style")!=-1)  {if (!attributes.getValue("style").equals("nice")) {root.isNice=false;}}
			// START KGU 2015-12-04: The following line was nonsense
			//if(attributes.getIndex("style")!=-1)  {if (attributes.getValue("type").equals(" ")) {root.isNice=true;}}
			// END KGU 2015-12-04
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {root.getColor();
			root.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			if(attributes.getIndex("text")!=-1)  {root.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {root.getComment().setCommaText(attributes.getValue("comment"));}
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
			
			// START KGU#363 2017-03-10: Enh. #372
			root.fetchAuthorDates(attributes);
			// END KGU#363 3017-03-10
			
			// place stack
			lastE = root;
			stack.push(root);
		}
		else if (qualifiedName.equals("instruction"))
		{
			// create element
			Instruction ele = new Instruction(StringList.getNew("???"));
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			//if(attributes.getIndex("rotated")!=-1)  {if (attributes.getValue("rotated").equals("1")) {ele.rotated=true;}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
			
			// START KGU#258 2016-09-25: Enh. #253
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-09-25
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("jump"))
		{
			// create element
			Jump ele = new Jump(StringList.getNew("???"));
			
			// read attributes
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
						
			// START KGU#258 2016-09-25: Enh. #253
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-09-25
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("call"))
		{
			// create element
			Call ele = new Call(StringList.getNew("???"));
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
			
			// Enh. #253: In a Call element, there isn't anything to refactor
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("alternative"))
		{
			// create element
			Alternative ele = new Alternative(StringList.getNew("???"));
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}

			// START KGU#258 2016-09-25: Enh. #253
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-09-25
			
			// set children 
			ele.qTrue.setColor(ele.getColor());
			ele.qFalse.setColor(ele.getColor());
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("while"))
		{
			// create element
			While ele = new While(StringList.getNew("???"));
			
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
			
			// START KGU#258 2016-09-25: Enh. #253
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-09-25
			
			// set children
			ele.q.setColor(ele.getColor());
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("for"))
		{
			// create element
			For ele = new For(StringList.getNew("???"));
			
//			// START KGU#134 2016-01-08: Bugfix #99
//			final StringList issue99Versions = StringList.explode("3.23-08 3.23-09 3.23-10 3.23-11", " ");
//			// END KGU#134 2016-01-08
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			// START KGU#3 2015-10-29: New attributes for cleaner loop parameter analysis
			int got = 0;
			if(attributes.getIndex("counterVar")!=-1)  {ele.setCounterVar(attributes.getValue("counterVar")); got++;}
			if(attributes.getIndex("startValue")!=-1)  {ele.setStartValue(attributes.getValue("startValue")); got++;}
			// START KGU#134 2016-01-08: Bugfix #99 Test for mis-spelled attribute name (issue99Versions)
			else if (attributes.getIndex("StartValue")!=-1)  {ele.setStartValue(attributes.getValue("StartValue")); got++;}
			// END KGU#134 2016-01-08
			if(attributes.getIndex("endValue")!=-1)  {ele.setEndValue(attributes.getValue("endValue")); got++;}
			if(attributes.getIndex("stepConst")!=-1)  {ele.setStepConst(attributes.getValue("stepConst")); got++;}
			//ele.isConsistent = ele.checkConsistency();
			// END KGU#3 2015-10-29
			
			// START KGU#258 2016-10-04: Enh. #253
			boolean reliable = attributes.getIndex("reliable")!=-1 && attributes.getValue("reliable").equals("true");
			// START KGU#61 2016-03-21: Enh. #84 - Support for FOR-INloops, new attribute
			//ele.isConsistent = (reliable || ele.checkConsistency());
			if (reliable)
			{
				ele.style = For.ForLoopStyle.COUNTER;
				reliable = true;
			}
			else if (attributes.getIndex("style")!=-1)
			{
				String style = attributes.getValue("style");
				try {
					ele.style = For.ForLoopStyle.valueOf(style);
					reliable = true;
				}
				catch (Exception ex)
				{
					System.err.println("Wrong loop style in FOR loop: " + style);
				}
			}
			
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-10-04
			
			// START KGU#3 2015-11-08: Better management of reliability of structured fields
			if (got == 0)	// Seems to be an older diagram file, so try to split the text
			{
				ele.setCounterVar(ele.getCounterVar());
				ele.setStartValue(ele.getStartValue());
				ele.setEndValue(ele.getEndValue());
				ele.setStepConst(ele.getStepConst());
			}
			
			//boolean reliable = attributes.getIndex("reliable")!=-1 && attributes.getValue("reliable").equals("true");
			// START KGU#61 2016-03-21: Enh. #84 - Support for FOR-INloops, new attribute
			//ele.isConsistent = (reliable || ele.checkConsistency());
//			if (reliable)
//			{
//				ele.style = For.ForLoopStyle.COUNTER;
//			}
//			else if (attributes.getIndex("style")!=-1)
//			{
//				String style = attributes.getValue("style");
//				try {
//					ele.style = For.ForLoopStyle.valueOf(style);
//				}
//				catch (Exception ex)
//				{
//					System.err.println("Wrong loop style in FOR loop: " + style);
//				}
//			}
//			else
			if (!reliable)
			{
				// This is now done with the current parser preferences - might fail.
				ele.style = ((For)ele).classifyStyle();
			}
			if (ele.style == For.ForLoopStyle.TRAVERSAL)
			{
				// Now we try to reconstruct the value list.
				// For this we use the post-FOR-IN separator that was valid on saving the file 
				String currentInSep = D7Parser.getKeyword("postForIn");
				if (!savedParserPrefs.containsKey("postForIn") && attributes.getIndex("insep")!=-1)
				{
					// In this case it's unlikely that the FOR-IN separator has already been 
					String inSep = attributes.getValue("insep");
					if (inSep != null && !inSep.trim().isEmpty())
					{
						// Temporarily substitute the postForIn keyword with that from file
						D7Parser.setKeyword("postForIn", inSep);
					}
				}
				try {
					ele.setValueList(ele.splitForClause()[5]);
				}
				catch (Exception ex) {
					System.err.println("Error on reconstructing FOR-IN loop: " + ex.getLocalizedMessage());
				}
				finally {
					// Make sure the current FOR-IN separator does not remain crooked
					D7Parser.setKeyword("postForIn", currentInSep);
				}
			}
			// END KGU#61 2016-03-21
			// END KGU#3 2015-11-08
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
			
			// set children
			ele.q.setColor(ele.getColor());
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("forever"))
		{
			// create element
			Forever ele = new Forever(StringList.getNew("???"));
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
			
			// Enh. #253: In a Forever loop, there isn't anything to refactor

			// set children
			ele.q.setColor(ele.getColor());
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("repeat"))
		{
			// create element
			Repeat ele = new Repeat(StringList.getNew("???"));
			
			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13
			
			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}
					
			// START KGU#258 2016-09-25: Enh. #253
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-09-25
			
			// set children
			ele.q.setColor(ele.getColor());
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
		}
		else if (qualifiedName.equals("case"))
		{
			// create element
			Case ele = new Case(StringList.getNew("???"));

			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text")); /*System.out.println(attributes.getValue("text"));*/}
			ele.qs.clear();
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13

			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}

			// START KGU#258 2016-09-25: Enh. #253
			if (this.refactorKeywords)
			{
				ele.refactorKeywords(savedParserPrefs, ignoreCase);
			}
			// END KGU#258 2016-09-25
			
			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
			cStack.push(ele);
		}
		// --- QUEUES ---
		else if (qualifiedName.equals("qCase"))
		{
			// create new queue
			lastQ = new Subqueue();
			// setup queue
			lastQ.parent = cStack.peek();	// the Case element
			// END KGU 2016-12-21: Bugfix #317
			//lastQ.setColor(lastQ.parent.getColor());
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			// handle stacks
			cStack.peek().qs.addElement(lastQ);
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("parallel"))
		{
			// create element
			Parallel ele = new Parallel(StringList.getNew("1"));

			// read attributes
			if(attributes.getIndex("text")!=-1)  {ele.getText().setCommaText(attributes.getValue("text"));}
			ele.qs.clear();
			if(attributes.getIndex("comment")!=-1)  {ele.getComment().setCommaText(attributes.getValue("comment"));}
			if(attributes.getIndex("color")!=-1)  {if (!attributes.getValue("color").equals("")) {ele.setColor(Color.decode("0x"+attributes.getValue("color")));}}
			// START KGU#277 2016-10-13: Enh. #270
			if(attributes.getIndex("disabled")!=-1)  {ele.disabled = "1".equals(attributes.getValue("disabled"));}
			// END KGU#277 2016-10-13

			// set system attribute - NO!
			// if(attributes.getIndex("comment")!=-1)  {Element.E_SHOWCOMMENTS = Element.E_SHOWCOMMENTS || !attributes.getValue("comment").trim().equals("");}

			// place stack
			lastE=ele;
			stack.push(ele);
			lastQ.addElement(ele);
			pStack.push(ele);
		}
		// --- QUEUES ---
		else if (qualifiedName.equals("qPara"))
		{
			// create new queue
			lastQ = new Subqueue();
			// setup queue
			lastQ.parent=((Parallel) pStack.peek());
			//lastQ.setColor(lastQ.parent.getColor());
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			// handle stacks
			((Parallel) pStack.peek()).qs.addElement(lastQ);
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("children"))
		{
			// handle stacks
			lastQ = root.children;
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("qTrue"))
		{
			// create new queue
			lastQ = ((Alternative) lastE).qTrue;
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			ifStack.push(((Alternative) lastE).qFalse);
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("qFalse"))
		{
			// handle stacks
			lastQ = ifStack.pop();
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("qFor"))
		{
			// handle stacks
			lastQ = ((For) lastE).q;
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("qForever"))
		{
			// handle stacks
			lastQ = ((Forever) lastE).q;
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("qWhile"))
		{
			// handle stacks
			lastQ = ((While) lastE).q;
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			qStack.push(lastQ);
		}
		else if (qualifiedName.equals("qRepeat"))
		{
			// handle stacks
			lastQ = ((Repeat) lastE).q;
			// START KGU 2106-12-21: Bugfix #317
			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals("")) {
				lastQ.setColor(Color.decode("0x"+attributes.getValue("color")));
			}
			// END KGU 2016-12-21
			qStack.push(lastQ);
		}
		// START KGU #2016-12-21: obsolete Bugfix #317 - restore color of empty subqueues
//		else if (qualifiedName.equals("subqueue"))
//		{
//			if (attributes.getIndex("color")!=-1 && !attributes.getValue("color").equals(""))
//			{
//				qStack.peek().setColor(Color.decode("0x"+attributes.getValue("color")));
//			} 
//		}
		// END KGU #2016-12-21
	}
	
    @Override
	public void endElement(String namespaceUri, String localName, String qualifiedName) throws SAXException 
	{
		// --- STRUCTURES ---
		if(qualifiedName.equals("root") ||
		   qualifiedName.equals("call") ||
		   qualifiedName.equals("jump") ||
		   qualifiedName.equals("instruction") ||
		   qualifiedName.equals("while") ||
		   qualifiedName.equals("repeat") ||
		   qualifiedName.equals("forever") ||
		   qualifiedName.equals("for") 
		   )
		{
			lastE = stack.pop();
		}
		else if (qualifiedName.equals("parallel"))
		{
			lastE = stack.pop();
			pStack.pop();
		}
		else if (qualifiedName.equals("case"))
		{
			lastE = stack.pop();
			cStack.pop();
		}
		// -- QUEUES ---
		else if(qualifiedName.equals("qCase") ||
				qualifiedName.equals("qPara") ||
				qualifiedName.equals("qFor") ||
				qualifiedName.equals("qForever") ||
				qualifiedName.equals("qWhile") ||
				qualifiedName.equals("qRepeat") ||
				qualifiedName.equals("qTrue") ||
				qualifiedName.equals("qFalse")
				)
		{
			lastQ = qStack.pop();	// What's this assignment good for
			lastQ = qStack.peek();
		}
		else if(qualifiedName.equals("children"))
		{
			lastQ = qStack.pop();
		}
		else if (qualifiedName.equals("qTrue"))
		{
			lastQ = qStack.pop();
		}
	}
	
    @Override
	public void characters(char[] chars, int startIndex, int endIndex) 
	{
		//String dataString =	new String(chars, startIndex, endIndex).trim();
	}
	
	public Root parse(String _filename) throws SAXException, IOException
	{
		// setup a new root
		root=new Root();
		
		// clear stacks
		stack.clear();
		ifStack.clear();
		qStack.clear();
		cStack.clear();
		pStack.clear();
				
		// START KGU#258 2016-09-26: Enhancement #253
		Ini ini = Ini.getInstance();
		ini.load();
		this.refactorKeywords = ini.getProperty("impRefactorOnLoading","false").equals("true");
		// END KGU#258 2016-09-26

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try		
		{
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(_filename,this);
		} 
		catch(Exception e) 
		{
			String errorMessage = "Error parsing " + _filename + ": " + e;
			System.err.println(errorMessage);
			e.printStackTrace();
			// START KGU#111 2015-12-16: Bugfix #63 re-throw the exception!
			if (e instanceof SAXException)
			{
				throw (SAXException)e;
			}
			else if (e instanceof IOException)
			{
				throw (IOException)e;
			}
			// END KGU#111 2015-12-16
		}
		
		// START KGU#137 2016-01-11: In theory no longer needed - should have been initialized so
		//root.hasChanged=false;
		// END KGU#137 2016-01-11
		
		return root;
	}
	
	// START KGU#177 2016-04-14: Enh. 158 - we need an opportunity to parse an XML string as well
	// (FIXME: This is just a copy-and-paste clone of Root parse(String _filename))
	public Root parse(InputStream _is) throws SAXException, IOException
	{
		// setup a new root
		root=new Root();
		
		// clear stacks
		stack.clear();
		ifStack.clear();
		qStack.clear();
		cStack.clear();
		pStack.clear();
		
		// START KGU#258 2016-09-26: Enh. #253
		Ini ini = Ini.getInstance();
		ini.load();
		refactorKeywords = ini.getProperty("impRefactorOnLoading","false").equals("true");
		// END KGU#258 2016-09-26
				
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try		
		{
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(_is, this);
		} 
		catch(Exception e) 
		{
			String errorMessage = "Error parsing NSD: " + e;
			System.err.println(errorMessage);
			e.printStackTrace();
			// START KGU#111 2015-12-16: Bugfix #63 re-throw the exception!
			if (e instanceof SAXException)
			{
				throw (SAXException)e;
			}
			else if (e instanceof IOException)
			{
				throw (IOException)e;
			}
			// END KGU#111 2015-12-16
		}
		return root;
	}
	// END KGU#177 2016-04-14
	
}
