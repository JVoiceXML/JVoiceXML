/**
 * Classes that facilitate parsing and validation of grammars..
 *
 * <p>
 * {@link org.jvoicexml.GrammarImplementation}s can be validated in two phases:
 * <ol>
 * <li>
 * Parse the {@link org.jvoicexml.GrammarImplementation} using the
 * {@link org.jvoicexml.implementation.grammar.GrammarParser} to retrieve an
 * implementation independent representation of the grammar.
 * </li>
 * <li>
 * Use the {@link org.jvoicexml.implementation.grammar.GrammarChecker} to
 * validate the grammar.
 * </li>
 * </ol>
 * </p>
  */

package org.jvoicexml.implementation.grammar;
