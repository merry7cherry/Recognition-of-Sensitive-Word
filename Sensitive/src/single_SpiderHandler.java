import javax.swing.*;

public class single_SpiderHandler extends Thread{
    private String website;
    private OptionalFrame optionalFrame;
    private MyProgressBar myProgressBar;

    public single_SpiderHandler(OptionalFrame father,String website){
        this.website=website;
        this.optionalFrame=father;
        myProgressBar=new MyProgressBar(father,"Spidering");
    }

    @Override
    public void run() {   //判断网址是否正常
        if(website.isEmpty()){
            JOptionPane.showMessageDialog(null, "网址不能为空");
            return;
        }
        optionalFrame.textArea.setText("");
        optionalFrame.htmlArea.setText("");
        myProgressBar.setTitle("爬取"+website+"中...");
        myProgressBar.setVisible(true);
        String obtain=myWeb.getHtml(website);
        myProgressBar.dispose();
        if(!obtain.isEmpty()){
            JOptionPane.showMessageDialog(null, "爬取完毕");	//提示完成
            optionalFrame.htmlArea.setText(obtain);	//显示html源代码
            String text=myWeb.getText(obtain);	//匹配网页文本
            optionalFrame.textArea.setText(text);	//显示网页文本
        }
    }
}
