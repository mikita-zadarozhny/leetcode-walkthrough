# 3739. Count Subarrays With Majority Element II

## Description

You are given an integer array `nums` and an integer `target`.
Return the number of subarrays of nums in which target is the majority element.
The majority element of a subarray is the element that appears 
strictly more than half of the times in that subarray.

## Constraints

- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^9`
- `1 <= target <= 10^9`

## Intuition

Each array `nums` contains only two types of elements, namely those that
are equal to `target` and those that are not. Thereof, it is a good idea
to get rid of redundant information, namely replace all `target` integers
with `1` and others with `-1`. For example, an array `nums` with values 
`[1,2,2,3]` and `2` as `target` will be transformed into `[-1, 1, 1, -1]`. 
``` java 
int n = nums.length;

for(int i = 0; i < n; i++) {
    if(nums[i] == target) {
        nums[i] = 1;
    } else {
        nums[i] = -1;
    }
}
```

If there are no elements that are equal to `target`, then there couldn't
any subarray with majority of elements equal to `target`. Thereof, the
following adjustment is valid:
``` java
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
```

Number of elements that are equal to `target` should be strictly greater 
than the number of others elements int `nums` for a subarray to consider 
it in the result. Considering the previous transformation, the sum of 
a subarray should be more than `0`.

How is it possible to calculate a sum of an arbitrary subarray in `O(1)`?
It is possible to achieve using prefix sums. Namely, `[-1, 1, 1, -1]`
will have the following prefix sum array `[0, -1, 0, 1, 0]`. For example,
given a subarray `[i, j]`, then its sum might be calculated using the
following formula: `prefixSum[j] - prefixSum[i]`. If the result is more
than `0`, then the majority of elements are equal to `target`. 
``` java
int[] prefixSum = new int[n + 1];

for(int i = 1; i <= n; i++) {
    prefixSum[i] = prefixSum[i - 1] + nums[i - 1];
}
```

Brute force solution against prefix sum would require `O(n^2)` time to
get through all possible subarrays, which is not quite efficient and
would result into TLE, since constrains are quite large, namely a length 
of `nums` might be as large as `10^9`, which eventually results into
`10^18` operations.

It is necessary to mention the following key observations:
- If the majority of elements of a subarray are equal to `target`, then
adding an additional `target` element to it will never ruin
the aforementioned majority.
- If the half of elements of a subarray are equal to `target`, then
adding an additional `target` element to it will make a majority of
`target` elements for this subarray.

Considering the aforementioned observations, there should be a way to
efficiently query all the previous subarrays that meet the requirement.
It is possible to achieve using segment tree data structure, which 
requires only `O(log(n))` time for both update and query operations.

Then we should start iterating over the prefix sum array, querying 
number of the previous subarrays that matches a criteria, and before
completing the current iteration, we should store the current subarray
occurrence in this segment tree. See the following code snippet:
``` java
for(int i = 0; i <= n; i++) {
    int current = prefixSum[i] - min;
    long tmp = segmentTree.query(current - 1);
    segmentTree.update(current, 1);
    result += tmp;
}
```

## Solution

``` java
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
```