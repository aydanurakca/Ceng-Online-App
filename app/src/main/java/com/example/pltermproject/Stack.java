package com.example.pltermproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Stack{

    private int top;
    private Object [] elements;

    Stack(int capacity) {
        elements = new Object [capacity];
        top = -1;
    }

    void push(Object data) {
        if(isFull())
            System.out.println("Stack overflow");
        else {
            top++;
            elements[top] = data;
        }
    }

    Object pop() {
        if(isEmpty()) {
            System.out.println("Stack is empty");
            return null;
        }
        else {
            Object retData = elements[top];
            top--;
            return retData;
        }
    }

    Object peek() {
        if(isEmpty()) {
            System.out.println("Stack is empty.");
            return null;
        }
        else
            return elements[top];
    }

    boolean isEmpty() {
        return (top == -1);
    }

    boolean isFull() {
        return (top + 1 == elements.length);
    }

    int size() {
        return top + 1;
    }

    public Stack sortstack(Stack input) throws ParseException {

        Stack tmpStack = new Stack (input.size());
        while(!input.isEmpty())
        {
            // pop out the first element
            Date tmp = (Date) input.pop();

            // while temporary stack is not empty and
            // top of stack is greater than temp
            if(!tmpStack.isEmpty()){
                while(!tmpStack.isEmpty() && ((Date) tmpStack.peek()).after(tmp))
                {
                    input.push(tmpStack.pop());

                }
            }
            // push temp in tempory of stack
            tmpStack.push(tmp);
        }
        return tmpStack;
    }
}
