import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

public class group_SpiderHandler extends Thread{
    private File file=null;		//网址库文本文件
    private OptionalFrame optionalFrame;
    private MyProgressBar myProgressBar ;		//进度条
    //构造函数初始化
    public group_SpiderHandler(OptionalFrame father, File file) {
        this.file=file;
        this.optionalFrame=father;
        myProgressBar =new MyProgressBar(father, "Spidering");
    }

    public void run() {
        try {
            //读取网址库中的网址
            BufferedReader brr=new BufferedReader(new FileReader(file));
            //将匹配数据写入文本中
            PrintStream ps=new PrintStream(new File("D:\\Java_code\\Sensitive","data.txt"));
            ps.println("敏感词记录如下:");
            int size=optionalFrame.wordList.size();
            myProgressBar.setVisible(true);	//显示进度条
            while(true) {
                String website=brr.readLine();
                if(website==null)	break;
                myProgressBar.setTitle("爬取"+website+"中...");	//设置进度条界面标题
                ps.println(website+"数据如下: ");
                String html=myWeb.getHtml(website);	//获取html代码
                String text=myWeb.getText(html);		//匹配网页文本
                for(int i=0;i<size;i++) {		//在网页文本中进行匹配
                    String word=optionalFrame.wordList.get(i);
                    int index=0,account=0,len=word.length();
                    while((index=text.indexOf(word,index))>=0) {
                        account++;
                        int temp=optionalFrame.wordNum.get(i);	//更新数据
                        optionalFrame.wordNum.set(i,++temp);
                        index+=len;		//更新匹配条件
                    }
                    ps.println(word+"  出现  "+account+"次");	//写入当前数据
                }
                ps.println();
            }
            brr.close();	//关闭文件流
            System.out.println("爬取完毕");
            ps.println("总数据如下:     ");		//写入总数据
            for(int i=0;i<size;i++) {
                ps.println(optionalFrame.wordList.get(i)+"  出现    "+optionalFrame.wordNum.get(i)+"次");
            }
            ps.close();		//关闭文件流
            JOptionPane.showMessageDialog(null, "爬取完毕！请打开文件查看!");
        }catch (Exception e) {
            // TODO: handle exception
            JOptionPane.showMessageDialog(null, "爬取失败");
        }finally {
            myProgressBar.dispose();	//关闭进度条
        }
    }
}
