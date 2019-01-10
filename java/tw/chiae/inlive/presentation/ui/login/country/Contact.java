package tw.chiae.inlive.presentation.ui.login.country;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjz on 9/3/16.
 */
public class Contact {
    private String index;
    private String name;
    private String code;

    public Contact(String index, String name,String code) {
        this.index = index;
        this.name = name;
        this.code=code;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static List<Contact> getCountryCode() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("A", "澳門", "853"));
        contacts.add(new Contact("A", "澳大利亞", "61"));
        contacts.add(new Contact("A", "阿根廷", "54"));
        contacts.add(new Contact("A", "愛爾蘭", "353"));
        contacts.add(new Contact("A", "埃及", "683"));
        contacts.add(new Contact("A", "奧地利", "43"));
        contacts.add(new Contact("B", "巴基斯坦", "92"));
        contacts.add(new Contact("B", "巴拿馬", "507"));
        contacts.add(new Contact("B", "白俄羅斯", "375"));
        contacts.add(new Contact("B", "保加利亞", "359"));
        contacts.add(new Contact("B", "秘魯", "51"));
        contacts.add(new Contact("B", "波蘭", "48"));
        contacts.add(new Contact("B", "巴西", "55"));
        contacts.add(new Contact("B", "冰島", "354"));
        contacts.add(new Contact("B", "比利時", "32"));
        contacts.add(new Contact("C", "朝鮮", "850"));
        contacts.add(new Contact("D", "丹麥", "45"));
        contacts.add(new Contact("D", "德國", "49"));
        contacts.add(new Contact("E", "俄羅斯", "7"));
        contacts.add(new Contact("F", "芬蘭", "358"));
        contacts.add(new Contact("F", "法屬圭亞那", "594"));
        contacts.add(new Contact("F", "法國", "33"));
        contacts.add(new Contact("F", "菲律賓", "63"));
        contacts.add(new Contact("G", "哥倫比亞", "57"));
        contacts.add(new Contact("G", "哥斯達黎加", "506"));
        contacts.add(new Contact("G", "古巴", "53"));
        contacts.add(new Contact("H", "韓國", "82"));
        contacts.add(new Contact("H", "哈薩克斯坦", "7"));
        contacts.add(new Contact("H", "海底", "509"));
        contacts.add(new Contact("H", "荷蘭", "31"));
        contacts.add(new Contact("J", "加拿大", "1"));
        contacts.add(new Contact("J", "柬埔寨", "855"));
        contacts.add(new Contact("J", "捷克", "420"));
        contacts.add(new Contact("K", "克羅地亞", "385"));
        contacts.add(new Contact("L", "老撾", "856"));
        contacts.add(new Contact("L", "羅馬尼亞", "40"));
        contacts.add(new Contact("M", "孟加拉", "880"));
        contacts.add(new Contact("M", "美國", "1"));
        contacts.add(new Contact("M", "馬來西亞", "60"));
        contacts.add(new Contact("M", "墨西哥", "52"));
        contacts.add(new Contact("M", "緬甸", "95"));
        contacts.add(new Contact("M", "外蒙古", "976"));
        contacts.add(new Contact("N", "挪威", "47"));
        contacts.add(new Contact("N", "南非", "27"));
        contacts.add(new Contact("N", "尼泊爾", "977"));
        contacts.add(new Contact("P", "葡萄牙", "351"));
        contacts.add(new Contact("R", "日本", "81"));
        contacts.add(new Contact("R", "瑞士", "41"));
        contacts.add(new Contact("R", "瑞典", "46"));
        contacts.add(new Contact("S", "斯洛伐克", "421"));
        contacts.add(new Contact("T", "泰國", "66"));
        contacts.add(new Contact("T", "台灣", "886"));
        contacts.add(new Contact("W", "烏克蘭", "380"));
        contacts.add(new Contact("X", "香港", "852"));
        contacts.add(new Contact("X", "新西蘭", "64"));
        contacts.add(new Contact("X", "新加坡", "65"));
        contacts.add(new Contact("X", "希臘", "30"));
        contacts.add(new Contact("X", "西班牙", "34"));
        contacts.add(new Contact("X", "匈牙利", "36"));
        contacts.add(new Contact("Y", "越南", "84"));
        contacts.add(new Contact("Y", "印度尼西亞", "62"));
        contacts.add(new Contact("Y", "意大利", "39"));
        contacts.add(new Contact("Y", "以色列", "972"));
        contacts.add(new Contact("Y", "約旦", "962"));
        contacts.add(new Contact("Y", "英國", "44"));
        contacts.add(new Contact("Z", "中國", "86"));
        contacts.add(new Contact("Z", "智利", "56"));
        return contacts;
    }

}
