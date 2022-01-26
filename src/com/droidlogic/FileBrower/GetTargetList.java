package com.droidlogic.FileBrower;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetTargetList {
    private static List<Map<String, Object>> rList = new ArrayList<>();
    public static List<Map<String, Object>> listsearch(List<Map<String, Object>> list, String input) {

        String patten = Pattern.quote("" + input);
        //不区分大小写
        Pattern pattern = Pattern.compile(patten, Pattern.CASE_INSENSITIVE);
        rList.clear();
        for (int i = 0; i < list.size(); i++) {
            File file = new File(((String) list.get(i).get("key_path")));
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find()) {
                rList.add(list.get(i));
            }
        }
        return rList;
    }
}


