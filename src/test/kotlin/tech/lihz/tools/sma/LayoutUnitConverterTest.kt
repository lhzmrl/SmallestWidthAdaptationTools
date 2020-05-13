package tech.lihz.tools.sma

import org.junit.Test
import tech.lihz.multi.logger.DebugPrinter
import tech.lihz.multi.logger.Logger

class LayoutUnitConverterTest {

    @Test
    fun testConvert() {
        Logger.install(DebugPrinter())
        val layoutUnitConverter = LayoutUnitConverter(
            "D:\\Workspace\\AndroidStudioProjects\\CateringPOS\\app\\src\\main\\res\\layout",
            "C:\\Users\\Morale\\Desktop\\TestLayoutUnitConvert\\Target",
            3F
        )
        layoutUnitConverter.addCustomAttr("customWidget:customAttr")
        layoutUnitConverter.start()
    }

}