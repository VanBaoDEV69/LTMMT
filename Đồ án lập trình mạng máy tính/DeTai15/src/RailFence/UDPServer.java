/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RailFence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author Administrator
 */
public class UDPServer {

    static final int PORT = 1234;
    private DatagramSocket socket = null;
    public UDPServer(){
        try{
          socket=new DatagramSocket(PORT);  
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void action(){
        InetAddress host = null;
        int port;
        String chuoi="";
        try{
            System.out.println("Server is listening");
            while(true){
                DatagramPacket packet= receive();
                host=packet.getAddress();
                port=packet.getPort();
                chuoi=new String(packet.getData()).trim(); 
                GhiFile(chuoi);
                String MaHoa = DocFile();
                if(!MaHoa.equals("")){
                    Scanner sca = new Scanner(MaHoa);
                    sca.useDelimiter("@");
                    String banMaHoa =sca.next();
                    int key = sca.nextInt();
                    System.err.println("Bản mã hóa: "+banMaHoa);
                    System.err.println("Khóa: "+key);

                    // giải mã 
                    String kq = GiaiMa(banMaHoa, key);
                    
                    //tìm ký tự thứ 2 
                    String kytu = TimKyTu(kq);
                    send(kytu,host,port);
                }
                    
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            socket.close();
        }       
    }
    private void send(String chuoi,InetAddress host,int port)throws IOException{
        byte[] buffer=chuoi.getBytes();
        DatagramPacket packet=new DatagramPacket(buffer,buffer.length,host,port);
        socket.send(packet);
    }
    
    
    private DatagramPacket receive()throws IOException{
        byte[]buffer = new byte[65507];
        DatagramPacket packet = new DatagramPacket (buffer,buffer.length);
        socket.receive(packet);
        return packet;
    }
    public static void main(String[] args) {
        new UDPServer().action();
    }

    private String TimKyTu(String text) {
        char kytu = 0;
        String ketqua = "";
        char kytuSt1 = 0;
        int solan1 = 0;
        char kytuSt2 = 0;
        int solan2 = 0;
        String St2 = "";
        for (int i = 0; i < text.length(); i++) {
            kytu = text.charAt(i);
            int dem=0; int tf = 0;
            for (int j = 0; j < text.length(); j++) {
                if (i<j && text.charAt(i) == text.charAt(j)) {
                    tf =1;
                }else{
                    if (text.charAt(i) == text.charAt(j)) {
                        dem++;
                        tf = 0;
                    }
                }
                
            }
            if (dem > solan1) {
                solan1 = dem;
                kytuSt1 = text.charAt(i);
            }
            if (tf == 0) {
                ketqua += kytu+": "+dem+"; ";
            }
        }
        
        
        // xóa ký tự nhiều nhất khỏi chuỗi
        String chuoimoi="";
        for (int i = 0; i < text.length(); i++) {
            kytu = text.charAt(i);
            int dem1=0;
            for (int j = 0; j < text.length(); j++) {
                if (text.charAt(i) == text.charAt(j)) {
                    dem1++;
                }
            }
            if (dem1 < solan1) {
                chuoimoi += Character.toString(text.charAt(i));
            }
        }
        // tìm ký tự xuất hiện nhiều thứ số lần xuất hiện nhiều nhất trong chuỗi mới
        for (int i = 0; i < chuoimoi.length(); i++) {
            kytu = chuoimoi.charAt(i);
            int dem=0;
            for (int j = 0; j < chuoimoi.length(); j++) {
                if (i<j && chuoimoi.charAt(i) == chuoimoi.charAt(j)) {
                }else{
                    if (chuoimoi.charAt(i) == chuoimoi.charAt(j)) {
                        dem++;
                    }
                }
            }
            if (dem > solan2) {
                solan2 = dem;
                kytuSt2 = chuoimoi.charAt(i);
            }
        }
        // Tìm các ký tự xuất hiện nhiều thứ 2
        for (int i = 0; i < chuoimoi.length(); i++) {
            kytu = chuoimoi.charAt(i);
            int dem=0; int tf =0;
            for (int j = 0; j < chuoimoi.length(); j++) {
                if (i<j && chuoimoi.charAt(i) == chuoimoi.charAt(j)) {
                    tf = 1;
                }else{
                    if (chuoimoi.charAt(i) == chuoimoi.charAt(j)) {
                        dem++;
                        tf =0;
                    }
                }
            }
            if (dem == solan2) {
                St2 += chuoimoi.charAt(i)+" ,";
            }
        }
        //In ra kết quả
        String kq = "";
        for (int i = 0; i < (St2.length()-2); i++) {
            kq += St2.charAt(i);
        }
        String KetQua = "Các ký tự xuất hiện nhiều thứ 2: "+kq+ " và xuất hiện: '"+solan2+"' lần.";
        
        System.out.println(ketqua);
        System.out.println(KetQua);
        return KetQua;
    }

    public void GhiFile(String text) {
        try {
            //Bước 1: Tạo đối tượng luồng và liên kết nguồn dữ liệu
            File f = new File("D:/MaHoa.txt");
            FileWriter fw = new FileWriter(f);
            //Bước 2: Ghi dữ liệu
            fw.write(text);
            //Bước 3: Đóng luồng
            fw.close();
          } catch (IOException ex) {
            System.out.println("Lỗi ghi file: " + ex);
        }
    }
    private String DocFile(){
        try {
            //Bước 1: Tạo đối tượng luồng và liên kết nguồn dữ liệu
            File f = new File("D:/MaHoa.txt");
            FileReader fr = new FileReader(f);
            //Bước 2: Đọc dữ liệu
            BufferedReader br = new BufferedReader(fr);
            String line;
            String text = "";
            while ((line = br.readLine()) != null){
                text += line;
            }
            
            //Bước 3: Đóng luồng
            fr.close();
            br.close();
            
            return text;
           } catch (Exception ex) {
                String loi = "Loi doc file: "+ex;
                return loi;
         }
    }
    
    
    private String GiaiMa(String s, int k){
        int n = s.length();
        int sd, sc;
        sd = k;
        sc = n/sd+1;
        char hr[][] = new char[sd][sc];
        int sodu=n%sd;
        int sokytu = sc;
        int t =0;
        for (int i = 0; i < sd; i++) {
            if (i>=sodu) {
                sokytu = sc -1;
            }
            for (int j = 0; j < sokytu; j++) {
                hr[i][j] = s.charAt(t);
                t++;
            }
        }
        int c = 0, d =0;
        String kq ="";
        for (int i = 0; i < n; i++) {
            kq += hr[d][c];
            d++;
            if (d==k) {
                c++;d=0;
            }
        }
        System.err.println("Bản giải mã: "+kq);
        return kq;
    }
}
