package com.sky.lamp;

import java.util.Arrays;
import java.util.Random;

public class Solution {
    int origin[];
    public Solution(int[] nums) {
        origin = nums;
    }

    /** Resets the array to its original configuration and return it. */
    public int[] reset() {
        return origin;
    }

    /** Returns a random shuffling of the array. */
    public int[] shuffle() {
        int shuffer[] = Arrays.copyOf(origin,origin.length);
        for (int i = 0; i < shuffer.length; i++) {
            int tmp = shuffer[i];
            int randxiabiao = new Random().nextInt(shuffer.length - i) + i;
            shuffer[i] = shuffer[randxiabiao];
            shuffer[randxiabiao] = tmp;
        }
        return shuffer;
    }
}

/**
 * Your Solution object will be instantiated and called as such:
 * Solution obj = new Solution(nums);
 * int[] param_1 = obj.reset();
 * int[] param_2 = obj.shuffle();
 */