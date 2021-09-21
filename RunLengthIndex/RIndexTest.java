import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RIndexTest {

    String text = Character.MIN_VALUE + "alabaralalabarda";

    char[] pattern = "la".toCharArray();

    RIndex rIndexParallel = new RIndex(text, 64, true);
    RIndex rIndex = new RIndex(text, 64, false);

    @Test
    void getRangeWithRIndex() {

        Assertions.assertArrayEquals(new int[]{13, 15}, rIndex.getRangeWithRIndex(pattern));
        Assertions.assertArrayEquals(new int[]{13, 15}, rIndexParallel.getRangeWithRIndex(pattern));

    }

    @Test
    void rankOfBwtWithRIndex() {

        Assertions.assertEquals(2, rIndex.rankOfBwtWithRIndex('r', 12));
        Assertions.assertEquals(2, rIndexParallel.rankOfBwtWithRIndex('r', 12));

    }

    @Test
    void rankWithR() {

        Assertions.assertEquals(2, rIndex.rankWithR('l', 5));
        Assertions.assertEquals(2, rIndexParallel.rankWithR('l', 5));

    }

    @Test
    void locate() {

        Assertions.assertArrayEquals(new int[]{2, 10, 8}, rIndex.locate(pattern));
        Assertions.assertArrayEquals(new int[]{2, 10, 8}, rIndexParallel.locate(pattern));

    }

    @Test
    void getPreData() {

        Assertions.assertArrayEquals(new int[]{1, 2, 3, 5, 6, 7, 8, 10, 12, 13}, rIndex.getPreData());
        Assertions.assertArrayEquals(new int[]{1, 2, 3, 5, 6, 7, 8, 10, 12, 13}, rIndexParallel.getPreData());

    }

    @Test
    void getSPrime() {

        char[] expectedArraySPrime = ("adl" + Character.MIN_VALUE + "lrbara").toCharArray();

        Assertions.assertArrayEquals(expectedArraySPrime, rIndex.getSPrime());
        Assertions.assertArrayEquals(expectedArraySPrime, rIndexParallel.getSPrime());

    }

    @Test
    void getR() {

        Assertions.assertArrayEquals(new int[]{1, 1, 3, 8, 2, 1, 2, 3, 1, 2}, rIndex.getR());
        Assertions.assertArrayEquals(new int[]{1, 1, 3, 8, 2, 1, 2, 3, 1, 2}, rIndexParallel.getR());

    }

    @Test
    void getCOfRIndex() {

        Assertions.assertArrayEquals(new int[]{0, 1, 4, 5, 6, 8}, rIndex.getCOfRIndex());
        Assertions.assertArrayEquals(new int[]{0, 1, 4, 5, 6, 8}, rIndexParallel.getCOfRIndex());

    }

    @Test
    void getBwtC() {

        Assertions.assertArrayEquals(new int[]{0, 1, 9, 11, 12, 15}, rIndex.getBwtC());
        Assertions.assertArrayEquals(new int[]{0, 1, 9, 11, 12, 15}, rIndexParallel.getBwtC());

    }

    @Test
    void getKeyDistance() {

        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4, 5, 7, 9, 15, 16}, rIndex.getKeyDistance());
        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4, 5, 7, 9, 15, 16}, rIndexParallel.getKeyDistance());

    }

    @Test
    void getValueDistance() {

        Assertions.assertArrayEquals(new int[]{10, 13, 13, 9, 2, 2, -8, -3, 1}, rIndex.getValueDistance());
        Assertions.assertArrayEquals(new int[]{10, 13, 13, 9, 2, 2, -8, -3, 1}, rIndexParallel.getValueDistance());

    }

    @Test
    void getL() {

        Assertions.assertArrayEquals(new int[]{1, 17, 12, 14, 13, 16, 11, 9, 7, 15}, rIndex.getL());
        Assertions.assertArrayEquals(new int[]{1, 17, 12, 14, 13, 16, 11, 9, 7, 15}, rIndexParallel.getL());

    }

}