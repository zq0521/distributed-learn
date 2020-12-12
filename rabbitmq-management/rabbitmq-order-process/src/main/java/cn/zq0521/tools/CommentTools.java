package cn.zq0521.tools;

import java.sql.Timestamp;
import java.util.Date;

public class CommentTools {

    //时间戳工具
    public static Timestamp getNowTimestamp() {
        Date date = new Date();
        Timestamp nousedate = new Timestamp(date.getTime());
        return nousedate;
    }
}
