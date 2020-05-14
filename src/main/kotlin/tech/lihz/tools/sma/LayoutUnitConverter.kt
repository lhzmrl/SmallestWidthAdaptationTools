package tech.lihz.tools.sma

import com.sun.org.apache.xml.internal.serialize.OutputFormat
import com.sun.org.apache.xml.internal.serialize.XMLSerializer
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import tech.lihz.multi.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.xml.parsers.DocumentBuilderFactory

class LayoutUnitConverter(private val sourceLayoutPath: String, private val targetLayoutPath: String, private val convertRadio: Float) {

    companion object {
        const val TAG = "LayoutUnitConvert"
    }

    private val SUIT_ATTR_NAMES: MutableList<String> = mutableListOf(
        "android:layout_width",
        "android:layout_height",
        "android:width",
        "android:height",
        "android:layout_margin",
        "android:layout_marginTop",
        "android:layout_marginBottom",
        "android:layout_marginLeft",
        "android:layout_marginRight",
        "android:layout_marginStart",
        "android:layout_marginEnd",
        "android:padding",
        "android:paddingTop",
        "android:paddingBottom",
        "android:paddingLeft",
        "android:paddingRight",
        "android:paddingStart",
        "android:paddingEnd",
        "android:textSize"
    )

    init {
        val targetLayoutDir = File(targetLayoutPath)
        targetLayoutDir.mkdirs()
    }

    fun addCustomAttr(attrName: String) {
        SUIT_ATTR_NAMES.add(attrName)
    }

    fun start() {
        translateDefaultResourceFile()
    }

    private fun translateDefaultResourceFile() {
        val layoutDir = File(sourceLayoutPath)
        if (!layoutDir.exists()) {
            Logger.e(TAG, "源目录不存在：$sourceLayoutPath")
            return
        }
        layoutDir.list()?.forEach {
            translate(File(layoutDir, it).absolutePath)
        }
    }

    private fun translate(layoutFilePath: String) {
        Logger.d(TAG, "Translate file : $layoutFilePath")
        val resourceFile = File(layoutFilePath)
        val fileName = resourceFile.name
        // 准备目标文件
        val targetLayoutFile = File(targetLayoutPath, fileName)
        if (targetLayoutFile.exists()) {
            targetLayoutFile.createNewFile()
        }
        Logger.d(TAG, "创建文件 ${targetLayoutFile.absolutePath}：${targetLayoutFile.exists()}")
        val targetDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()


        // 开始解析源文件
        val dFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dFactory.newDocumentBuilder()
        val doc = dBuilder.parse(resourceFile)
        val sourceDocumentElement = doc.documentElement

        Logger.d(TAG, "Node:${sourceDocumentElement.nodeName}")
        targetDoc.appendChild(copyNode(targetDoc, sourceDocumentElement))


        val format = OutputFormat(targetDoc)
        format.lineWidth = 80
        format.indenting = true
        format.indent = 4
        val out: OutputStream = FileOutputStream(targetLayoutFile)
        val serializer = XMLSerializer(out, format)
        serializer.serialize(targetDoc)
        out.close()
    }

    private fun copyNode(doc: Document, source: Node): Node? {
        return when (source.nodeType) {
            Node.ELEMENT_NODE -> copyElement(doc, source as Element)
            else -> null
        }
    }

    private fun copyElement(doc: Document, source: Element): Element {
        val target = doc.createElement(source.tagName)
        val namedNodeMap = source.attributes
        for (i in 0 until namedNodeMap.length) {
            val node = namedNodeMap.item(i)
            if (node.nodeType == Node.ATTRIBUTE_NODE) {
                val attr = node as Attr
                target.setAttribute(attr.name, tryConvertUnit(attr.name, attr.value))
            }
        }
        val sourceChildNodes = source.childNodes
        for (i in 0 until sourceChildNodes.length) {
            val node = sourceChildNodes.item(i)
            val nodeType = node.nodeType
            if (nodeType != Node.ELEMENT_NODE) {
                continue
            }
            target.appendChild(copyNode(doc, node))
        }
        return target
    }

    private fun tryConvertUnit(name: String, value: String): String {
        SUIT_ATTR_NAMES.find {
            it == name
        } ?: return value
        if (!value.endsWith("dp") && !value.endsWith("sp") && !value.endsWith("px") && !value.endsWith("dip")) {
            return value
        }
        val valueStr = value.substring(0, value.length - 2)
        val valueFloat = valueStr.toFloatOrNull() ?: throw RuntimeException("Can't parse value to a valid size value")
        val valueFloatChanged = valueFloat / convertRadio
        // 先处理字体的情况
        if(value.endsWith("sp")){
            return if(valueFloatChanged < 0.6F) {
                "@dimen/sp_1"
            } else {
                "@dimen/sp_${valueFloatChanged.toInt()}"
            }
        }
        // 先处理小数的情况
        return if (valueFloatChanged == 0F) {
            "@dimen/dp_0"
        } else if(valueFloatChanged < 0.15F) {
            "@dimen/dp_0.1"
        } else if(valueFloatChanged < 0.3F) {
            "@dimen/dp_0.2"
        } else if(valueFloatChanged < 0.75F) {
            "@dimen/dp_0.5"
        } else {
            "@dimen/dp_${valueFloatChanged.toInt()}"
        }
    }


}