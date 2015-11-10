//Shaun McThomas #13828643
//Matthew Yefima #37442442
import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class AIDSAI extends CKPlayer {

	public class PointAndValue{
		Point point;
		Integer value;
	}
	
	public AIDSAI(byte player, BoardModel state) {
		super(player, state);
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
		long startTime = System.currentTimeMillis(), timePassed=0;
		//hard code first move. 
		if (state.spacesLeft == state.getHeight() * state.getWidth())
		{
			return new Point( state.getWidth()/2, state.getHeight()/2);
		}
		PointAndValue choice = new PointAndValue();
		int depth = 3, stoploop=0;
		choice = topMax(state,depth);
		long timeThistime = System.currentTimeMillis()- startTime;
		
		while(timeThistime * 8 <= (deadline - timePassed))
		{
			long mark = System.currentTimeMillis(); 
			depth ++;
			choice = topMax(state,depth);
			if(choice.value == Integer.MAX_VALUE)
				break;
			timePassed = System.currentTimeMillis() - startTime;
			
			if( timeThistime ==(System.currentTimeMillis()- mark) &&
					++stoploop > 3)
			{
				stoploop = 0;
				break;
			}				

			timeThistime = System.currentTimeMillis()- mark;
			System.out.println("time:" + timeThistime 
					+ " deadline: " + deadline);
		}			
		return choice.point; 
	} 
	
	public PointAndValue topMax(BoardModel state, int iterationsLeft )
	{
		Set<Point> moves = getPossibleMove(state, this.player);
		Set<PointAndValue> choices = new HashSet<PointAndValue>();
		for(Point myMove : moves)
		{
			PointAndValue v = minPlay(Integer.MIN_VALUE, Integer.MAX_VALUE , state.placePiece(myMove, this.player), iterationsLeft);
			v.point = myMove;
			choices.add(v);
		}
		PointAndValue best = new PointAndValue();
		best.value = Integer.MIN_VALUE;
		for(PointAndValue choice : choices)
		{				
			if(choice.value > best.value || best.point == null)
			{
				best.point = choice.point;
				best.value = choice.value;
			}
		}
		return best;
	}
	
	private PointAndValue maxPlay( Integer alpha, Integer beta, BoardModel state, int iterationsLeft )
	{
		PointAndValue v = new PointAndValue();
		
		if( state.winner() == this.player)
		{
			v.value = Integer.MAX_VALUE;
			v.point = state.getLastMove();
			alpha = Math.max(alpha, v.value);
			return v;
		}
		
		if(state.winner() == (this.player == 1 ? 2 : 1))
		{
			v.value = Integer.MIN_VALUE;
			v.point = state.getLastMove();
			alpha = Math.max(alpha, v.value);
			return v;			
		}
		
		if(state.winner() == 0)
		{
			v.value = 0;
			v.point = state.getLastMove();
			alpha = Math.max(alpha, v.value);
			return v;			
		}
		
		if (iterationsLeft == 0 )
		{
			
			v.value = heuristic(state);
			v.point = state.getLastMove();
			alpha = Math.max(alpha, v.value);
			return v;
		}
		
		v.value = Integer.MIN_VALUE; 
		Set<Point> moves = getPossibleMove(state, this.player);
		
		for(Point myMove : moves)
		{
			PointAndValue w = minPlay(alpha, beta , state.placePiece(myMove, this.player), iterationsLeft-1 );
			w.point = myMove;
			if( w.value > v.value || v.point == null)
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
	
	private PointAndValue minPlay(Integer alpha, Integer beta, BoardModel state,int iterationsLeft )
	{
		PointAndValue v = new PointAndValue();
		byte otherPlayer = (byte) (this.player == 1 ? 2 : 1);
		
		if( state.winner() == this.player)
		{
			v.value = Integer.MAX_VALUE;
			v.point = state.getLastMove();
			beta = Math.min(beta, v.value);
			return v;
		}
		
		if(state.winner() == otherPlayer)
		{
			v.value = Integer.MIN_VALUE;
			v.point = state.getLastMove();
			beta = Math.min(beta, v.value);
			return v;			
		}
		
		if(state.winner() == 0)
		{
			v.value = 0;
			v.point = state.getLastMove();
			beta = Math.min(beta, v.value);
			return v;			
		}
		
		if (iterationsLeft == 0){
			int eval = heuristic(state);
			v.value = eval;
			v.point = state.getLastMove();
			beta = Math.min(beta, v.value);
			return v;
		}
		
		v.value = Integer.MAX_VALUE; 		
		
		Set<Point> moves = getPossibleMove(state, this.player);
				
		for(Point myMove : moves)
		{
			PointAndValue w = maxPlay(alpha, beta , state.placePiece(myMove, otherPlayer), iterationsLeft-1 );
			w.point = myMove;
			if( w.value < v.value || v.point == null)
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
	
	private Set<Point> getPossibleMove(BoardModel state, byte player) 
	{
		Set<Point> output = new HashSet<Point>();
		for(int j = 0; j < (state.gravityEnabled() ? 1 : state.getHeight()); j++)
		{
			for (int i = 0 ; i < state.getWidth(); i++)
			{
				if(state.getSpace(i, j ) == player || state.getSpace(i,j) == (player == 1 ? 2 : 1))
				{
					output.addAll(findLocalMoves(state,i,j, player));
				}
			}
		}
		
		return output;
	}

	private Set<Point> findLocalMoves(BoardModel state, int x, int y, byte player) 
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
				if(state.getSpace(i, j ) != player && state.getSpace(i,j) != (player == 1 ? 2 : 1))
					output.add(new Point(i,j));
			}
			
		}
		return output;
	}

	public void printBoard(BoardModel state)
	{
		System.out.println("****************************");
		for (int j = state.getHeight()-1 ; j >= 0; j--)
		{
			for(int i = 0; i < state.getWidth(); i++)
			{	
				System.out.print("| " + state.getSpace(i,j) + " |" );
			}
			System.out.println();
		}
		System.out.println("****************************");
	}

	private int heuristic(BoardModel state)
	{ 
		if (state.gravity)
			return EvaluateState.evaluate(state, this.player);
		else
			return EvaluateState.evaluate(state, this.player);
	}	

	
}
