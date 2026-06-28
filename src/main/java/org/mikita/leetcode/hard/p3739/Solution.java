package org.mikita.leetcode.hard.p3739;

public class Solution {

    public long countMajoritySubarrays(int[] nums, int target) {
        int n = nums.length;
        boolean noTargetElementsArePresent = true;

        for(int i = 0; i < n; i++) {
            if(nums[i] == target) {
                nums[i] = 1;
                noTargetElementsArePresent = false;
            } else {
                nums[i] = -1;
            }
        }

        if(noTargetElementsArePresent) {
            return 0;
        }

        int[] prefixSum = new int[n + 1];
        int max = 0;
        int min = 0;

        for(int i = 1; i <= n; i++) {
            prefixSum[i] = prefixSum[i - 1] + nums[i - 1];
            max = Math.max(prefixSum[i], max);
            min = Math.min(prefixSum[i], min);
        }

        long result = 0;
        SegmentTree segmentTree = new SegmentTree(max - min + 1);

        for(int i = 0; i <= n; i++) {
            int current = prefixSum[i] - min;
            long tmp = segmentTree.query(current - 1);
            segmentTree.update(current, 1);
            result += tmp;
        }

        return result;
    }

    private static class SegmentTree {

        private final int size;
        private final long[] nodes;

        public SegmentTree(int size) {
            this.size = size;
            nodes = new long[size * 4];
        }

        public void update(int index, long value) {
            update(0, 0, size - 1, index, value);
        }

        private void update(int node, int left, int right, int index, long value) {
            if(left == right) {
                nodes[node] += value;
                return;
            }

            int mid = left + (right - left) / 2;

            if(index <= mid) {
                update(node * 2 + 1, left, mid, index, value);
            } else {
                update(node * 2 + 2, mid + 1, right, index, value);
            }

            nodes[node] = nodes[node * 2 + 1] + nodes[node * 2 + 2];
        }

        public long query(int target) {
            return query(0, 0, size - 1, 0, target);
        }

        private long query(int node, int left, int right, int qLeft, int qRight) {
            if(qLeft > right || qRight < left) {
                return 0;
            }

            if(qLeft <= left && right <= qRight) {
                return nodes[node];
            }

            int mid = left + (right - left) / 2;

            return query(node * 2 + 1, left, mid, qLeft, qRight) +
                    query(node * 2 + 2, mid + 1, right, qLeft, qRight);
        }
    }
}
