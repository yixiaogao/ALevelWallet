package com.theone.a_levelwallet.activity.businessCardFrame;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lh on 2015/9/13.
 */
public class BSCardUtils {
    private String name;
    private String title;
    private String tel;
    private String mobile;
    private String fax;
    private String email;
    private String comp;
    private String dept;
    private String degree;
    private String addr;
    private String post;
    private String mbox;
    private String htel;
    private String web;
    private String im;
    private String numOther;
    private String other;
    private String extTel;

    public BSCardUtils() {
    }

    public BSCardUtils(String bsCardInfo) {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(bsCardInfo);
            name = jsonObj.getString("name");
            title = jsonObj.getString("title");
            tel = jsonObj.getString("tel");
            mobile = jsonObj.getString("mobile");
            fax = jsonObj.getString("fax");
            email = jsonObj.getString("email");
            comp = jsonObj.getString("comp");
            dept = jsonObj.getString("dept");
            degree = jsonObj.getString("degree");
            addr = jsonObj.getString("addr");
            post = jsonObj.getString("post");
            mbox = jsonObj.getString("mbox");
            htel = jsonObj.getString("htel");
            web = jsonObj.getString("web");
            im = jsonObj.getString("im");
            numOther = jsonObj.getString("numOther");
            other = jsonObj.getString("other");
            extTel = jsonObj.getString("extTel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isStringNotEmpty(String s) {
        return s != null && !"".equals(s.trim())&& !"[]".equals(s.trim());
    }

    public String toString() {
        String str = (isStringNotEmpty(name) ? ("姓名：" + name + "\n") : "") +
                (isStringNotEmpty(title) ? ("职务：" + title+ "\n") : "") +
                (isStringNotEmpty(tel) ? ("电话：" + tel+ "\n") : "") +
                (isStringNotEmpty(mobile) ? ("手机：" + mobile+ "\n") : "")+
                (isStringNotEmpty(fax) ? ("传真：" + fax+ "\n") : "") +
                (isStringNotEmpty(email) ? ("邮箱：" + email+ "\n") : "")+
                (isStringNotEmpty(comp) ? ("公司：" + comp+ "\n") : "")+
                (isStringNotEmpty(dept) ? ("部门：" + dept+ "\n") : "") +
                (isStringNotEmpty(degree) ? ("学历：" + degree+ "\n") : "")  +
                (isStringNotEmpty(addr) ? ("地址：" + addr+ "\n") : "")+
                (isStringNotEmpty(post) ? ("post：" + post+ "\n") : "")  +
                (isStringNotEmpty(mbox) ? ("mbox：" + mbox+ "\n") : "") +
                (isStringNotEmpty(htel) ? ("Htel：" + htel+ "\n") : "") +
                (isStringNotEmpty(web) ? ("网站：" + web+ "\n") : "") +
                (isStringNotEmpty(im) ? ("im：" + im+ "\n") : "") +
                (isStringNotEmpty(numOther) ? ("其他号码：" + numOther+ "\n") :"") +
                (isStringNotEmpty(other) ? ("其他：" + other+ "\n") :"")+
                (isStringNotEmpty(extTel) ? ("extTel：" + extTel+ "\n") :"") ;
        System.out.println(str);

        return str;
    }

}
