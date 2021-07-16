package requests;

import net.sf.json.JSONObject;
import utils.HttpRequestUtil;
import utils.PropertyUtil;

public class UploadThread implements Runnable{

    private String base64;
    private String userId;
    private String host;
    private String uri;

    public UploadThread(String base64, String userId) {
        this.base64 = base64;
        this.userId = userId;
        this.host = PropertyUtil.getProperty("host");
        this.uri = PropertyUtil.getProperty("uri");
    }

    @Override
    public void run() {
        JSONObject json = new JSONObject();
        json.put("img", base64);
        json.put("userId", userId);
        String result = HttpRequestUtil.sendPost(host + uri, json.toString());
        System.out.println(result);
    }
}
