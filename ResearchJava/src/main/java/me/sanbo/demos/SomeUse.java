package me.sanbo.demos;

public class SomeUse {
    public static void main(String[] args) {
        AbstractClass abc = new AbstractClass() {
            @Override
            public void methodA(String info) {
                System.out.println("It's in abc.methodA()");
                super.methodA(info);
            }
        };
        abc.new NNClass("八戒").methodB();
        abc.methodA("悟空");
    }

    public abstract static class AbstractClass {

        public void methodA(String info) {
            System.out.println("It's AbstractClass.methodA(\"" + info + "\")");
        }

        public class NNClass {
            private String mStr = null;

            public NNClass(String str) {
                this.mStr = str;
                System.out.println("It's in NNClass(\"" + str + "\")");
            }

            public void methodB() {
                System.out.println("It's in NNClass.methodB() ");
            }
        }
    }

}