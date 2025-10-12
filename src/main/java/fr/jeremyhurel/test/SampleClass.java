package fr.jeremyhurel.test;

public class SampleClass {

    public void methodA() {
        methodB();
        methodC();
    }

    public void methodB() {
        methodD();
    }

    public void methodC() {
        System.out.println("Method C");
    }

    private void methodD() {
        methodE();
    }

    private void methodE() {
        System.out.println("Method E");
    }

    public static void main(String[] args) {
        SampleClass sample = new SampleClass();
        sample.methodA();
    }
}