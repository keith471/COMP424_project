# TODO

- run through optiminimax once in debug mode
- try OptiMinimax on Trottier servers
- add new class AlphaBeta minimax that improves upon Minimax

- add a new class - OptMinimax - that remembers the tree so that you don't have to rebuild parts of it each move
	- get results
- add a new class OptiAlphaBetaMinimax that remembers the tree AND uses alpha beta pruning
	- get results

- improve the function! there are other important things besides difference in score between you and opponent
	- magnitude of score (higher the magnitude, the closer you are to winning!)
	- number of beans on your side (maybe?)
	- etc.
	- incrementally improve function and get results for report
- improve get first move