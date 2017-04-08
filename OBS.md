# Observations

- timeout can happen quite easily!!
	- you should be sure to cap the max depth that you simulate out well below 700ms
- optiMinimax is actually slower! My best guess: we are writing lots of nodes to memory, eventually running out of L2 cache and spilling into L3 and potentially beyond. Thus, many reads require more time, so we don't actually save any time.
	- up to seven possible moves each time (including skip) so O(7^n) nodes in the tree where n is the depth we have gone --> huge number!