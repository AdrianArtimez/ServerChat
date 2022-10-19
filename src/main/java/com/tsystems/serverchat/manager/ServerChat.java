/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tsystems.serverchat.manager;

import com.tsystems.serverchat.models.Message;
import com.tsystems.serverchat.models.Chat;
import static com.tsystems.serverchat.ConnectionDetails.*;
import com.tsystems.serverchat.models.User;
import com.tsystems.serverchat.models.UserSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Runs the server client and setups a Socker w/r that sends messages to a Chat.
 *
 * @author aalonsoa
 */
public class ServerChat {

    private ServerSocket serverSocket;
    private ArrayList<UserSocket> clientSock;
    private ArrayList<Message> unProcessText;
    private ReentrantLock lock;
    private ExecutorService executorService;

    public ServerChat() throws IOException {
        serverSocket = new ServerSocket(PORT);
        clientSock = new ArrayList<>();
        unProcessText = new ArrayList<>();
        lock = new ReentrantLock();
        executorService = Executors.newFixedThreadPool(22);
    }

    /**
     * Runs the server that will listen for connections until it is shutdown.
     *
     * @throws IOException If there is no connection to the server.
     */
    public void run() throws IOException {

        startThreads();
        
        while (true) {

            InetAddress ia = InetAddress.getLocalHost();
            System.out.println("ip:" + ia);
            System.out.println("Server listening for a connection");
            Socket clientSocket = serverSocket.accept();
            
            ThreadLogin tl= new ThreadLogin(clientSocket, clientSock);
            //executorService.execute(tl);
            
            
            //////OLD TEST TO MAKE THE SERVER UP
            System.out.println("Received connection ");
            UserSocket temp=new UserSocket(new User("asdas","asdasd"), clientSocket);
            clientSock.add(temp);
            //////
            
            

        }

    }

    private void startThreads() {
        ThreadReader tr = new ThreadReader(clientSock, unProcessText, lock);
        Chat chat = new Chat("All", clientSock);
        ThreadWriter tw = new ThreadWriter(unProcessText, chat, lock);
    
        executorService.execute(tr);
        executorService.execute(tw);
    }


    private void process(String read) {
        //SEND MESAJE TO THE CHAT
    }

}
