package tech.lihz.tools.sma;

import org.junit.Test;

public class SmallestWidthGeneratorTest {

    @Test
    public void generateDimenValue() {
        SmallestWidthGenerator smallestWidthGenerator = new SmallestWidthGenerator();
        smallestWidthGenerator.setPath("D:\\Workspace\\DemoAndroidStudioProjects\\ScreenAdaptation\\app\\src\\main\\res\\");
        smallestWidthGenerator.setDesignDpWidth(360);
        smallestWidthGenerator.generate();
    }
}