//Shaun McThomas #13828643
//Matthew Yefima #37442442
import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.sun.jmx.remote.internal.ArrayQueue;

public class AIDSAI extends CKPlayer {

	public long startTime;
	public boolean print = true, stop = false;
	public long decideTime = 20;
	public byte otherPlayer;
	
	public class PointAndValue
	{
		Point point;
		Integer value;
	}
	
	public AIDSAI(byte player, BoardModel state) {
		super(player, state);
		EvaluateState.setCompacity();
		teamName = "TheArtificialIntelligenceDevelopmentStruggle";
		otherPlayer = (byte) (this.player == 1 ? 2 : 1);
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
		print = true;
		stop = false;
		//hard code first move. 
		if (state.spacesLeft == state.getHeight() * state.getWidth())
		{			
			return new Point( state.getWidth()/2, state.getHeight()/2);
		}
		
		int depth = 1;
		PointAndValue current = topMax(state, deadline, depth);
		Point bestSoFar = current.point;
		
		do{
			if(current.value == Integer.MAX_VALUE)
				break;
			 ++depth;
			current = topMax(state, deadline, depth);
			if (stop)
				break;			
			bestSoFar = current.point;
			
		}while(!stop);
		
		long stopTime = System.currentTimeMillis()- startTime;
		System.out.println("We went " + depth + " levels deep!");
		System.out.println("Time spent:" + stopTime/1000.0);
		
		if(stopTime - deadline > 10)//we cut it too close
			decideTime *= 2;
		return bestSoFar; 
	} 
	
	public PointAndValue topMax(BoardModel state, long deadline, int depth )
	{
		
		Set<Point> moves = getPossibleMove(state);
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
		
		PointAndValue best =new PointAndValue();
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
			
		if(state.winner() == this.player)
		{
			v.value = Integer.MAX_VALUE;
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
			v.value = heuristic(state);
			v.point = state.getLastMove();			
			return v;
		}
		
		v.value = Integer.MIN_VALUE; 
		Set<Point> moves = getPossibleMove(state);
		
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
		return v;		
	}
	
	private PointAndValue minPlay(Integer alpha, Integer beta, BoardModel state, long deadline, int depth )
	{
		PointAndValue v = new PointAndValue();
		
		if(state.winner() == this.player)
		{
			v.value = Integer.MAX_VALUE;
			v.point = state.getLastMove();
			return v;
		}
		
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
			stop = true;
			v.value = 0;
			v.point = null;			
			return v;
		}
		
		if (depth == 0)
		{			
			v.value = heuristic(state);
			v.point = state.getLastMove();
			return v;
		}
		
		v.value = Integer.MAX_VALUE; 			
		Set<Point> moves = getPossibleMove(state);
		
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
		return v;
	}
	
	private Set<Point> getPossibleMove(BoardModel state) 
	{
		Set<Point> output = new HashSet<Point>();
		if (!state.gravityEnabled())
			for(int j = 0; j < state.getHeight(); j++)
			{
				for (int i = 0 ; i < state.getWidth(); i++)
				{
					if(state.getSpace(i, j ) == 1 || state.getSpace(i,j) == 2)
					{
						output.addAll(findLocalMoves(state,i,j));
					}
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

	private Set<Point> findLocalMoves(BoardModel state, int x, int y) 
	{
		Set<Point> output = new HashSet<Point>();
		
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
		return output;
	}
	
	private int heuristic(BoardModel state)
	{ 
		return EvaluateState.evaluate(state, this.player);
	}	

	
}
