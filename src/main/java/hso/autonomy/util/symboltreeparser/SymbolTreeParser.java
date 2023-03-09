/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.symboltreeparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Parses an s-expression string into a Tree consisting of SymbolNode and String objects.
 *
 * @author Simon Raffeiner
 */
public class SymbolTreeParser
{
	/**
	 * Parse an s-expression string into a Symbol tree.
	 *
	 * @param input String to parse
	 * @return Generated Symbol tree
	 * @throws IllegalSymbolInputException if illegal symbols are found in the
	 *         input stream
	 */
	public List<Object> parse(String input) throws IllegalSymbolInputException
	{
		/* Check input rules */
		if (input == null || input.length() == 0)
			throw new IllegalSymbolInputException("Empty string");

		if (input.charAt(0) != '(' || input.charAt(input.length() - 1) != ')')
			throw new IllegalSymbolInputException("Input not embedded in braces: " + input);

		return parseReal(input);
	}

	/**
	 * Parse a string into a symbol tree. This routine fetches the
	 * top-level-tokens from the string, converts them to SymbolLeaf entries and
	 * adds them to the actual node. If a list is found the complete content
	 * (regardless how many levels of symbols and sub-lists are found inside the
	 * list)between the highest-level opening and closing braces is parsed
	 * recursively.
	 *
	 * The following example shows how the tree is formed:
	 *
	 * Input: (A (B (C C C)) A)
	 *
	 * 1. Token A is added 2. The function calls itself on the list "(B (C C C))"
	 * 3. Token B is added 4. The function calls itself on the list "(C C C)" 5.
	 * The three "C" tokens are added 6. The function returns from the recursive
	 * calls 7. Token "A" is added
	 *
	 * @param string String to parse
	 * @return Generated Symbol tree
	 * @throws IllegalSymbolInputException if illegal symbols are found in the
	 *         input stream
	 */
	private static List<Object> parseReal(String string) throws IllegalSymbolInputException
	{
		Stack<List<Object>> stack = new Stack<>();
		List<Object> currentNode = new ArrayList<>(50);

		/* Current character index */
		int index = 0;

		// index to start string copy
		int startIndex = 0;

		// Repeat until the input string is completely parsed
		while (index < string.length()) {
			/* If we are on a deeper level just add the characters */
			char character = string.charAt(index);
			switch (character) {
			case '(':
				// Descending into a deeper level.
				startIndex = index + 1;
				stack.push(currentNode);
				currentNode = new ArrayList<>(5);
				break;

			case ')':
				// Returning from a deeper level.
				if (stack.isEmpty()) {
					throw new IllegalSymbolInputException("Missing brackets in input: " + string);
				}

				if (index > startIndex) {
					currentNode.add(string.substring(startIndex, index));
				}
				startIndex = index + 1;
				List<Object> parent = stack.pop();
				parent.add(currentNode);
				currentNode = parent;
				break;

			case ' ':
				// space separates string elements
				if (index > startIndex) {
					currentNode.add(string.substring(startIndex, index));
				}
				startIndex = index + 1;
				break;
			}

			index++;
		}

		if (!stack.isEmpty()) {
			throw new IllegalSymbolInputException("Missing brackets in input: " + string);
		}
		return currentNode;
	}
}
