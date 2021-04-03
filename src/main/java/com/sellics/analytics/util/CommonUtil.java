package com.sellics.analytics.util;


import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommonUtil {

    private static final String REG_EXP = "^[a-zA-Z0-9 ]*$";

    public static boolean isStringContainsSpecialChar(final String inputString) {

        Pattern pattern = Pattern.compile(REG_EXP);
        Matcher matcher = pattern.matcher(inputString);

        return !matcher.find();
    }

    public static List<String> getAlphabets() {

        return IntStream.rangeClosed('a', 'z')
                .mapToObj(var -> String.valueOf((char) var))
                .collect(Collectors.toList());
    }

    public static List<Integer> getIntegers(int startIndex, int endIndex) {

        return IntStream.rangeClosed(startIndex, endIndex)
                .boxed()
                .collect(Collectors.toList());
    }

    @Cacheable(value = "getSearchCharList")
    public static List<String> getSearchCharList() {

        List<String> searchKeywordList = CommonUtil.getAlphabets();

        searchKeywordList.addAll(CommonUtil.getIntegers(0, 9)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));

        return searchKeywordList;
    }
}
