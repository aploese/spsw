package de.ibapl.spsw.api;

import java.util.Comparator;

/**
 * "ttyUSB09".compareTo("ttyUSB19") == PortnamesComparator.compare("ttyUSB9",
 * "ttyUSB19")
 * 
 * @author aploese
 *
 */
public class PortnamesComparator implements Comparator<String> {

	class Iterator {
		final String value;
		final int length;
		int pos;
		int stuffedZeros;
		int zerosToFeed;

		public Iterator(String value) {
			this.value = value;
			this.length = value.length();
		}

		/**
		 * 
		 * @return the number of digits from this pos
		 */
		public int getNumberOfDigits() {
			int result = 0;
			while (Character.isDigit(value.charAt(pos + result))) {
				result++;
				if (pos + result == length) {
					break;
				}
			}
			;
			return result;
		}

		public void addZeros(int zerosToFeed) {
			this.zerosToFeed = zerosToFeed;
			this.stuffedZeros += zerosToFeed;
		}

		public char getChar() {
			if (zerosToFeed > 0) {
				return '0';
			} else {
				return value.charAt(pos);
			}
		}

		public boolean next() {
			if (zerosToFeed > 0) {
				zerosToFeed--;
			} else {
				pos++;
			}
			return pos < length;
		}

		public int overallLength() {
			return length + stuffedZeros;
		}

	}

	@Override
	public int compare(String valueA, String valueB) {
		Iterator iterA = new Iterator(valueA);
		Iterator iterB = new Iterator(valueB);
		int fisttStuffedAt = 0;
		int digitsFound = 0;
		while (iterA.next() && iterB.next()) {
			if (digitsFound == 0) {
				int digitsA = iterA.getNumberOfDigits();
				int digitsB = iterB.getNumberOfDigits();
				if (digitsA == digitsB) {
					
				} else {
					if (fisttStuffedAt == 0) {
						fisttStuffedAt = digitsA - digitsB;
					}
				if (digitsA > digitsB) {
					iterB.addZeros(digitsA - digitsB);
					digitsFound = digitsA;
				} else if (digitsA < digitsB) {
					iterA.addZeros(digitsB - digitsA);
					digitsFound = digitsB;
				}
				}
			} else {
				digitsFound--;
			}

			if (iterA.getChar() != iterB.getChar()) {
				return iterA.getChar() - iterB.getChar();
			}
		}
		int lengthDifference = iterA.overallLength() - iterB.overallLength();
		if (lengthDifference == 0) {
			return fisttStuffedAt;
		} else {
		return lengthDifference;
		}
	}
}