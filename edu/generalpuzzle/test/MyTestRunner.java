package edu.generalpuzzle.test;

/**
 * Created by IntelliJ IDEA.
 * User: talshah
 * Date: 07/06/2010
 * Time: 02:43:24
 * To change this template use File | Settings | File Templates.
 */
public class MyTestRunner {
    public static void main(String s[]) {
        int failed = 0;
        long time = System.currentTimeMillis();

        failed += new MyTestCase(EngineTest.class).runTests();
        failed += new MyTestCase(BasicAcceptanceTest.class).runTests();
        failed += new MyTestCase(PartTest.class).runTests();
        failed += new MyTestCase(SphereTest.class).runTests();

        System.out.println("\n--------------------------");
		System.out.println("total failed: " + failed);
        System.out.println("total time: " + (System.currentTimeMillis() - time));
    }
}
