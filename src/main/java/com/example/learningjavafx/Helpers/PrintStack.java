package com.example.learningjavafx.Helpers;

// will contain a stack of messages and show them accordingly.

import com.example.learningjavafx.RunnableApplication;
import javafx.scene.control.Label;

import java.util.Iterator;
import java.util.Stack;

public class PrintStack {
    private Stack<String> stack;
    private int maxLength;

    public PrintStack(int maxLength) {
        this.stack = new Stack<>();
        this.maxLength = maxLength;
    }
    public PrintStack() {
        this(5);
    }

    public void push(String message) {
        if (this.stack.size() == maxLength) stack.pop();
        stack.push(new String(message));
        render();
    }

    private void render() {
        Label label = (Label) RunnableApplication.scene.lookup("#systemupdate");
        StringBuilder text = new StringBuilder();
        for (String s : this.stack) text.append(s+"\n");
        label.setText(text.toString());
    }

    public void clear() {
        this.stack.clear();
    }
}
