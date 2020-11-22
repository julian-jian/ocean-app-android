package com.sky.lamp;

import org.junit.Test;

public class Solution {

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
    @Test
    public void test() {
        int nums[] = {2,5,9,6,9,3,8,9,7,1};
        new Solution().findDuplicate(nums);
    }

    public int findDuplicate(int[] nums) {
        // 第一步找到环，使用快慢指针找到环
        int slow = 0, fast = 0;
        do {
            slow = nums[slow];
            fast = nums[fast];
            fast = nums[fast];
        } while (slow != fast);
        // 第二步找到环的起始点，起始点就是重复的数字
        slow = 0;
        do{
            slow = nums[slow];
            fast = nums[fast];
        } while(slow != fast);
        return nums[slow];
    }

//    class Solution {
//        public int findDuplicate(int[] nums) {
//            int slow = 0, fast = 0;
//            do {
//                slow = nums[slow];
//                fast = nums[nums[fast]];
//            } while (slow != fast);
//            slow = 0;
//            while (slow != fast) {
//                slow = nums[slow];
//                fast = nums[fast];
//            }
//            return slow;
//        }
//    }

}