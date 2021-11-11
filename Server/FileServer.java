import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileServer implements FileInterface{

   public static int count = 0;
   //Code to start and run the server (learnt from the labs)
   public FileServer(){
   }
   public static void main(String argv[]) {
      if(System.getSecurityManager() == null) {
         System.setSecurityManager(new SecurityManager());
      }
      try {
         FileServer obj = new FileServer();

			FileInterface stub = (FileInterface) UnicastRemoteObject.exportObject(obj,0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("FileServer",stub);

         //indicate command line that the server is running
			System.out.println("Server Ready service is running...");
         
      } catch(Exception e) {
         System.out.println("FileServer: "+e.getMessage());
         e.printStackTrace();
      }
   }


   //Code that executes when the client chooses to download a file from the server.
   @Override
   public byte[] downloadFile(String fileName) throws RemoteException {
      try {
         System.out.println(UnicastRemoteObject.getClientHost());
         File file = new File(fileName);
         byte buffer[] = new byte[(int)file.length()];
         BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileName));
         input.read(buffer,0,buffer.length);
         input.close();
         return(buffer);
      } catch(Exception e){
         System.out.println("FileImpl: "+e.getMessage());
         e.printStackTrace();
         return(null);
   }
   }

   //Code that executes when the client chooses to upload a file to the server
   @Override
   public void uploadFile(byte[] content, String fileName, String search) throws RemoteException {
      // TODO Auto-generated method stub
         try{
            String cIP = RemoteServer.getClientHost();
             File file = new File(fileName);
             byte[] buffer = content;
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(fileName));
             System.out.println(cIP + " is uploading " + fileName);
             
             //Command to convert bytes to string so it can be manipulated easily
             String translate = new String(buffer, StandardCharsets.UTF_8);
             buffer=change(translate, search);
             output.write(buffer,0,buffer.length);
             System.out.println(fileName + " successfully uploaded");
             output.close();
         }catch(Exception e){
            System.out.println("FileImpl: "+e.getMessage());
            e.printStackTrace();
            return;
      }
   }

   

   //A function made by me which uses regex expression to filter ip addresses and query the hostnames.
   public byte[] change(String content, String search) throws IOException {

      //The regex expression
      String IPADDRESS_PATTERN = 
        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

      
      Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
      Matcher matcher = pattern.matcher(content);
      String result="", host="";
     
      //While loop that executes until it fings the last occurance of the regex expression.
      
        while(matcher.find()) {
           //Iterate only if the ip address matches the searching string by the user
           if(content.substring(matcher.start(),matcher.end()).substring(0, content.substring(matcher.start(),matcher.end()).indexOf('.')).equalsIgnoreCase(search)){
               count++;
               //System.out.println("found: " + count + " : "
                     //+ matcher.start() + " - " + matcher.end());
               
               //Prints the ip address
               System.out.print(content.substring(matcher.start(),matcher.end()));
               System.out.print(" ");

               //Code to query hostname of the ip address
               try {
                  InetAddress ia = InetAddress.getByName(content.substring(matcher.start(),matcher.end()));
                  System.out.println(ia.getCanonicalHostName());
                  host=ia.getCanonicalHostName();
               } catch (UnknownHostException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               
               //Creates a master string with all data needed to be put back into the textfile.
               result+= content.substring(matcher.start(),matcher.end())+" --> "+host + "\n";
           }
         }

        //Converts back to bytes so server can use it.
      return result.getBytes();
   }
   public int returnCount(){
      return count;
   }
}