package io.wispforest.common.util;

import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Credit to <a href="https://github.com/SingingBush">SingingBush</a> for creating this class for Singular / Pluralization
 *
 * Source: <a href="https://github.com/atteo/evo-inflector/issues/6#issuecomment-64907255">Click Here</>
 *
 * Warning: Unknown if it works correctly
 */
public class WordMagic {

    public static WordMagic INSTANCE = new WordMagic();

    private static final List<String> UNCOUNTABLES = Arrays.asList("equipment", "information", "rice", "money", "species", "series", "fish", "sheep");

    private final LinkedList<Rule> _singulars = new LinkedList<>();

    public WordMagic() {
        addSingularizeRules();
    }

    /**
     * For a given word, return either the singular or the plural version
     * @param word the term that needs checking
     * @return either the singular or the plural version
     */
    public String calculateSingularOrPlural(final String word) {
        if (isUncountable(word)) return word;

        for (final Rule rule : _singulars) {
            final String result = rule.apply(word);
            if (result != null) return result;
        }
        // if no singular was found we'll assume that the word is already singular and can be safely pluralised.
        // English.plural() will always pluralise, even if it's already plural!!!
        return English.plural(word.trim());
    }


    private void addSingularize(final String rule, final String replacement) {
        final Rule singularizeRule = new Rule(rule, replacement);
        _singulars.addFirst(singularizeRule);
    }

    private void addSingularizeRules() {
        addSingularize("s$", "");
        addSingularize("(s|si|u)s$", "$1s"); // '-us' and '-ss' are already singular
        addSingularize("(n)ews$", "$1ews");
        addSingularize("([ti])a$", "$1um");
        addSingularize("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        addSingularize("(^analy)ses$", "$1sis");
        addSingularize("(^analy)sis$", "$1sis"); // already singular, but ends in 's'
        addSingularize("([^f])ves$", "$1fe");
        addSingularize("(hive)s$", "$1");
        addSingularize("(tive)s$", "$1");
        addSingularize("([lr])ves$", "$1f");
        addSingularize("([^aeiouy]|qu)ies$", "$1y");
        addSingularize("(s)eries$", "$1eries");
        addSingularize("(m)ovies$", "$1ovie");
        addSingularize("(x|ch|ss|sh)es$", "$1");
        addSingularize("([m|l])ice$", "$1ouse");
        addSingularize("(bus)es$", "$1");
        addSingularize("(o)es$", "$1");
        addSingularize("(shoe)s$", "$1");
        addSingularize("(cris|ax|test)is$", "$1is"); // already singular, but ends in 's'
        addSingularize("(cris|ax|test)es$", "$1is");
        addSingularize("(octop|vir)i$", "$1us");
        addSingularize("(octop|vir)us$", "$1us"); // already singular, but ends in 's'
        addSingularize("(alias|status)es$", "$1");
        addSingularize("(alias|status)$", "$1"); // already singular, but ends in 's'
        addSingularize("^(ox)en", "$1");
        addSingularize("(vert|ind)ices$", "$1ex");
        addSingularize("(matr)ices$", "$1ix");
        addSingularize("(quiz)zes$", "$1");
    }

    private boolean isUncountable(final String word) {
        return !StringUtils.isEmpty(word) && UNCOUNTABLES.contains(word.trim().toLowerCase());
    }

    private static class Rule {
        private final String _expression;
        private final Pattern _expressionPattern;
        private final String _replacement;

        protected Rule(final String expression, final String replacement) {
            _expression = expression;
            _replacement = replacement != null ? replacement : "";
            _expressionPattern = Pattern.compile(_expression, Pattern.CASE_INSENSITIVE);
        }

        /**
         * Apply the rule against the input string, returning the modified string or null if the rule didn't apply (and no
         * modifications were made)
         *
         * @param input the input string
         * @return the modified string if this rule applied, or null if the input was not modified by this rule
         */
        protected String apply(final String input) {
            final Matcher matcher = _expressionPattern.matcher(input);
            return matcher.find() ? matcher.replaceAll(_replacement) : null;
        }
    }

}
