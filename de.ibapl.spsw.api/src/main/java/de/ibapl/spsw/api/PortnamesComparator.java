package de.ibapl.spsw.api;

import java.util.Comparator;

public class PortnamesComparator implements Comparator<String> {

	@Override
	public int compare(String valueA, String valueB) {

		if (valueA.equalsIgnoreCase(valueB)) {
			return valueA.compareTo(valueB);
		}

		int minLength = Math.min(valueA.length(), valueB.length());

		int shiftA = 0;
		int shiftB = 0;

		for (int i = 0; i < minLength; i++) {
			char charA = valueA.charAt(i - shiftA);
			char charB = valueB.charAt(i - shiftB);
			if (charA != charB) {
				if (Character.isDigit(charA) && Character.isDigit(charB)) {
					int[] resultsA = getNumberAndLastIndex(valueA, i - shiftA);
					int[] resultsB = getNumberAndLastIndex(valueB, i - shiftB);

					if (resultsA[0] != resultsB[0]) {
						return resultsA[0] - resultsB[0];
					}

					if (valueA.length() < valueB.length()) {
						i = resultsA[1];
						shiftB = resultsA[1] - resultsB[1];
					} else {
						i = resultsB[1];
						shiftA = resultsB[1] - resultsA[1];
					}
				} else {
					if (Character.toLowerCase(charA) - Character.toLowerCase(charB) != 0) {
						return Character.toLowerCase(charA) - Character.toLowerCase(charB);
					}
				}
			}
		}
		return valueA.compareToIgnoreCase(valueB);
	}

	/**
	 * Evaluate port <b>index/number</b> from <b>startIndex</b> to the number end.
	 * For example: for port name <b>serial-123-FF</b> you should invoke this method
	 * with <b>startIndex = 7</b>
	 *
	 * @return If port <b>index/number</b> correctly evaluated it value will be
	 *         returned<br>
	 *         <b>returnArray[0] = index/number</b><br>
	 *         <b>returnArray[1] = stopIndex</b><br>
	 *
	 *         If incorrect:<br>
	 *         <b>returnArray[0] = -1</b><br>
	 *         <b>returnArray[1] = startIndex</b><br>
	 *
	 *         For this name <b>serial-123-FF</b> result is: <b>returnArray[0] =
	 *         123</b><br>
	 *         <b>returnArray[1] = 10</b><br>
	 */
	private int[] getNumberAndLastIndex(String str, int startIndex) {
		String numberValue = "";
		int[] returnValues = { -1, startIndex };
		for (int i = startIndex; i < str.length(); i++) {
			returnValues[1] = i;
			char c = str.charAt(i);
			if (Character.isDigit(c)) {
				numberValue += c;
			} else {
				break;
			}
		}
		try {
			returnValues[0] = Integer.valueOf(numberValue);
		} catch (Exception ex) {
			// Do nothing
		}
		return returnValues;
	}
}