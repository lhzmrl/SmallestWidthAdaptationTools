package tech.lihz.tools.sma;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SmallestWidthGenerator {

    private String path;
    private int mDesignDpWidth;
    private static List<DimenConfig> dimensData = new ArrayList<>();

    static {
        dimensData.add(new DimenConfig(240));
        dimensData.add(new DimenConfig(256));
        dimensData.add(new DimenConfig(320));
        dimensData.add(new DimenConfig(360));
        dimensData.add(new DimenConfig(384));
        dimensData.add(new DimenConfig(392));
        dimensData.add(new DimenConfig(400));
        dimensData.add(new DimenConfig(410));
        dimensData.add(new DimenConfig(411));
        dimensData.add(new DimenConfig(420));
        dimensData.add(new DimenConfig(430));
        dimensData.add(new DimenConfig(432));
        dimensData.add(new DimenConfig(440));
        dimensData.add(new DimenConfig(455));
        dimensData.add(new DimenConfig(480));
        dimensData.add(new DimenConfig(533));
        dimensData.add(new DimenConfig(592));
        dimensData.add(new DimenConfig(600));
        dimensData.add(new DimenConfig(640));
        dimensData.add(new DimenConfig(662));
        dimensData.add(new DimenConfig(720));
        dimensData.add(new DimenConfig(768));
        dimensData.add(new DimenConfig(800));
        dimensData.add(new DimenConfig(811));
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDesignDpWidth(int designDpWidth) {
        mDesignDpWidth = designDpWidth;
    }

    public void generate() {

        for (int i = 0; i < dimensData.size(); i++) {
            DimenConfig dimenConfig = dimensData.get(i);
            String parentName = path + "values-sw" + dimenConfig.getSmallestWidthDp() + "dp";
            File file = new File(parentName);
            if (!file.exists()) {
                file.mkdirs();
            }
            /************************编写dimens.xml文件*******************************/
            File dim = new File(file, "dimens.xml");
            dim.delete();
            writeFile(dim, dimenConfig);
        }

    }

    private void writeFile(File lay, DimenConfig dimens) {
        //切勿使用FileWriter写数据，它是默认使用ISO-8859-1 or gb2312，不是utf-8,并且没有setEncoding方法
        BufferedWriter fw = null;
        try {
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lay, true), "UTF-8"));

            fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
            fw.write("<resources xmlns:tools=\"http://schemas.android.com/tools\">" + "\n");
            fw.write("    <string name=\"_dimen_name\" translatable=\"false\">" + lay.getParentFile().getName() + "</string>" + "\n");
            addDimen(fw, dimens.getSmallestWidthDp(), 0);
            addDimen(fw, dimens.getSmallestWidthDp(), 0.1F);
            addDimen(fw, dimens.getSmallestWidthDp(), 0.2F);
            addDimen(fw, dimens.getSmallestWidthDp(), 0.5F);
            for (int k = 1; k < 1920; k++) {
                addDimen(fw, dimens.getSmallestWidthDp(), k);
            }
            StringBuffer sp = new StringBuffer();
            for (int i = 1; i <= 40; i++) {
                sp.append("    <dimen name=\"sp_" + i + "\">");
                float value = ((float) dimens.getSmallestWidthDp() / mDesignDpWidth) * i;
                sp.append(value + "sp</dimen>" + "\n");
            }
            fw.write("\n");
            fw.write("    /**********字体适配***************/" + "\n");
            fw.write("\n");
            fw.write(sp.toString());
            fw.write("</resources>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StringBuffer _sb = new StringBuffer();
    private final DecimalFormat df = new DecimalFormat("#0.#");

    private void addDimen(BufferedWriter fw, int swDp, float k) throws IOException {
        _sb.setLength(0);
        String kStr = df.format(k);
        String ignore = "";
        if (kStr.contains(".")) {
            ignore = " tools:ignore=\"MissingDefaultResource\"";
        }
        _sb.append("    <dimen name=\"dp_").append(kStr).append('"').append(ignore).append(">");
        float dp = (swDp * 1.0F / mDesignDpWidth) * k;
        _sb.append(dp).append("dp</dimen>").append("\n");
        fw.write(_sb.toString());
    }


}