package org.mikita.leetcode.hard.p3739;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Arrays;

class SolutionTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/p3739.csv", numLinesToSkip = 1, maxCharsPerColumn = 100000)
    void countMajoritySubarrays(@ConvertWith(BracketedIntArrayConverter.class) int[] nums,
                                int target, long expectedResult) {
        // given
        Solution solution = new Solution();

        // when
        long actual = solution.countMajoritySubarrays(nums, target);

        // then
        Assertions.assertEquals(expectedResult, actual);
    }

    static class BracketedIntArrayConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) {
            if (source instanceof String && targetType == int[].class) {
                String str = (String) source;

                String cleaned = str.replace(" ", "")
                        .replace("[", "")
                        .replace("]", "");

                if (cleaned.isEmpty()) {
                    return new int[0];
                }

                return Arrays.stream(cleaned.split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray();
            }
            throw new IllegalArgumentException("Conversion failed for input: " + source);
        }
    }
}
