import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		if (state.spacesLeft == state.getHeight() * state.getWidth())
		{
			return new Point( state.getWidth()/2, state.getHeight()/2);
		}
		Set<Point> moves = getPossibleMove(state, this.player);
		Set<PointAndValue> choices = new HashSet<PointAndValue>();
		for(Point myMove : moves)
		{
			PointAndValue v = minPlay(Integer.MIN_VALUE, Integer.MAX_VALUE , state.placePiece(myMove, this.player), 4 );
			v.point = myMove;
			choices.add(v);
		}
		Point best =new Point();
		int bestVal = Integer.MIN_VALUE;
		for(PointAndValue choice : choices)
		{				
			if(choice.value>bestVal || choice.point == null)
			{
				best = choice.point;
			}
		}
		return best;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
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
			if( w.value > v.value)
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
			if( w.value < v.value)
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
		for(int j = 0; j < state.getHeight(); j++)
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
			return heuristicWithGrav( state );
		else
			return EvaluateState.evaluate(state, this.player);//heuristicNoGrav( state );
	}
	
	
	private int heuristicNoGrav(BoardModel state)
	{
		int val = 1;
		byte otherPlayer = (byte)(this.player == 1 ? 2 : 1);
		List<Set<Point>> checkedPoints = new ArrayList<Set<Point>>();
		//#0 is horizontal, 1 is vertical, 2 is up right/down left, 3 is up left/down right
		for(int i=4; i-- > 0;)
			checkedPoints.add(new HashSet<Point>());

		for(int j = 0; j < state.getHeight(); j++)
		{
			for (int i = 0 ; i < state.getWidth(); i++)
			{
				if(state.getSpace(i,j) != 1 && state.getSpace(i,j) != 2 )
					continue;
				else if(state.getSpace(i,j)== this.player )
				{	
					val += countByMe(state, i, j,checkedPoints, this.player);
				}else
				{			
					val -= countByMe(state, i, j,checkedPoints, otherPlayer);
				}
			}
		}
		return val;
	}
	
	int countByMe(BoardModel state, int x, int y,List<Set<Point>> checkedPoints, byte currentPlayer)
	{
		int count = 0;
		Point p = new Point(x,y);
		if(!checkedPoints.get(0).contains(p))
			count += countHorizontal(state, x, y , checkedPoints.get(0), currentPlayer);
		if(!checkedPoints.get(1).contains(p))
			count += countVertical(state, x, y , checkedPoints.get(1), currentPlayer);
		if(!checkedPoints.get(2).contains(p))
			count += countRightDiag(state, x, y , checkedPoints.get(2), currentPlayer);
		if(!checkedPoints.get(3).contains(p))
			count += countLeftDiag(state, x, y , checkedPoints.get(3), currentPlayer);
		return count;
	}
	


	private int countLeftDiag(BoardModel state, int x, int y, Set<Point> set, byte currentPlayer) 
	{
		byte otherPlayer = (byte)(currentPlayer == 1 ? 2 : 1);
		int value = 0, blankChain = 0, blanks = 0;
		List<Integer> listOfChains = new ArrayList<Integer>();
		int currentChain =0;
		int totalSpace =0;
		int currentY = y, currentX = x;
		
		while(currentY < state.getHeight() && currentX >= 0  && 
				state.getSpace(currentX,currentY ) != otherPlayer )
		{		
			totalSpace++;
			if( state.getSpace(currentX,currentY ) == currentPlayer )
			{
				++currentChain;
				set.add(new Point(currentX, currentY));
				blankChain = 0;
			}
			else
			{
				listOfChains.add(currentChain);
				currentChain = 0;
				blanks++;
				if(++blankChain > state.getkLength())
					break;	
			}
			set.add(new Point(currentX,currentY));
			currentY++;
			currentX--;
			
		}
		
		currentChain = 0;
		blankChain = 0;
		currentY--;
		currentX++;
		
		while(currentY >= 0 && currentX < state.getWidth() 
				&& state.getSpace(currentX,currentY ) != otherPlayer)
		{
			totalSpace += (currentY < y) ? 1 : 0;
			if( state.getSpace(currentX,currentY ) == currentPlayer )
			{
				++currentChain;
				blankChain = 0;
			}
			else
			{
				if(currentY < y){
					blanks++;
					listOfChains.add(currentChain);
				}
				currentChain = 0;
				if(currentY < y && ++blankChain > state.getkLength())
					break;
			}
			set.add(new Point(currentX,currentY));
			currentY--;
			currentX++;
		}
		if(totalSpace < state.getkLength())
			return 0;
		for(Integer i : listOfChains)
			value += i * i *2;
		value +=blanks;
		return value;
	}

	private int countRightDiag(BoardModel state, int x, int y, Set<Point> set, byte currentPlayer) 
	{
		byte otherPlayer = (byte)(currentPlayer == 1 ? 2 : 1);
		int value = 0, blankChain = 0, blanks = 0;
		List<Integer> listOfChains = new ArrayList<Integer>();
		int currentChain =0;
		int totalSpace =0;
		int currentY = y, currentX = x;
		
		while(currentY < state.getHeight() && currentX < state.getWidth() && 
				state.getSpace(currentX,currentY ) != otherPlayer)
		{		
			totalSpace++;
			if( state.getSpace(currentX,currentY ) == currentPlayer )
			{
				++currentChain;
				set.add(new Point(currentX, currentY));
				blankChain = 0;
			}
			else
			{
				listOfChains.add(currentChain);
				currentChain = 0;
				blanks++;
				if(++blankChain > state.getkLength())
					break;	
			}
			set.add(new Point(currentX,currentY));
			currentY++;
			currentX++;
		}
		
		currentChain = 0;
		blankChain = 0;
		currentY--;
		currentX--;
		
		while(currentY >= 0 && currentX >= 0 
				&& state.getSpace(currentX,currentY ) != otherPlayer)
		{
			totalSpace += (currentY < y) ? 1 : 0;
			if( state.getSpace(currentX,currentY ) == currentPlayer )
			{
				++currentChain;
				blankChain = 0;
			}
			else
			{
				if(currentY < y){
					blanks++;
					listOfChains.add(currentChain);
				}
				currentChain = 0;
				if(currentY < y && ++blankChain > state.getkLength())
					break;
			}
			set.add(new Point(currentX,currentY));
			currentY--;
			currentX--;
		}
		if(totalSpace < state.getkLength())
			return 0;
		for(Integer i : listOfChains)
			value += i * i *2;
		value +=blanks;
		return value;
	}

	private int countVertical(BoardModel state, int x, int y, Set<Point> set, byte currentPlayer) 
	{
		byte otherPlayer = (byte)(currentPlayer == 1 ? 2 : 1);
		int value = 0, blankChain = 0, blanks = 0;
		List<Integer> listOfChains = new ArrayList<Integer>();
		int currentChain =0;
		int totalSpace =0;
		int currentY = y;
		
		while(currentY < state.getHeight() &&
				state.getSpace( x, currentY ) != otherPlayer)
		{		
			totalSpace++;
			if( state.getSpace( x, currentY ) == currentPlayer )
			{
				++currentChain;
				set.add(new Point(x, currentY));
				blankChain = 0;
			}
			else
			{
				listOfChains.add(currentChain);
				currentChain = 0;
				blanks++;
				if(++blankChain > state.getkLength())
					break;	
			}
			set.add(new Point(x,currentY));
			currentY++;
		}
		
		currentChain = 0;
		blankChain = 0;
		currentY--;
		
		while(currentY >= 0 &&
				state.getSpace( x, currentY ) != otherPlayer)
		{
			totalSpace += (currentY < y ) ? 1 : 0;
			if( state.getSpace( x, currentY ) == currentPlayer )
			{
				++currentChain;
				blankChain = 0;
			}
			else
			{
				if(currentY < y ){
					blanks++;
					listOfChains.add(currentChain);
				}
				currentChain = 0;
				if(currentY < y && ++blankChain > state.getkLength())
					break;
			}
			set.add(new Point(x,currentY));
			currentY--;
		}
		if(totalSpace < state.getkLength())
			return 0;
		for(Integer i : listOfChains)
			value += i * i *2;
		value +=blanks;
		return value;
	}

	private int countHorizontal( BoardModel state, int x, int y, Set<Point> set, byte currentPlayer) 
	{
		byte otherPlayer = (byte)(currentPlayer == 1 ? 2 : 1);
		int value = 0, blankChain = 0, blanks = 0;
		List<Integer> listOfChains = new ArrayList<Integer>();
		int currentChain =0;
		int totalSpace =0;
		int currentX = x;
		
		while(currentX < state.getWidth() 
				&& state.getSpace( currentX, y ) != otherPlayer)
		{		
			totalSpace++;
			if( state.getSpace( currentX, y ) == currentPlayer )
			{
				++currentChain;
				set.add(new Point(currentX,y));
				blankChain = 0;
			}
			else
			{
				listOfChains.add(currentChain);
				currentChain = 0;
				blanks++;
				if(++blankChain > state.getkLength())
					break;	
			}
			set.add(new Point(currentX,y));
			currentX++;
		}
		
		currentChain = 0;
		blankChain = 0;
		currentX--;
		
		while(currentX >= 0 && 
				state.getSpace( currentX, y ) != otherPlayer)
		{
			totalSpace += (currentX < x ) ? 1 : 0;
			if( state.getSpace( currentX, y ) == currentPlayer )
			{
				++currentChain;
				blankChain = 0;
			}
			else
			{
				if(currentX < x ){
					blanks++;
					listOfChains.add(currentChain);
				}
				currentChain = 0;
				if(currentX < x && ++blankChain > state.getkLength())
					break;
			}
			set.add(new Point(currentX,y));
			currentX--;
		}
		if(totalSpace < state.getkLength())
			return 0;
		for(Integer i : listOfChains)
			value += i * i *2;
		value += blanks;
		return value;
	}

	private int heuristicWithGrav (BoardModel state)
	{
		int val = 1;
		List<Set<Point>> checkedPoints = new ArrayList<Set<Point>>();
		//#0 is horizontal, 1 is vertical, 2 is up right/down left, 3 is up left/down right
		for(int i=4; i-- > 0;)
			checkedPoints.add(new HashSet<Point>());
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
					val += countByMe(state, i, j , checkedPoints, this.player);
								
					val -= countByMe(state, i, j, checkedPoints, (byte)(this.player == 1 ? 2 : 1));
				}
			}
			if(count == state.getWidth())
				break;
		}
		return val;
	}

	
}
