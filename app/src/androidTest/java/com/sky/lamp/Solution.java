package com.sky.lamp;

import org.junit.Test;

public class Solution {
    @Test
    public void test() {
        int ar[] = new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        new Solution().maxSubArray(ar);
    }
    public int maxSubArray(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        int max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int preMax = Math.max(max, nums[i]);
            max = Math.max(max, max + preMax);
        }
        return max;
    }
}