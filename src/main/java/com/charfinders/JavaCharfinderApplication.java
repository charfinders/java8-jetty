package com.charfinders;

import static spark.Spark.get;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import spark.Request;

public class JavaCharfinderApplication {

	public static void main(String[] args) throws Exception {
		IndexBuilder indexBuilder = new IndexBuilder(UnicodeLoader.loadCharTable());
		Map<String, Set<String>> invertedIndex = indexBuilder.invertedIndex();
		Map<String, String> nameIndex = indexBuilder.nameIndex();

		get("/find_chars", (req, res) -> {
			return intersection(findCodes(invertedIndex, req))
								.stream()
								.map(codeEntity(nameIndex))
								.collect(Collectors.toList());
		});
	}

	private static Function<? super String, ? extends Map<String, String>> codeEntity(Map<String, String> nameIndex) {
		return code -> {
			Map<String, String> response = new HashMap<>(3);
			response.put("code_point", code);
			response.put("name", nameIndex.get(code));
			response.put("character", codeToSym(code));
			return response;
		};
	}

	private static List<Set<String>> findCodes(Map<String, Set<String>> invertedIndex, Request req) {
		return split(req.queryParams("q"), " ")
				.parallelStream()
				.map(p -> invertedIndex.get(p))
				.collect(Collectors.toList());
	}

	private static Set<String> intersection(List<Set<String>> sets) {
		Set<String> intersection = sets.get(0);
		for (int i = 1; i < sets.size(); i++) {
			intersection.retainAll(sets.get(i));
		}
		return intersection;
	}

	private static String codeToSym(String codeAsString) {
		int code = Integer.valueOf(codeAsString, 16).intValue();
		return String.valueOf(Character.toChars(code));
	}

	private static List<String> split(String s, String delimiter) {
		return Arrays.asList(s.split(delimiter));
	}
}