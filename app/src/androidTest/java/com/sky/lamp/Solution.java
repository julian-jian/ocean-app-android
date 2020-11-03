package com.sky.lamp;

public class Solution {

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public boolean isPalindrome(ListNode head) {
        ListNode slow = head;
        ListNode fast = slow.next;
        boolean isPalindrome = false;
        while (fast == null || slow ==null) {
            if (slow == fast) {
                isPalindrome = true;
                break;
            }
            slow = slow.next;
            fast = fast.next;
            if (fast != null) {
                fast = fast.next;
            }
        }
        return isPalindrome;
    }
}