import org.junit.jupiter.api.Assertions;

import java.util.HashMap;

class FMIndexTest {

    String text = Character.MIN_VALUE + "alabaralalabarda";

    char[] pattern = "la".toCharArray();

    FMIndex fmIndex = new FMIndex(text, 64);

    @org.junit.jupiter.api.Test
    void computeC() {

        HashMap<Character, Integer> expectedHashMap = new HashMap<>();

        expectedHashMap.put(Character.MIN_VALUE, 0);
        expectedHashMap.put('a' , 1);
        expectedHashMap.put('b', 9);
        expectedHashMap.put('d', 11);
        expectedHashMap.put('l', 12);
        expectedHashMap.put('r', 15);

        Assertions.assertEquals(expectedHashMap, FMIndex.computeC(fmIndex.getBwtOfText()));

    }

    @org.junit.jupiter.api.Test
    void rank() {

        Assertions.assertEquals(2, fmIndex.rank('r', fmIndex.getBwtOfText(), 12));

    }

    @org.junit.jupiter.api.Test
    void getRange() {

        Assertions.assertArrayEquals(new int[]{13, 15}, fmIndex.getRange(pattern));

    }

    @org.junit.jupiter.api.Test
    void nextGreatestAlphabet() {

        Assertions.assertEquals( 'd', FMIndex.nextGreatestAlphabet(fmIndex.getCharacters(), 'b'));

    }

    @org.junit.jupiter.api.Test
    void locate() {

        Assertions.assertArrayEquals(new int[]{2, 10, 8}, fmIndex.locate(pattern));

    }

    @org.junit.jupiter.api.Test
    void getBwtOfText() {

        char[] expectedArrayBWT = ("adll" + Character.MIN_VALUE + "lrbbaaraaaaa").toCharArray();

        Assertions.assertArrayEquals(expectedArrayBWT, fmIndex.getBwtOfText());

    }

    @org.junit.jupiter.api.Test
    void getRankInitial() {

        int[][] expectedArrayInitial = new int[][]{
                {0, 1, 0, 0, 0, 0}
        };

        Assertions.assertArrayEquals(expectedArrayInitial, fmIndex.getRankInitial());

    }

    @org.junit.jupiter.api.Test
    void getC() {

        Assertions.assertArrayEquals(new int[]{0, 1, 9, 11, 12, 15}, fmIndex.getC());

    }

    @org.junit.jupiter.api.Test
    void getCharacters() {

        char[] expectedArrayCharacters = (Character.MIN_VALUE + "abdlr").toCharArray();

        Assertions.assertArrayEquals(expectedArrayCharacters, fmIndex.getCharacters());

    }

}