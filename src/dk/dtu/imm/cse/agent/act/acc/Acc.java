// Copyright 2002 Henrik Lauritzen.
/*
    This file is part of the AgentC Toolkit.

    The AgentC Toolkit is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The AgentC Toolkit is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the AgentC Toolkit.  If not, see <http://www.gnu.org/licenses/>.
*/
package dk.dtu.imm.cse.agent.act.acc;

import java.io.*;
import java.util.*;

import dk.dtu.imm.cse.agent.act.afc.*;


/**
 * This is the AgentC compiler implementation. The compiler takes 
 *  an AgentC specification as input and produces as output the 
 *  source code for a Java class implementing an {@link Acme ACME}
 *
 * @author  Henrik Lauritzen
 */
public class Acc implements AccConstants {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Instance fields
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Constructors
	// =======================================================================
		
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	private Acc() {
	}
	
	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * Compiles an AgentC program given as an input stream into a
	 *  StringBuffer instance containing the translated program code.
	 * @param in the input stream from which to obtain the source code
	 * @param pack the package name of the generated class. If the value
	 *  is null, no package statement will be generated.
	 * @param name the class name of the generated class.
	 * @param superclass the class which the generated class should extend.
	 * @param ifs a collection of the names of the interfaces to be 
	 *   implemented by the generated class
	 * @return a StringBuffer instance containing source code for the 
	 *  Java class resulting from the compilation.
	 * @exception ParseException if an error occurs during compilation.
	 */
	public static StringBuffer compile(InputStream in, String pack, 
			String name, String superclass, Collection ifs) throws ParseException {
		// create the parser
		AgentCparser p = new AgentCparser(in);
		ParseTree tree = new ParseTree();

		// include inherited defs and procedures from the superclass
		if (superclass != null) {			
			tree.setSuperclass(superclass);
		}
		// include initial declarations specified by the interfaces to
		// be implemented
		if (ifs != null) {
			for (Iterator i = ifs.iterator(); i.hasNext(); ) {
				tree.addInterfaceDecl((String)i.next());
			}			
		}
				
		// first pass: parse the program
		p.program(tree);
			
		// second pass: verify (+classify) constants and variables
		checkSemantics(tree);
		
		// third pass: generate code		
		StringBuffer result = generateCode(pack, name, superclass, ifs, tree);
					
		return result;
	}
		
	
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Class methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Invokes the ACC from the command line. The command line syntax is
	 *  the following:
	 * <ul>
	 * <li><code>-o <em>output<em></code> saves the result in the file named
	 *  <code><em>output</em></code>. By default, the compiler writes
	 *  to {@link System#out}.
	 * <li><code>-i <em>input</em></code> specifies from which file input
	 *  should be read. By default, the compiler reads from
	 *  {@link System#in}.
	 * <li><code>-cls <em>class</em></code> specifies the class name of the
	 *  generated code. By default, the name "AccOutput" will be used.
	 *   However, if an output specification having the suffix ".java" is
	 *   given, the class name is taken from this specification.
	 * <li><code>-pck <em>package</em></code> specifies the package name
	 *  to be used in the generated code. By default, no package declaration
	 *  will be generated.
	 *  However, if an output specification having the suffix ".java" and
	 *  a path prefix is given, that prefix will be used to set the package name.
	 * </ul>
	 */
	public static void main(String[] args) {
		List inputs = new ArrayList();
		InputStream in = System.in;
		String outName = null;
		PrintStream out = System.out;

		String cls = "AccOutput";
		String pck = null;
		String ext = null;
		List interfaces = new ArrayList();
		
		Acc acc = new Acc();
		
		try {
			// decode the arguments
			try {
				int i = 0;
				for (; i < args.length; i++) {
					if ("-o".equals(args[i])) {
						outName = args[++i];
						if (outName.endsWith(".java")) {
							int idx = outName.lastIndexOf(File.separatorChar);
							cls = outName.substring(idx < 0 ? 0 : idx + 1, 
									outName.length() - 5);
							if (idx >= 0) {
								String p = outName.substring(0, idx);
								pck = p.replace(File.separatorChar, '.');
							}
						}
					}
					else if ("-cls".equals(args[i])) {
						cls = args[++i];
					}
					else if ("-pck".equals(args[i])) {
						pck = args[++i];
					}
					else if ("-impl".equals(args[i])) {
						interfaces.add(args[++i]);
					}
					else if ("-ext".equals(args[i])) {
						ext = args[++i];
					}
					else if (args[i].charAt(0) != '-') {
						inputs.add(args[i]);
					}
					else {
						// force the help message to be printed.
						// this will also be the case if a required argument
						// is missing
						throw new IndexOutOfBoundsException();
					}
				}				
			}
			catch (IndexOutOfBoundsException e) {
				System.err.println(makeHelp());
				return;
			}

			if (inputs.size() > 0) {
				InputStream in1 = null, in2 = null;
				
				try {
					for (int i = inputs.size() - 1; i >= 0; i--) {
						in2 = new BufferedInputStream(new FileInputStream(
								(String)inputs.get(i)));
						in1 = in1 == null ? in2 : 
								new SequenceInputStream(in2, in1);
						in2 = null;
					}
					
					in = in1;
				}
				catch (IOException e) {
					e.printStackTrace();
					
					if (in1 != null) {
						in1.close();
					}
					if (in2 != null) {
						in2.close();
					}
					return;
				}
			}

			StringBuffer result = acc.compile(in, pck, cls, ext, interfaces);
			
			if (outName != null) {
				out = new PrintStream(new FileOutputStream(outName, false));
			}
			out.print(result.toString());			
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		finally {
			if (in != System.in) {
				try {
					in.close();
				}
				catch (Throwable t) {
				}
			}
			if (out != System.out) {
				try {
					out.close();
				}
				catch (Throwable t) {
				}
			}
		}
	}
	
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
		
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------

	// check the variables and constants in the abstract syntax
	private static void checkSemantics(ParseTree tree) throws ParseException {
		// check the initial facts
		List facts = tree.getFacts();
		for (int i = 0, max = facts.size(); i < max; i++) {
			((Sentence)facts.get(i)).checkSymbols(tree,
					Collections.EMPTY_MAP, AbstractSyntax.SCOPE_NOVARS);
		}
		
		// check every procedure in a new scope
		for (Iterator it = tree.getProcedures().entrySet().iterator(); 
				it.hasNext(); ) {
			Map.Entry e = (Map.Entry)it.next();
			ParseTree.ProcedureSignature sig = (ParseTree.ProcedureSignature)e.getKey();
			if (tree.isExternalProcedure(sig)) {
				// ignore procedures which have been inherited
				continue;
			}

			Map scope = sig.checkSymbols();
			List rules = (List)e.getValue();
			if (rules != null) {		
				for (int i = 0, max = rules.size(); i < max; i++) {
					((AbstractSyntax)rules.get(i)).checkSymbols(tree, scope, 
							AbstractSyntax.SCOPE_DEFUSE);
				}
			}
			else {
				// illegal empty forward declaration
				throw new ParseException("No body defined for procedure \"" + 
					e.getKey() + "\"");
			}
		}
	}
		

	// generate code for the program
	private static StringBuffer generateCode(String pck, String cls, 
			String superclass, Collection ifs, ParseTree tree) {
		// generate the file header
		StringBuffer b = new StringBuffer();
		b.append("/*\n * @(#)").append(cls).append(".java\t");
		b.append(java.text.DateFormat.getDateTimeInstance().format(new Date())).
				append("\n *\n").append(" */\n");
		if (pck != null) {
			b.append("package ").append(pck).append(";\n\n");
		}
		
		// generate a collection of the packages which are implicitly or
		// explicitly imported
		List imports = new ArrayList();
		imports.add("java.lang");
		imports.add("java.util");
		imports.add("dk.dtu.imm.cse.agent.act.afc");
		imports.add("dk.dtu.imm.cse.agent.act.util");
		if (pck != null) {
			imports.add(pck);
		}

		// make code for the necessary imports
		for (Iterator i = imports.iterator(); i.hasNext(); ) {
			String next = (String)i.next();
			if (next == pck || next == "java.lang") {
				continue;
			}
			b.append("import ").append(next).append(".*;\n");
		}
		b.append("\n\n");

		b.append("/**\n * This file was generated by automatically by ").
				append("the AgentC Compiler.\n");
		b.append(" *\n * (c) Henrik Lauritzen, 2002\n */\n");
		b.append("public class ").append(cls).append(" extends ");
		b.append(superclass == null ? "Acme" : makeClassName(superclass, imports));
		
		int ifCount = ifs.size();
		if (ifCount > 0) {
			b.append(" implements ");
			for (Iterator i = ifs.iterator(); ifCount > 0; ) {
				b.append(makeClassName((String)i.next(), imports));
				if (--ifCount <= 0) {
					break;
				}
				else {
					b.append(", ");
				}
			}
		}		
		b.append(" {\n\n");	
		
		// generate code for the constant declarations
		for (Iterator i = tree.getDefs().entrySet().iterator(); 
				i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();			
			String name = (String)e.getKey();
			if (tree.isExternalDef(name)) {
				// don't generate code for constants inherited from interfaces
				continue;
			}
			Literal l = (Literal)e.getValue();
			
			b.append("\tpublic static final ").append(l.getJavaType());
			b.append(' ').append(java.text.MessageFormat.format(
					CONST_PATTERN, new String[] { name })).append(" = ");
			b.append(l.toJavaExpression()).append(";\n");
		}

		// generate the constructors
		b.append("\n\n\tpublic ").append(cls).append("(Object id) {\n");
		b.append("\t\tsuper(id);\n");
		b.append("\t}\n");
		b.append("\n\tpublic ").append(cls);
		b.append("(Object id, Actuator a, Messenger m, Investigator i) {\n");
		b.append("\t\tsuper(id);\n");
		b.append("\t\tinit(a, m, i);\n");
		b.append("\t}\n\n\n");
				
		// generate code for the initial facts
		b.append("\tprotected void initKnowledgeBase(KnowledgeBase kb) {\n");
		for (Iterator i = tree.getFacts().iterator(); i.hasNext(); ) {
			b.append("\t\tkb.add(");
			Sentence a = (Sentence)i.next();
			
			b.append(a.getAttitude()).append(", ").
					append(AccUtils.encodeStringLiteral(a.getName())).append(", ");
			AccUtils.translateTerms(b, a.getTerms(), null);
			b.append(");\n");
		}
		b.append("\t}\n\n");

		// generate code corresponding to the attitude definitions
		int maxAttitude = 0;
		Map m = tree.getAttitudes();
		for (Iterator i = m.values().iterator(); i.hasNext(); ) {
			int id = ((Integer)i.next()).intValue();
			if (id > maxAttitude) {
				maxAttitude = id;
			}
		}
		b.append("\tpublic int getMaxAttitude() {\n");
		b.append("\t\treturn ").append(maxAttitude).append(";\n\t}\n\n");

		// generate the main program code
		Map procs = tree.getProcedures();
		for (Iterator it = procs.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry e = (Map.Entry)it.next();
			ParseTree.ProcedureSignature sig = (ParseTree.ProcedureSignature)e.getKey();
			if (tree.isExternalProcedure(sig)) {
				// don't generate code for procedures which have been inherited
				continue;
			}
			
			b.append("\tpublic Object ").append(sig.getName()).append("(Map ").
					append(MESSAGE_MAP_NAME);
			sig.addSignature(", ", b);
			b.append(") {\n");
			
			// insert local variable definitions at this point
			int variableOffset = b.length();
			
			
			// generate code for the procedure
			List block = (List)e.getValue();
			int bsize = block.size();
			for (int i = 0; i < bsize; i++) {
				((Statement)block.get(i)).generateCode(2, b);
			}			
			if (bsize == 0 || !(block.get(bsize - 1) instanceof ReturnStatement)) {
				b.append("\t\treturn null;\n");
			}
			b.append("\t}\n\n");

			// insert the necessary local variable declarations at the
			// beginning of the procedure
			String s = b.substring(variableOffset);
			if (s.indexOf(TEMP_FACT_NAME) >= 0) {
				b.insert(variableOffset, "\t\tFact " + TEMP_FACT_NAME + ";\n");
			}
			if (s.indexOf(TEMP_MAP_NAME) >= 0) {
				b.insert(variableOffset, "\t\tMap " + TEMP_MAP_NAME + ";\n");
			}
			if (s.indexOf(TEMP_OBJ_NAME) >= 0) {
				b.insert(variableOffset, "\t\tObject " + TEMP_OBJ_NAME + ";\n");
			}
			if (s.indexOf(TEMP_KB_NAME) >= 0) {
				b.insert(variableOffset, "\t\tKnowledgeBase " + 
						TEMP_KB_NAME + " = getKnowledgeBase();\n");
			}
			if (s.indexOf(TEMP_BS_NAME) >= 0) {
				b.insert(variableOffset, "\t\tBitSet " + TEMP_BS_NAME + 
						" = new BitSet();\n");
			}
		}

		
		// done!
		b.append("}");
		return b;
	}
	
	
	// make a class name, removing an already imported package
	private static String makeClassName(String name, Collection imports) {
		int idx = name.lastIndexOf('.');
		if (idx < 0 || !imports.contains(name.substring(0, idx))) {
			return name;
		}
		else {
			return name.substring(idx + 1);
		}
	}

	
	// create the help message
	private static StringBuffer makeHelp() {
		StringBuffer buf = new StringBuffer("Usage: acc [-options] [inputs]\n\n");
		buf.append("with the following possible options:\n\n");
		buf.append("\t-o <output>\twrites the output class to the path <output>\n");
		buf.append("\t-cls <class>\tmakes <class> the class name of the output class\n");
		buf.append("\t-pck <package>\tmakes <package> the package of the output class\n");
		buf.append("\t-impl <intf>\tmakes the output class implement <intf>\n");
		return buf.append("\t-ext <class>\tmakes the output class extend <class>");
	}
	
	// ------------------------------ instance -----------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
