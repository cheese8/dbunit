package org.dbunit.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replacements {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Replacements.class);

    private static Map _objectMap = new HashMap();
    private static Map _substringMap = new HashMap();
    private static String _startDelim;
    private static String _endDelim;
    private static boolean _strictReplacement ;

    private static Map<Object, ReplacementFunction> _functionMap = new HashMap();

    private static final String REGEX_FUNCTION = "\\[(.[^\\[\\]{}]*?)\\((.*?)\\]";
    private static final Pattern PATTERN_FUNCTION = Pattern.compile(REGEX_FUNCTION);

    private static final String REGEX_SUB = "\\[.[^\\[\\]{}]*?\\]"; // match [XXX]
    private static final Pattern PATTERN_SUB = Pattern.compile(REGEX_SUB);

    private static final String REGEX_FUN = "\\[(.*?)\\((.*?)\\)\\]"; // match [XXX([XXX])]
    private static final Pattern PATTERN_FUN = Pattern.compile(REGEX_FUN);

    private static final String REGEX_ALL = "(?<=\\\")[^\\\",:]*[\\[\\]\\(\\)](?=\\\")"; // match [XXX] and [XXX([XXX])]
    private static final Pattern PATTERN_ALL = Pattern.compile(REGEX_ALL);

    public static void set(Map objectMap, Map substringMap, Map functionMap) {
        _objectMap = new HashMap();
        _substringMap = new HashMap();
        _functionMap = new HashMap();
        if (objectMap != null) {
            _objectMap = objectMap;
        }
        if (substringMap != null) {
            _substringMap = substringMap;
        }
        if (functionMap != null) {
            _functionMap = functionMap;
        }
        _startDelim = null;
        _endDelim = null;
        _strictReplacement = false;
    }

    /**
     * Setting this property to true indicates that when no replacement
     * is found for a delimited substring the replacement will fail fast.
     *
     * @param strictReplacement true if replacement should be strict
     */
    public static void setStrictReplacement(boolean strictReplacement) {
        if (logger.isDebugEnabled())
            logger.debug("setStrictReplacement(strictReplacement={}) - start", strictReplacement);
        _strictReplacement = strictReplacement;
    }

    /**
     * Add a new Object replacement mapping.
     *
     * @param originalObject    the object to replace
     * @param replacementObject the replacement object
     */
    public static void addReplacementObject(Object originalObject, Object replacementObject) {
        logger.debug("addReplacementObject(originalObject={}, replacementObject={}) - start", originalObject, replacementObject);

        _objectMap.put(originalObject, replacementObject);
    }

    /**
     * Add a new substring replacement mapping.
     *
     * @param originalSubstring    the substring to replace
     * @param replacementSubstring the replacement substring
     */
    public static void addReplacementSubstring(String originalSubstring,
                                        String replacementSubstring) {
        logger.debug("addReplacementSubstring(originalSubstring={}, replacementSubstring={}) - start", originalSubstring, replacementSubstring);

        if (originalSubstring == null || replacementSubstring == null) {
            throw new NullPointerException();
        }

        _substringMap.put(originalSubstring, replacementSubstring);
    }

    /**
     * Add a new function replacement mapping.
     *
     * @param originalObject      the object to replace
     * @param replacementFunction the replacement function
     */
    public static void addReplacementFunction(Object originalObject, ReplacementFunction replacementFunction) {
        logger.debug("addReplacementFunction(originalObject={}, replacementFunction={}) - start", originalObject, replacementFunction);

        _functionMap.put(originalObject, replacementFunction);
    }

    /**
     * Sets substring delimiters.
     */
    public static void setSubstringDelimiters(String startDelimiter, String endDelimiter) {
        logger.debug("setSubstringDelimiters(startDelimiter={}, endDelimiter={}) - start", startDelimiter, endDelimiter);

        if (startDelimiter == null || endDelimiter == null) {
            throw new NullPointerException();
        }

        _startDelim = startDelimiter;
        _endDelim = endDelimiter;
    }

    /**
     * Replace occurrences of source in text with target. Operates directly on text.
     */
    private static void replaceAll(StringBuffer text, String source, String target) {
        int index = 0;
        while ((index = text.toString().indexOf(source, index)) != -1) {
            text.replace(index, index + source.length(), target);
            index += target.length();
        }
    }

    private static String replaceStrings(String value, String lDelim, String rDelim) {
        StringBuffer buffer = new StringBuffer(value);

        for (Iterator it = _substringMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String original = (String) entry.getKey();
            String replacement = (String) entry.getValue();
            replaceAll(buffer, lDelim + original + rDelim, replacement);
        }

        return buffer == null ? value : buffer.toString();
    }

    private static String replaceSubstrings(String value) {
        return replaceStrings(value, "", "");
    }

    /**
     * @throws DataSetException when stringReplacement fails
     */
    private static String replaceDelimitedSubstrings(String value) throws DataSetException {
        StringBuffer buffer = null;

        int startIndex = 0;
        int endIndex = 0;
        int lastEndIndex = 0;
        for (; ; ) {
            startIndex = value.indexOf(_startDelim, lastEndIndex);
            if (startIndex != -1) {
                endIndex = value.indexOf(_endDelim, startIndex + _startDelim.length());
                if (endIndex != -1) {
                    if (buffer == null) {
                        buffer = new StringBuffer();
                    }

                    String substring = value.substring(
                            startIndex + _startDelim.length(), endIndex);
                    if (_substringMap.containsKey(substring)) {
                        buffer.append(value.substring(lastEndIndex, startIndex));
                        buffer.append(_substringMap.get(substring));
                    } else if (_strictReplacement) {
                        throw new DataSetException(
                                "Strict Replacement was set to true, but no"
                                        + " replacement was found for substring '"
                                        + substring + "' in the value '" + value + "'");
                    } else {
                        logger.debug("Did not find a replacement map entry for substring={}. " +
                                "Leaving original value there.", substring);
                        buffer.append(value.substring(
                                lastEndIndex, endIndex + _endDelim.length()));
                    }

                    lastEndIndex = endIndex + _endDelim.length();
                }
            }

            // No more delimited substring
            if (startIndex == -1 || endIndex == -1) {
                if (buffer != null) {
                    buffer.append(value.substring(lastEndIndex));
                }
                break;
            }
        }

        return buffer == null ? value : buffer.toString();
    }

    public static String clean(String str) {
        Matcher MATCHER_ALL = PATTERN_ALL.matcher(str);
        while(MATCHER_ALL.find()) {
            String eachAll = MATCHER_ALL.group();
            Matcher MATCHER_FUN = PATTERN_FUN.matcher(eachAll);
            if (MATCHER_FUN.find()) {
                String funName = MATCHER_FUN.group(1);
                String paramName = MATCHER_FUN.group(2);
                Matcher MATCHER_SUB = PATTERN_SUB.matcher(paramName);
                if (MATCHER_SUB.find()) {
                    paramName = paramName.replace(paramName, (String) _objectMap.get(paramName));
                }
                ReplacementFunction function = _functionMap.get(funName);
                if (function != null) {
                    try {
                        String result = function.evaluate(paramName);
                        str = str.replace(eachAll, result);
                    } catch (Exception e) {
                        throw new RuntimeException("ReplacementFunction "+ funName +" invoke error", e);
                    }
                } else {
                    throw new RuntimeException("ReplacementFunction "+ funName +" was not found");
                }
            }
            Matcher MATCHER_SUB = PATTERN_SUB.matcher(eachAll);
            if (MATCHER_SUB.find()) {
                str = str.replace(eachAll, (String) _objectMap.get(eachAll));
            }
        }
        return str;
    }

    public static void main(String[] args) {
        String json = "{\"code\":\"\", \"data\":[{\"cardNo\":\"[CARDNO]\", {\"encryptCardNo\":\"[encrypt([CARDNO])]\"}]}";
        Matcher matcher = PATTERN_ALL.matcher(json);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    public static Object getValue(Object value) throws DataSetException {
        // Object replacement
        if (_objectMap.containsKey(value)) {
            return _objectMap.get(value);
        }

        if (value instanceof String) {
            // Function replacement
            String valueStr = (String) value;
            Matcher M_FUNCTION = PATTERN_FUNCTION.matcher(valueStr);
            if (M_FUNCTION.find()) {
                String functionName = M_FUNCTION.group(1);
                String parameter = M_FUNCTION.group(2);
                if (_functionMap.containsKey(functionName)) {
                    return _functionMap.get(functionName).evaluate(parameter);
                } else {
                    throw new DataSetException("ReplacementFunction " + functionName + " was not registered.");
                }
            } else {
                // Substring replacement
                if (_startDelim != null && _endDelim != null) {
                    return replaceDelimitedSubstrings(valueStr);
                }
                return replaceSubstrings((String) value);
            }
        }
        // Stop here if substring replacement not applicable
        return value;
    }

    private Replacements() {}
}