package org.example;
import java.util.Scanner;

public class ConsoleInputReader implements InputReader{
    private Scanner sc;

    public ConsoleInputReader(){
        sc = new Scanner(System.in);
    }

    @Override
    public String readInput() {
        return sc.nextLine();
    }

    @Override
    public void close() {
        sc.close();
    }
}
