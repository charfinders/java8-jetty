package com.charfinders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IndexBuilder {
	
	private static final String LINE_DELIMITER = "\\n";

	private static final String WORD_DELIMITER = "-|\\s";

	private static final String INVALID_LINE_REGEX = "^[0-9a-fA-F]{4,6};<.*>.*";

	private static final String CELL_DELIMITER = ";";

	private List<List<String>> lines;
	
	public IndexBuilder(String unicodeTable){
		lines = charLines(unicodeTable);
	}

	public Map<String, Set<String>> invertedIndex() {
		Map<String, Set<String>> index = new HashMap<>();
		lines.forEach(line -> {
			line.subList(1, line.size())
				.forEach(fullword -> 
					split(fullword, WORD_DELIMITER)
					.forEach(word -> {
								Set<String> ids = index.getOrDefault(word, new HashSet<String>());
								ids.add(line.get(0));
								index.put(word, ids);
							}));
		});

		return index;
	}
	
	public Map<String, String> nameIndex() {
		return lines.stream().collect(Collectors.toMap(line -> line.get(0), line -> line.get(1)));
	}

	private List<List<String>> charLines(String textTable) {
		return split(textTable, LINE_DELIMITER)
				.parallelStream()
				.filter(line -> !line.matches(INVALID_LINE_REGEX))
				.map(line -> cutFirst2Cells(line))
				.map(line -> split(line, CELL_DELIMITER))
				.collect(Collectors.toList());
	}

	private static String cutFirst2Cells(String textTable) {
		return textTable.substring(0, textTable.indexOf(CELL_DELIMITER, textTable.indexOf(CELL_DELIMITER) + 1));
	}

	private static List<String> split(String s, String delimiter) {
		return Arrays.asList(s.split(delimiter));
	}

}
