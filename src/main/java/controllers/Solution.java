package controllers;// you can also use imports, for example:
// import java.util.*;

// you can write to stdout for debugging purposes, e.g.
// System.out.println("this is a debug message");

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

class Solution {

    public static void main(String[] args) {
        Solution s = new Solution();
//        System.out.println(s.solution(new int[] {-1, 1, 2, -1}));
        System.out.println(s.solution(874589745));
    }

    public boolean solution(int[] A) {
        boolean visited = false;

        for (int i=0; i<A.length; i++) {
            int minSoFarIndex = -1;

            for (int j=i+1; j<A.length; j++) {
                if (A[i] > A[j]) {
                    minSoFarIndex = j;
                }
            }

            if (minSoFarIndex != -1) {
                if (visited) return false;
                int temp = A[i];
                A[i] = A[minSoFarIndex];
                A[minSoFarIndex] = temp;
                visited = true;
                i -= 1;
            }
        }

        return true;
    }

    public int solution(int N) {
        ArrayList<Integer> digits = getDigits(N);
        Collections.sort(digits);
        Collections.reverse(digits);

        return makeNum(digits);
    }

    public int makeNum(ArrayList<Integer> ls) {
        int highestNum = 0;

        for (Integer num : ls) {
            highestNum = highestNum*10+num;
        }

        return highestNum;
    }

    public ArrayList<Integer> getDigits(int N) {
        ArrayList<Integer> ls = new ArrayList<Integer>();

        while (N > 9) {
            int remainder = N % 10;

            if (remainder == 0) {
                N = N / 10;
            } else {
                ls.add(remainder);
                N = N - remainder;
            }
        }

        ls.add(N);

        return ls;
    }
}