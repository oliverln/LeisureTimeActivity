import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by lunan on 17-11-28.
 */
public class ClientTest {
    Client cli;
    @Before
    public void setUp() throws Exception {
        cli=new Client();
    }

    @Test
    public void nettyClient(){
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("请输入：");
            String sentences = sc.nextLine();
            cli.connect(8989, "zhonghaihua-Lenovo-G410", sentences);
        }
        }
    @Test
    public void main()throws Exception{
        /*
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        System.out.println("Enter your value:");
        str = br.readLine();
        System.out.println("your value is :"+str);

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入你的姓名：");
        String name = sc.nextLine();
        System.out.println("请输入你的年龄：");
        int age = sc.nextInt();
        System.out.println("请输入你的工资：");
        float salary = sc.nextFloat();
        System.out.println("你的信息如下：");
        System.out.println("姓名："+name+"\n"+"年龄："+age+"\n"+"工资："+salary);
        */
        nettyClient();
    }

}