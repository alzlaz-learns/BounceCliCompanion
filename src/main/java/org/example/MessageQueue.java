package org.example;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MessageQueue {

    private static Queue<Pair<String, String>> messageQ = new LinkedList<>();

    public static void queueMessage(String phoneNumber, String message) {
        messageQ.add(new ImmutablePair<>(phoneNumber, message));
    }

    static boolean toPush(){
        return !messageQ.isEmpty();
    }

    static Pair<String, String> pushMessage(){
        Pair<String, String> test = messageQ.poll();
//        System.out.println("here");
//        System.out.println("PhoneNumber: " + test.getKey() + ", Message: " + test.getValue());
        return test;
    }
}
