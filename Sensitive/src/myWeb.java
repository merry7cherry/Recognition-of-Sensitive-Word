import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class myWeb {

    private static String codeType="UTF-8";
    //设置正则表达式的匹配符
    private static final String regExHtml="<[^>]+>";		//匹配标签
    private static final String regExScript = "<script[^>]*?>[\\s\\S]*?</script>";		//匹配script标签
    private static final String regExStyle = "<style[^>]*?>[\\s\\S]*?</style>";		//匹配style标签
    private static final String regExSpace="\\s{2,}";	//匹配连续空格或回车等
    private static final String regExImg="&\\S*?;+";	//匹配网页上图案的乱码
    //定义正则表达式
    private static final Pattern pattern3=Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern1=Pattern.compile(regExScript,Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern2=Pattern.compile(regExStyle,Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern4=Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern5=Pattern.compile(regExImg,Pattern.CASE_INSENSITIVE);

    //使用URL爬取网页的html代码
    public static String getHtml(String website){
        String str=null;
        StringBuilder text= new StringBuilder();		//保存网页的内容
        try {
            URL url=new URL(website);	//建立对应的URL对象
            URLConnection urlConnection=url.openConnection();	//连接
            urlConnection.connect();
            //获取输入流
            BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),myWeb.getCodeType()));
            System.out.println("开始爬取");
            while(true) {	//爬取到结束
                str=br.readLine();
                if(str==null)	break;
                text.append(str).append("\n");
            }
            br.close();		//关闭输入流
        }catch (Exception e) {
            // TODO: handle exception
            JOptionPane.showMessageDialog(null, website+"爬取源代码失败");
        }
        System.out.println("爬取结束");
        return text.toString();	//返回html代码文本
    }

    //对html进行正则匹配,提取出其中的文本
    public static String getText(String str) {

        Matcher matcher=pattern1.matcher(str);
        str=matcher.replaceAll("");		//匹配普通标签
        matcher=pattern2.matcher(str);
        str=matcher.replaceAll("");		//匹配script标签
        matcher=pattern3.matcher(str);
        str=matcher.replaceAll("");		//匹配style标签
        matcher=pattern4.matcher(str);
        str=matcher.replaceAll("\n");	//匹配连续回车或空格
        matcher=pattern5.matcher(str);
        str=matcher.replaceAll("");		//匹配网页图案出现的乱码
        return str;		//返回文本
    }

    public static String getCodeType() {
        return codeType;
    }

    public static void setCodeType(String codeType) {
        myWeb.codeType = codeType;
    }
}
