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
		teamName = "Artificial Intelligence Development Struggle";
	}

	@Override
	public Point getMove(BoardModel state) 
	{
		if (state.spacesLeft == state.getHeight() * state.getWidth())
		{
			return new Point(state.getHeight()/2, state.getWidth()/2);
		}
		PointAndValue v = maxPlay(Integer.MIN_VALUE, Integer.MAX_VALUE, state, 4 );
		return v.point;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	private PointAndValue maxPlay(Integer alpha, Integer beta, BoardModel state, int iterationsLeft )
	{
		
		if (iterationsLeft == 0 || state.winner() != -1){
			int eval = heuristic(state);
			PointAndValue v = new PointAndValue();
			v.value = eval;
			v.point = state.getLastMove();
			return v;
		}
		PointAndValue v = new PointAndValue();
		v.value = Integer.MIN_VALUE; 
		Set<Point> moves = getPossibleMove(state, this.player);
		for(Point myMove : moves)
		{
			PointAndValue temp = minPlay(alpha, beta, state.placePiece(myMove, this.player),iterationsLeft-1);
			v = v.value >= temp.value ? v : temp;
			if(v.value >= beta)
				return v;
			alpha = Math.max(alpha,v.value);
		}
		
		return v;
		
	}
	
	private Set<Point> getPossibleMove(BoardModel state, byte player) 
	{
		Set<Point> output = new HashSet<Point>();
		for(int j = 0; j < state.getHeight(); j++)
		{
			for (int i = 0 ; i < state.getWidth(); i++)
			{
				if(state.getSpace(i, j ) != 0)
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
			i < x + 1 || i < state.getWidth() ;
			i++)
		{
			for(int j = ( y <= 1 ? 0 : y - 1 ) ; 
			j < y + 1 || j < state.getHeight() ;
			j++)
			{
				if(state.getSpace(i,j) == 0)
					output.add(new Point(i,j));
			}
			
		}
		return output;
	}

	private PointAndValue minPlay(Integer alpha, Integer beta, BoardModel state,int iterationsLeft )
	{
		
		if (iterationsLeft == 0 || state.winner() != -1){
			int eval = heuristic(state);
			PointAndValue v = new PointAndValue();
			v.value = eval;
			v.point = state.getLastMove();
			return v;
		}
		PointAndValue v = new PointAndValue();
		byte otherPlayer = (byte) (this.player == 1 ? 2 : 1);
		v.value = Integer.MAX_VALUE; 
		Set<Point> moves = getPossibleMove(state, otherPlayer);
		for(Point myMove : moves)
		{
			PointAndValue temp = maxPlay(alpha, beta, state.placePiece(myMove, otherPlayer),iterationsLeft-1);
			v = v.value < temp.value ? v : temp;
			if(v.value <= alpha)
				return v;
			beta = Math.min(beta,v.value);
		}
	
		return v;	
	}
	
	private int heuristic(BoardModel state)
	{ 
		if (state.gravity)
			return heuristicWithGrav( state );
		else
			return heuristicNoGrav( state );
	}
	
	
	private int heuristicNoGrav(BoardModel state)
	{
		int val = 0;
		for(int j = 0; j < state.getHeight(); j++)
		{
			for (int i = 0 ; i < state.getWidth(); i++)
			{
				if(state.getSpace(i,j) != 1 && state.getSpace(i,j) != 2 )
					continue;
				else if(state.getSpace(i,j)== this.player )
				{
					val += countByMe(state, i, j , this.player);
				} else {				
					val -= countByMe(state, i, j, (byte)(this.player == 1 ? 2 : 1));
				}
			}
		}
		return val;
	}
	
	int countByMe(BoardModel state, int x, int y, byte currentPlayer)
	{
		int count = 1;
		int startX = x, startY = y;
		//count right
		while( startX < state.getWidth() 
				&& state.getSpace(startX,startY) == currentPlayer ){
			count*=2;
			++startX;
		}
				
		//count up
		startX = x;
		while( startY < state.getHeight() && 
				state.getSpace(startX,startY) == currentPlayer ){
			count*=2;
			++startY;
		}
		
		//count down-right
		startY = y;		
		while( startY > 0 &&
				startX < state.getWidth() &&
				state.getSpace(startX,startY) == currentPlayer ){
			count*=2;
			--startY;
			++startX;
		}
		
		startX = x;
		startY = y;
		//count down-left
		while( startY > 0 &&
				startX > 0 &&
				state.getSpace(startX,startY) == currentPlayer ){
			count*=2;
			--startY;
			--startX;
		}
		
		return count;
	}
	
	private int heuristicWithGrav (BoardModel state)
	{
		int val = 0;
		for(int j = state.getHeight()-1; j >= 0 ; j--)
		{
			int count = 0;
			for (int i = 0 ; i < state.getWidth(); i++)
			{
				if(state.getSpace(i,j) != 1 && state.getSpace(i,j) != 2)
				{
					count ++;
					continue;
				}
				else if(state.getSpace(i,j)== this.player )
				{
					val += countByMe(state, i, j , this.player);
				} else {				
					val -= countByMe(state, i, j, (byte)(this.player == 1 ? 2 : 1));
				}
			}
			if(count == state.getWidth())
				break;
		}
		return val;
	}

	
}
