import java.util.Scanner;

public class Main {

    public static void main(String args[]) {
        print_beginning();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] tokens = line.split("\\s+");
            if (tokens.length == 1) {
                if (tokens[0].equals("a") || tokens[0].equals("b") || tokens[0].equals("c")) {
                    print_cls_out(tokens[0].charAt(0));
                } else {
                    print_arr_out(Integer.parseInt(tokens[0]));
                }
            } else {
                if (tokens[0].equals("a") || tokens[0].equals("b") || tokens[0].equals("c")) {
                    print_cls_in(tokens[0].charAt(0), Integer.parseInt(tokens[1]));
                } else {
                    print_arr_in(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
                }
            }
        }

        print_ending();
    }

    public static void print_beginning() {
        System.out.println(".global start");
        System.out.println("start:");
        System.out.println("call init_structs");
        System.out.println("nop");
        System.out.println();
    }

    public static void print_arr_out(int i) {
        System.out.printf("mov %d, %%o0%n", i);
        System.out.println("call print_arr");
        System.out.println("nop");
        System.out.println();
    }

    public static void print_arr_in(int i, int v) {
        System.out.printf("mov %d, %%o0%n", i);
        System.out.printf("mov %d, %%o1%n", v);
        System.out.println("call set_arr");
        System.out.println("nop");
        System.out.println();
    }

    public static void print_cls_out(char c) {
        System.out.printf("mov %d, %%o0%n", (int) c);
        System.out.println("call print_cls");
        System.out.println("nop");
        System.out.println();
    }

    public static void print_cls_in(char c, int v) {
        System.out.printf("mov %d, %%o0%n", (int) c);
        System.out.printf("mov %d, %%o1%n", v);
        System.out.println("call set_cls");
        System.out.println("nop");
        System.out.println();
    }

    public static void print_ending() {
        System.out.println("exit_program");
    }

}
