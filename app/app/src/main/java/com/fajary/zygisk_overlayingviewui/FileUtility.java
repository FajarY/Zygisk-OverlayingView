package com.fajary.zygisk_overlayingviewui;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class FileUtility {
    public static boolean writeJson(JSONObject jsonObject, String parentPath, String path)
    {
        try
        {
            File file = new File(parentPath, path);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonObject.toString(2));
            writer.close();

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Debug.logE(e.getMessage());
        }
        return false;
    }
    public static JSONObject readJson(String parentPath, String path)
    {
        try
        {
            File file = new File(parentPath, path);
            if(!file.exists())
            {
                return null;
            }

            StringBuilder builder = new StringBuilder();

            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                builder.append(line);
            }
            bufferedReader.close();

            return new JSONObject(builder.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Debug.logE(e.getMessage());
        }

        return null;
    }
}
