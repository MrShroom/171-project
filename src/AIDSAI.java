//Shaun McThomas #13828643
//Matthew Yefima #37442442
import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.Comparator;
import java.util.PriorityQueue;

public class AIDSAI extends CKPlayer {

	private long startTime;
	private boolean stop = false;
	private int decideTime = 20;
	private byte otherPlayer;
	private int startDepth = 1;
	private int maxNumMoves = 0;
	private long [][] lastMoveMaxScore,lastMoveMinScore;	
	private static final long INFINITY = Long.MAX_VALUE;
	
	private class PointAndValue
	{
		Point point = null;
		long value = 0;
	}
	
	public AIDSAI(byte player, BoardModel state) 
	{
		super(player, state);
		EvaluateState.setCompacity(state, player);
		otherPlayer = (byte) (this.player == 1 ? 2 : 1);
		lastMoveMaxScore = new long [state.getWidth()][state.getHeight()];
		lastMoveMinScore = new long [state.getWidth()][state.getHeight()];
		for(int i = 0; i < state.getWidth(); i++)
			for (int j = 0 ; j < state.getHeight(); j++)
			{
				lastMoveMaxScore[i][j]= -INFINITY;
				lastMoveMinScore[i][j]= INFINITY;
			}
		teamName = "TheArtificialIntelligenceDevelopmentStruggle";
	}

	@Override
	public Point getMove(BoardModel state) 
	{
		return getMove(state, Integer.MAX_VALUE);
	}

	@Override
	public Point getMove(BoardModel state, int deadline) 
	{
		startTime = System.currentTimeMillis();
		stop = false;
		maxNumMoves = 0;
		//hard code first move.
		if (state.spacesLeft == state.getHeight() * state.getWidth())
		{			
			Point startPoint = new Point( state.getWidth()/2, state.getHeight()/2);
			goLearn(state.placePiece(startPoint, player), deadline);
			return startPoint;
		}		
		
		PriorityQueue<Point> moves = getPossibleMove(state, player);		
		PointAndValue current = new PointAndValue();
		Point bestSoFar = winTest(state, moves, deadline);

		int depth;
		long goingForValue = 0;		
		for(depth = startDepth; !stop ; depth += 1)
		{			
			current = minMax(-INFINITY, INFINITY ,state, deadline, depth, true);
			if (!stop)
			{
				bestSoFar = current.point;
				goingForValue = current.value;
			}
		}
		
		long stopTime = System.currentTimeMillis() - startTime;
		System.out.println("We went " + (depth - 1) + " levels deep!");
		System.out.println("Time spent:" + stopTime/1000.0);
		System.out.println("Going for:" + goingForValue);
		System.out.println("maxNumMoves:" + maxNumMoves);
		if( deadline - stopTime  < 10)//we cut it too close
			decideTime *= 2;
		return bestSoFar; 
	} 
	
	private void goLearn(BoardModel state, int deadline)
	{
		for(int depth = startDepth; !stop ; ++depth)
		{
			minMax(-INFINITY, INFINITY , state, deadline, depth, false);
		}
	}
	
	private  Point winTest(BoardModel state, PriorityQueue<Point> moves, int deadline)
	{
		for(Point myMove : moves)
		{
			if( state.placePiece(myMove, player).winner() == player 
					|| state.placePiece(myMove, player).winner() == 0)
			{
				stop = true;
				return myMove;
			}
		}
		for(Point myMove : moves)
		{
			if(state.placePiece(myMove, otherPlayer).winner() == otherPlayer)
			{
				goLearn(state.placePiece(myMove, player), deadline);
				return myMove;
			}
		}
		return null;
	}
	
	private PointAndValue minMax( long alpha, long beta, BoardModel state, int deadline, int depth, boolean max )
	{
		PointAndValue v = new PointAndValue();
		byte currentPlayer = (max ? player : otherPlayer);
		
		if(state.winner() == player)
		{
			v.value = INFINITY;
			v.point = state.lastMove;
			return v;
		}	
		
		if(state.winner() == otherPlayer)
		{
			v.value = -INFINITY;
			v.point = state.lastMove;
			return v;			
		}
			
		if(state.winner() == 0)
		{
			v.value = 0;
			v.point = state.lastMove;
			return v;			
		}
		
		if (stop || System.currentTimeMillis() > (startTime + deadline - decideTime))
		{			
			stop = true;
			return v;
		}     
		
		if(depth == 0)
		{			
			v.value = EvaluateState.evaluate(state, this.player);
			maxNumMoves ++;
			v.point = state.lastMove;
			return v;
		}		
		 
		PriorityQueue<Point> moves = getPossibleMove(state, currentPlayer);
		Point myMove;
		v.value = (max ? -1 : 1) * INFINITY;
		
		PointAndValue w; 
		while(!moves.isEmpty())
		{
			myMove = moves.poll();
			w = minMax(alpha, beta , state.placePiece(myMove, currentPlayer), deadline, depth - 1, (!max));
				
			if(max)
			{
				if(w.value >= v.value)
				{
					v.value = w.value;
					v.point = myMove;
				}	
				alpha = Math.max(alpha, v.value);
			}
			else 
			{
				if(w.value <= v.value)
				{
					v.value = w.value;
					v.point = myMove;
				}
				beta = Math.min(beta, v.value);
			}			
			if (alpha >= beta)
				break;
		}	
		
		updateTimesWasInBest(v, currentPlayer);
		return v;		
	}
	
	public class MaxCompare implements Comparator<Point>
	{
		@Override
		public int compare(Point arg0, Point arg1) 
		{
			if (lastMoveMaxScore[arg1.x][arg1.y] > lastMoveMaxScore[arg0.x][arg0.y])
				return -1;
			if (lastMoveMaxScore[arg1.x][arg1.y] < lastMoveMaxScore[arg0.x][arg0.y])
				return 1;
			return 0;
		}
		
	}
	public class MinCompare implements Comparator<Point>
	{
		@Override
		public int compare(Point arg0, Point arg1) 
		{		
			if (lastMoveMinScore[arg1.x][arg1.y] > lastMoveMinScore[arg0.x][arg0.y])
				return -1;
			if (lastMoveMinScore[arg1.x][arg1.y] < lastMoveMinScore[arg0.x][arg0.y])
				return 1;
			return 0;
		}
		
	}
	
	private PriorityQueue<Point> getPossibleMove(BoardModel state, byte currentPlayer) 
	{
		PriorityQueue<Point> output;
		if (currentPlayer == player)
			output = new PriorityQueue<Point>(state.spacesLeft, new MaxCompare());
		else
			output = new PriorityQueue<Point>(state.spacesLeft, new MinCompare());
		
		if (!state.gravityEnabled())
			for(int j = 0; j < state.getHeight(); j++)
			{
				for (int i = 0 ; i < state.getWidth(); i++)
				{
					if(state.getSpace(i, j ) == 1 || state.getSpace(i,j) == 2)
					{
						findLocalMoves(state,i,j,output);
					}
					//else
						//output.add(new Point (i,j));
				}
			}
		else
			for (int i = 0 ; i < state.getWidth(); i++)
			{
				if( state.getSpace(i, state.getHeight()-1 ) != 1 
						&& state.getSpace(i,state.getHeight()-1) != 2)
				{
					output.add(new Point(i,state.getHeight()-1));
				}
			}
		return output;
	}

	private void findLocalMoves(BoardModel state, int x, int y, PriorityQueue<Point> output) 
	{	
		for(int i = ( x <= 1 ? 0 : x - 1 ) ; 
			i <= x + 1 && i < state.getWidth() ;
			i++)
		{
			for(int j = ( y <= 1 ? 0 : y - 1 ) ; 
			j <= y + 1 && j < state.getHeight() ;
			j++)
			{
				if(state.getSpace(i, j ) != 1 && state.getSpace(i,j) != 2)
					output.add(new Point(i,j));
			}
			
		}
	}
	
	private void updateTimesWasInBest(PointAndValue v, byte currentPlayer)
	{
		if(v == null || v.point == null )
			return;
		if (currentPlayer == player)
			lastMoveMaxScore[v.point.x][v.point.y] = v.value;
		else
			lastMoveMinScore[v.point.x][v.point.y] = v.value;
	}
}
