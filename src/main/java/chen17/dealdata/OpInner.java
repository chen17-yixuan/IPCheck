package chen17.dealdata;

import chen17.domain.Dayerrorwork;
import chen17.util.JacksonUtil;
import chen17.util.JarTool;
import com.chen17.pingip.opt.ConcurrentPingIp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yd
 * @version 1.0
 * @date 2020-10-31 08:40
 */

public class OpInner {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("开始,即将读取本目录下deal.json,请确保该目录下存在该文件");
        File file = new File(JarTool.getJarDir()+"\\deal.json");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            System.out.println("未发现deal.json文件");
            file.createNewFile();
        }

        System.out.println("读取文件成功");

        ObjectMapper mo = new ObjectMapper();

        ArrayList<Dayerrorwork> dayerrorwork = mo.readValue(file, new TypeReference<ArrayList<Dayerrorwork>>() {
        });


        Map<Integer,String> idIpList = new HashMap<Integer, String>();
        for (Dayerrorwork dw : dayerrorwork) {
            idIpList.put(dw.getErrortableId(),dw.getErrortableDeviceIp());
        }
        System.out.println("正在Ping IP");
        ConcurrentPingIp cp = new ConcurrentPingIp();
        Map<Integer, Boolean> integerBooleanMap = cp.dealIp(idIpList, 100);

        List<Integer> idList = new ArrayList<Integer>();
        for(Integer index : integerBooleanMap.keySet()){
            if(integerBooleanMap.get(index)){
                idList.add(index);
            }
        }
        System.out.println("Ping IP完成，正在生成文件");
        File outFile = new File(JarTool.getJarDir()+"\\resule.json");
        if (!outFile.exists()) {
            outFile.createNewFile();
        }else {
            outFile.delete();
            outFile.createNewFile();
        }

        String toJSon = JacksonUtil.toJSon(idList);

        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outFile));

        bo.write(toJSon.getBytes("UTF-8"));
        bo.flush();

        bo.close();
        System.out.println("生成文件成功");
        file.delete();
    }

}
