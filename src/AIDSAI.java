//Shaun McThomas #13828643
//Matthew Yefima #37442442
import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.sun.jmx.remote.internal.ArrayQueue;

public class AIDSAI extends CKPlayer {

	private long startTime;
	private boolean stop = false;
	private int decideTime = 20;
	private byte otherPlayer;
	private int startDepth = 6;
	private Map<Point,Integer> timesWasInBest;
	
	
	private class PointAndValue
	{
		Point point;
		Integer value;
	}
	
	public AIDSAI(byte player, BoardModel state) 
	{
		super(player, state);
		EvaluateState.setCompacity();
		otherPlayer = (byte) (this.player == 1 ? 2 : 1);
		timesWasInBest = new HashMap<Point,Integer>(state.spacesLeft);
		for(int j = 0; j < state.getHeight(); j++)
			for (int i = 0 ; i < state.getWidth(); i++)
				timesWasInBest.put(new Point(i,j), 0);
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
		//hard code first move. 
		if (state.spacesLeft == state.getHeight() * state.getWidth())
		{			
			return new Point( state.getWidth()/2, state.getHeight()/2);
		}
		
		int depth = 1;
		PointAndValue current = topMax(state, deadline, depth);
		Point bestSoFar = current.point;
		

		for(depth = startDepth; !stop  && current.value != Integer.MAX_VALUE; ++depth)
		{
			current = topMax(state, deadline, depth);
			if (!stop)
				bestSoFar = current.point;			
		}
		
		long stopTime = System.currentTimeMillis()- startTime;
		System.out.println("We went " + depth + " levels deep!");
		System.out.println("Time spent:" + stopTime/1000.0);
		
		if( deadline - stopTime  < 10)//we cut it too close
			decideTime *= 2;
		return bestSoFar; 
	} 
	
	private PointAndValue topMax(BoardModel state, long deadline, int depth )
	{
		
		Set<Point> moves = getPossibleMove(state, player);
		ArrayQueue<PointAndValue> choices = new ArrayQueue<PointAndValue>(moves.size());
		
		for(Point myMove : moves)
		{
			Integer alpha = Integer.MIN_VALUE;
			Integer beta = Integer.MAX_VALUE;
			PointAndValue v = minPlay(alpha, beta , state.placePiece(myMove, this.player), deadline, depth - 1);
			v.point = myMove;
			if(depth == 1 && state.placePiece(myMove, otherPlayer).winner() == otherPlayer)
			{ 	
				stop = true;
				return v;
			}
			choices.add(v);
		}
		if(depth == 1 && choices.size() < 2)
			stop = true;
		
		PointAndValue best = new PointAndValue();
		best.value = Integer.MIN_VALUE;
		for(PointAndValue choice : choices)
		{				
			if(choice.value >= best.value )
			{
				best.point = choice.point;
				best.value = choice.value;
			}
		}		
		return best;
	}
	
	private PointAndValue maxPlay( Integer alpha, Integer beta, BoardModel state, long deadline, int depth )
	{
		PointAndValue v = new PointAndValue();
		
		if(state.winner() == otherPlayer)
		{
			v.value = Integer.MIN_VALUE;
			v.point = state.getLastMove();
			return v;			
		}
				
		if(state.winner() == 0)
		{
			v.value = 0;
			v.point = state.getLastMove();
			return v;			
		}
		
		if (stop || System.currentTimeMillis() > (startTime + deadline - decideTime))
		{			
			v.value = 0;
			v.point = null;
			stop = true;
			return v;
		}
		
		if (depth == 0)
		{			
			v.value = EvaluateState.evaluate(state, this.player);
			v.point = state.getLastMove();			
			return v;
		}
		
		v.value = Integer.MIN_VALUE; 
		Set<Point> moves = getPossibleMove(state, player);
		
		for(Point myMove : moves)
		{
			PointAndValue w = minPlay(alpha, beta , state.placePiece(myMove, this.player), deadline, depth - 1 );
			if( w.value >= v.value  )
			{
				v.value = w.value;
				v.point = myMove;
			}			
			if( v.value >= beta)
				return v;
			alpha = Math.max(alpha, v.value);
		}	
		
		updateTimesWasInBest(v.point);
		return v;		
	}
	
	private PointAndValue minPlay(Integer alpha, Integer beta, BoardModel state, long deadline, int depth )
	{
		PointAndValue v = new PointAndValue();
		
		if(state.winner() == this.player)
		{
			v.value = Integer.MAX_VALUE;
			v.point = state.getLastMove();
			updateTimesWasInBest(v.point);
			return v;
		}
		
		if(state.winner() == 0)
		{
			v.value = 0;
			v.point = state.getLastMove();
			return v;			
		}
		
		if (stop || System.currentTimeMillis() > (startTime + deadline - decideTime))
		{
			stop = true;
			v.value = 0;
			v.point = null;			
			return v;
		}
		
		if (depth == 0)
		{			
			v.value = EvaluateState.evaluate(state, this.player);
			v.point = state.getLastMove();
			return v;
		}
		
		v.value = Integer.MAX_VALUE; 			
		Set<Point> moves = getPossibleMove(state, otherPlayer);
		
		for(Point myMove : moves)
		{
			PointAndValue w = maxPlay(alpha, beta , state.placePiece(myMove, otherPlayer),deadline, depth -1 );
			if( w.value <= v.value )
			{
				v.value = w.value;
				v.point = myMove;
			}			
			if( v.value <= alpha)
				return v;	
			beta = Math.min(beta, v.value);		
		}	
		updateTimesWasInBest(v.point);
		return v;
	}
	
	public class myCompare implements Comparator<Point>
	{

		@Override
		public int compare(Point arg0, Point arg1) 
		{		
			if (timesWasInBest.get(arg1)- timesWasInBest.get(arg0) ==0)
					return (arg1.hashCode()-arg0.hashCode());
			return timesWasInBest.get(arg1)- timesWasInBest.get(arg0);
		}
		
	}
	
	private Set<Point> getPossibleMove(BoardModel state, byte currentPlayer) 
	{
		Set<Point> output = new TreeSet<Point>(new myCompare());		
		if (!state.gravityEnabled())
			for(int j = 0; j < state.getHeight(); j++)
			{
				for (int i = 0 ; i < state.getWidth(); i++)
				{
					if(state.getSpace(i, j ) == 1 || state.getSpace(i,j) == 2)
					{
						//findLocalMoves(state,i,j,output);
					}
					else
						output.add(new Point (i,j));
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

	private void findLocalMoves(BoardModel state, int x, int y, Set<Point> output) 
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
	private void updateTimesWasInBest(Point point)
	{
		timesWasInBest.put(point, timesWasInBest.get(point)+1);
	}
}
