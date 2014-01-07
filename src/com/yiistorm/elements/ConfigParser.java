package com.yiistorm.elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.intellij.openapi.vfs.VirtualFile;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.references.YiiPsiReferenceProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 01.10.13
 * Time: 2:51
 * To change this template use File | Settings | File Templates.
 */
public class ConfigParser {
    public HashMap<String, String> aliases = new HashMap<String, String>();
    public HashMap<String, Map.Entry<String, JsonElement>> components = new HashMap<String, Map.Entry<String, JsonElement>>();
    HashMap<String, String> componentsMap;
    public HashMap<String, Map.Entry<String, JsonElement>> params = new HashMap<String, Map.Entry<String, JsonElement>>();
    public HashMap<String, Map.Entry<String, JsonElement>> modules = new HashMap<String, Map.Entry<String, JsonElement>>();
    String yiiConfigPath;
    String yiiLitePath;

    public ConfigParser(YiiStormProjectComponent projectComponent) {
        yiiConfigPath = projectComponent.getProp("yiiConfigPath");
        yiiLitePath = projectComponent.getProp("yiiLitePath");
        if (yiiConfigPath != null && yiiLitePath != null) {
            parseConfig();
        }
    }

    public void parseConfig() {
        if (yiiConfigPath == null) {
            return;
        }
        if (new File(yiiConfigPath).exists()) {
            BufferedReader output = null;
            String s = "";
            try {
                //   String prependPath = CommonHelper.getCommandPrepend();

                String php = "print json_encode(require('" + yiiConfigPath + "'));";
                php = "php -r \"error_reporting(0);require('" + yiiLitePath + "');" + php + "\"";
                Process p = Runtime.getRuntime().exec(php);

                if (p != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    JsonStreamParser jsonStreamParser = new JsonStreamParser(reader);
                    JsonElement obj = jsonStreamParser.next();
                    JsonObject root = obj.getAsJsonObject();

                    if (root != null && root.has("aliases") && root.get("aliases").isJsonObject()) {
                        JsonObject aliasesObj = root.get("aliases").getAsJsonObject();
                        for (Map.Entry<String, JsonElement> el : aliasesObj.entrySet()) {
                            aliases.put(el.getKey(), el.getValue().toString());
                        }
                    }
                    if (root != null && root.has("components") && root.get("components").isJsonObject()) {
                        JsonObject aliasesObj = root.get("components").getAsJsonObject();
                        for (Map.Entry<String, JsonElement> el : aliasesObj.entrySet()) {
                            components.put(el.getKey(), el);
                        }
                    }
                    if (root != null && root.has("params") && root.get("params").isJsonObject()) {
                        JsonObject aliasesObj = root.get("params").getAsJsonObject();
                        for (Map.Entry<String, JsonElement> el : aliasesObj.entrySet()) {
                            modules.put(el.getKey(), el);
                        }
                    }
                    if (root != null && root.has("modules") && root.get("modules").isJsonObject()) {
                        JsonObject aliasesObj = root.get("modules").getAsJsonObject();
                        for (Map.Entry<String, JsonElement> el : aliasesObj.entrySet()) {
                            params.put(el.getKey(), el);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    public HashMap<String, String> getComponentsClassMap() {
        if (componentsMap == null) {
            componentsMap = new HashMap<String, String>();
            if (components.size() > 0) {
                for (String key : components.keySet()) {
                    JsonElement el = components.get(key).getValue();
                    JsonObject elArr = el.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> elArrKey : elArr.entrySet()) {
                        if (elArrKey.getKey().toLowerCase().equals("class")) {
                            String className = elArrKey.getValue().getAsString();
                            componentsMap.put(key, className);
                        }
                    }
                }
            }
        }
        return componentsMap;
    }

    public String checkAlias(String path) {
        String ret = "";
        if (path.contains(".")) {
            String alias = path.split("\\.")[0];
            if (aliases.containsKey(alias)) {
                ret = aliases.get(alias).replace("\\\\", "\\").replace("\"", "");

                VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
                String basePath = baseDir.getPath().replace("/", "\\");
                ret = ret.replace(basePath, "");
            }
        }
        return ret;
    }
}
