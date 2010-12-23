package moten.david.util.text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StringUtil {

	/**
	 * Reads an InputStream completely to return a string
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readString(InputStream is) throws IOException {
		StringBuffer s = new StringBuffer();
		BufferedInputStream bis = new BufferedInputStream(is);
		int ch;
		while ((ch = bis.read()) != -1) {
			s.append((char) ch);
		}
		bis.close();
		return s.toString();
	}

	public static String readString(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String s = readString(fis);
		fis.close();
		return s;
	}

	public static void removeCarriageReturns(InputStream is, OutputStream os)
			throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			line = line.replace(((char) 13) + "", "");
			os.write(line.getBytes());
			os.write("\n".getBytes());
		}
		br.close();
		isr.close();
	}

	/**
	 * Converts a string to upper underscore case (Ex. "justASimple_String"
	 * becomes "JUST_A_SIMPLE_STRING"). This function is not idempotent (Eg.
	 * "abc" => "ABC" => "A_B_C").
	 * 
	 * @param value
	 * @return the upper underscore case string
	 */
	public static String toUpperUnderscoreCase(String value) {
		return toUnderscoreCaseHelper(value, true);
	}

	/**
	 * Converts a string to lower underscore case (Ex. "justASimple_String"
	 * becomes "just_a_simple_string"). This function is idempotent.
	 * 
	 * @param value
	 * @return the upper underscore case string
	 */
	public static String toLowerUnderscoreCase(String value) {
		return toUnderscoreCaseHelper(value, false);
	}

	/**
	 * Converts a string to lower came case (Ex. "justASimple_String" becomes
	 * "JustASimpleString"). This function is idempotent.
	 * 
	 * @param value
	 * @return the upper underscore case string
	 */
	public static String toUpperCamelCase(String value) {
		return toCamelCaseHelper(value, true);
	}

	/**
	 * Converts a string to lower came case (Ex. "justASimple_String" becomes
	 * "justASimpleString"). This function is idempotent.
	 * 
	 * @param value
	 * @return the upper underscore case string
	 */
	public static String toLowerCamelCase(String value) {
		return toCamelCaseHelper(value, false);
	}

	/**
	 * Helper function for underscore case functions.
	 * 
	 * @param value
	 * @param upperCase
	 * @return
	 */
	private static String toUnderscoreCaseHelper(String value, boolean upperCase) {
		if (value == null) {
			return null;
		}
		// 10% percent increase estimation, minimum 8
		int estimatedSize = value.length() * 11 / 10;
		if (value.length() < 8) {
			estimatedSize = 8;
		}
		StringBuilder result = new StringBuilder(estimatedSize);
		boolean underscoreWritten = true;
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if ((ch >= 'A') && (ch <= 'Z')) {
				if (!underscoreWritten) {
					result.append('_');
				}

			}
			result.append((upperCase) ? CharUtil.toAsciiUpperCase(ch)
					: CharUtil.toAsciiLowerCase(ch));
			underscoreWritten = (ch == '_');
		}
		return result.toString();
	}

	/**
	 * Helper function for camel case functions.
	 * 
	 * @param value
	 * @param upperCase
	 * @return
	 */
	private static String toCamelCaseHelper(String value, boolean upperCase) {
		if (value == null) {
			return null;
		}
		if (value.length() == 0) {
			return "";
		}
		char firstChar = value.charAt(0);
		char firstCharCorrected = (upperCase) ? CharUtil
				.toAsciiUpperCase(firstChar) : CharUtil
				.toAsciiLowerCase(firstChar);
		if (value.indexOf('_') == -1) {
			if (firstChar != firstCharCorrected) {
				return firstCharCorrected + value.substring(1);
			} else {
				return value;
			}
		}
		StringBuilder result = new StringBuilder(value.length());
		result.append(firstCharCorrected);
		boolean nextIsUpperCase = false;
		for (int i = 1; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (ch == '_') {
				nextIsUpperCase = true;
			} else {
				if (nextIsUpperCase) {
					result.append(CharUtil.toAsciiUpperCase(ch));
					nextIsUpperCase = false;
				} else {
					result.append(ch);
				}
			}
		}
		return result.toString();
	}

}
