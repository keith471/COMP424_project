# TODO

- add ability to stop at certain depth to minimax!
- simplify to just return net score (max player score - min player score) upon reaching max depth
	- the calculations you're currently doing simply do this... no point in the extraneous calculations!
- confirm that player ids are 0 and 1 by printing them out
- build minimax move selection into StudentPlayer!
	- minimax returns null if all moves end up in a loss or if no moves are possible
		- if this is the case, then Student player should react by trying to skip
		- if no skips left, then just try a random move and hope for the best
- test it out! Should be pretty good already!
- get some results for this utility function (for the report)
- improve the function! there are other important things besides difference in score between you and opponent
	- magnitude of score (higher the magnitude, the closer you are to winning!)
	- number of beans on your side (maybe?)
	- etc.
	- incrementally improve function and get results for report